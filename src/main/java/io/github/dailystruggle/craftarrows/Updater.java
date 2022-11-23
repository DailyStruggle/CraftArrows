package io.github.dailystruggle.craftarrows;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Updater {
    private static final String TITLE_VALUE = "name";

    private static final String LINK_VALUE = "downloadUrl";

    private static final String TYPE_VALUE = "releaseType";

    private static final String VERSION_VALUE = "gameVersion";

    private static final String QUERY = "/servermods/files?projectIds=";

    private static final String HOST = "https://api.curseforge.com";

    private static final String USER_AGENT = "Updater (by Gravity)";

    private static final String DELIMETER = "^v|[\\s_-]v";

    private static final String[] NO_UPDATE_TAG = new String[]{"-DEV", "-PRE", "-SNAPSHOT"};

    private static final int BYTE_SIZE = 1024;

    private static final String API_KEY_CONFIG_KEY = "api-key";

    private static final String DISABLE_CONFIG_KEY = "disable";

    private static final String API_KEY_DEFAULT = "PUT_API_KEY_HERE";

    private static final boolean DISABLE_DEFAULT = false;

    private final Plugin plugin;

    private final UpdateType type;

    private final boolean announce;

    private final File file;

    private final File updateFolder;

    private final UpdateCallback callback;

    private int id = -1;

    private String apiKey = null;

    private String versionName;

    private String versionLink;

    private String versionType;

    private String versionGameVersion;

    private URL url;

    private Thread thread;

    private UpdateResult result = UpdateResult.SUCCESS;

    public Updater(Plugin plugin, File file, UpdateType type) {
        this(plugin, file, type, null, true);
    }

    public Updater(Plugin plugin, File file, UpdateType type, UpdateCallback callback) {
        this(plugin, file, type, callback, false);
    }

    public Updater(Plugin plugin, File file, UpdateType type, UpdateCallback callback, boolean announce) {
        this.plugin = plugin;
        this.type = type;
        this.announce = announce;
        this.file = file;
        this.id = 288895;
        this.updateFolder = this.plugin.getServer().getUpdateFolderFile();
        this.callback = callback;
        File pluginFile = this.plugin.getDataFolder().getParentFile();
        File updaterFile = new File(pluginFile, "Updater");
        File updaterConfigFile = new File(updaterFile, "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )\nIf you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below.\nSome updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
        config.addDefault("api-key", "PUT_API_KEY_HERE");
        config.addDefault("disable", false);
        if (!updaterFile.exists())
            fileIOOrError(updaterFile, updaterFile.mkdir(), true);
        boolean createFile = !updaterConfigFile.exists();
        try {
            if (createFile) {
                fileIOOrError(updaterConfigFile, updaterConfigFile.createNewFile(), true);
                config.options().copyDefaults(true);
                config.save(updaterConfigFile);
            } else {
                config.load(updaterConfigFile);
            }
        } catch (Exception e) {
            String message;
            if (createFile) {
                message = "The updater could not create configuration at " + updaterFile.getAbsolutePath();
            } else {
                message = "The updater could not load configuration at " + updaterFile.getAbsolutePath();
            }
            this.plugin.getLogger().log(Level.SEVERE, message, e);
        }
        if (config.getBoolean("disable")) {
            this.result = UpdateResult.DISABLED;
            return;
        }
        String key = config.getString("api-key");
        if ("PUT_API_KEY_HERE".equalsIgnoreCase(key) || "".equals(key))
            key = null;
        this.apiKey = key;
        try {
            this.url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + this.id);
        } catch (MalformedURLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "The project ID provided for updating, " + this.id + " is invalid.", e);
            this.result = UpdateResult.FAIL_BADID;
        }
        if (this.result != UpdateResult.FAIL_BADID) {
            this.thread = new Thread(new UpdateRunnable());
            this.thread.start();
        } else {
            runUpdater();
        }
    }

    public UpdateResult getResult() {
        waitForThread();
        return this.result;
    }

    public ReleaseType getLatestType() {
        waitForThread();
        if (this.versionType != null)
            for (ReleaseType type : ReleaseType.values()) {
                if (this.versionType.equalsIgnoreCase(type.name()))
                    return type;
            }
        return null;
    }

    public String getLatestGameVersion() {
        waitForThread();
        return this.versionGameVersion;
    }

    public String getLatestName() {
        waitForThread();
        return this.versionName;
    }

    public String getLatestFileLink() {
        waitForThread();
        return this.versionLink;
    }

    private void waitForThread() {
        if (this.thread != null && this.thread.isAlive())
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
    }

    private void saveFile(String file) {
        File folder = this.updateFolder;
        deleteOldFiles();
        if (!folder.exists())
            fileIOOrError(folder, folder.mkdir(), true);
        downloadFile();
        File dFile = new File(folder.getAbsolutePath(), file);
        if (dFile.getName().endsWith(".zip"))
            unzip(dFile.getAbsolutePath());
        if (this.announce)
            this.plugin.getLogger().info("Finished updating.");
    }

    private void downloadFile() {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            URL fileUrl = followRedirects(this.versionLink);
            int fileLength = fileUrl.openConnection().getContentLength();
            in = new BufferedInputStream(fileUrl.openStream());
            fout = new FileOutputStream(new File(this.updateFolder, this.file.getName()));
            byte[] data = new byte[1024];
            if (this.announce)
                this.plugin.getLogger().info("About to download a new update: " + this.versionName);
            long downloaded = 0L;
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                downloaded += count;
                fout.write(data, 0, count);
                int percent = (int) (downloaded * 100L / fileLength);
                if (this.announce && percent % 10 == 0)
                    this.plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
            }
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.WARNING, "The auto-updater tried to download a new update, but was unsuccessful.", ex);
            this.result = UpdateResult.FAIL_DOWNLOAD;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, null, ex);
            }
            try {
                if (fout != null)
                    fout.close();
            } catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }
    }

    private URL followRedirects(String location) throws IOException {
        HttpURLConnection conn;
        while (true) {
            URL base, next;
            String redLoc;
            URL resourceUrl = new URL(location);
            conn = (HttpURLConnection) resourceUrl.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0...");
            switch (conn.getResponseCode()) {
                case 301:
                case 302:
                    redLoc = conn.getHeaderField("Location");
                    base = new URL(location);
                    next = new URL(base, redLoc);
                    location = next.toExternalForm();
                    continue;
            }
            break;
        }
        return conn.getURL();
    }

    private void deleteOldFiles() {
        File[] list = listFilesOrError(this.updateFolder);
        for (File xFile : list) {
            if (xFile.getName().endsWith(".zip"))
                fileIOOrError(xFile, xFile.mkdir(), true);
        }
    }

    private void unzip(String file) {
        File fSourceZip = new File(file);
        try {
            String zipPath = file.substring(0, file.length() - 4);
            ZipFile zipFile = new ZipFile(fSourceZip);
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                File destinationFilePath = new File(zipPath, entry.getName());
                fileIOOrError(destinationFilePath.getParentFile(), destinationFilePath.getParentFile().mkdirs(), true);
                if (!entry.isDirectory()) {
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    byte[] buffer = new byte[1024];
                    FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
                    int b;
                    while ((b = bis.read(buffer, 0, 1024)) != -1)
                        bos.write(buffer, 0, b);
                    bos.flush();
                    bos.close();
                    bis.close();
                    String name = destinationFilePath.getName();
                    if (name.endsWith(".jar") && pluginExists(name)) {
                        File output = new File(this.updateFolder, name);
                        fileIOOrError(output, destinationFilePath.renameTo(output), true);
                    }
                }
            }
            zipFile.close();
            moveNewZipFiles(zipPath);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
            this.result = UpdateResult.FAIL_DOWNLOAD;
        } finally {
            fileIOOrError(fSourceZip, fSourceZip.delete(), false);
        }
    }

    private void moveNewZipFiles(String zipPath) {
        File[] list = listFilesOrError(new File(zipPath));
        for (File dFile : list) {
            if (dFile.isDirectory() && pluginExists(dFile.getName())) {
                File oFile = new File(this.plugin.getDataFolder().getParent(), dFile.getName());
                File[] dList = listFilesOrError(dFile);
                File[] oList = listFilesOrError(oFile);
                for (File cFile : dList) {
                    boolean found = false;
                    for (File xFile : oList) {
                        if (xFile.getName().equals(cFile.getName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        File output = new File(oFile, cFile.getName());
                        fileIOOrError(output, cFile.renameTo(output), true);
                    } else {
                        fileIOOrError(cFile, cFile.delete(), false);
                    }
                }
            }
            fileIOOrError(dFile, dFile.delete(), false);
        }
        File zip = new File(zipPath);
        fileIOOrError(zip, zip.delete(), false);
    }

    private boolean pluginExists(String name) {
        File[] plugins = listFilesOrError(new File("plugins"));
        for (File file : plugins) {
            if (file.getName().equals(name))
                return true;
        }
        return false;
    }

    private boolean versionCheck() {
        String title = this.versionName;
        if (this.type != UpdateType.NO_VERSION_CHECK) {
            String localVersion = this.plugin.getDescription().getVersion();
            if ((title.split("^v|[\\s_-]v")).length >= 2) {
                String remoteVersion = title.split("^v|[\\s_-]v")[(title.split("^v|[\\s_-]v")).length - 1].split(" ")[0];
                if (hasTag(localVersion) || !shouldUpdate(localVersion, remoteVersion)) {
                    this.result = UpdateResult.NO_UPDATE;
                    return false;
                }
            } else {
                String authorInfo = this.plugin.getDescription().getAuthors().isEmpty() ? "" : (" (" + this.plugin.getDescription().getAuthors().get(0) + ")");
                this.plugin.getLogger().warning("The author of this plugin" + authorInfo + " has misconfigured their Auto Update system");
                this.plugin.getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
                this.plugin.getLogger().warning("Please notify the author of this error.");
                this.result = UpdateResult.FAIL_NOVERSION;
                return false;
            }
        }
        return true;
    }

    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return (!localVersion.equalsIgnoreCase(remoteVersion) && remoteIsNewer(localVersion, remoteVersion));
    }

    private boolean remoteIsNewer(String localVersion, String remoteVersion) {
        String[] local = localVersion.split("\\.");
        String[] remote = remoteVersion.split("\\.");
        if (local.length == remote.length)
            for (int i = 0; i < local.length; i++) {
                if (Integer.parseInt(local[i]) < Integer.parseInt(remote[i]))
                    return true;
                if (Integer.parseInt(local[i]) > Integer.parseInt(remote[i]))
                    return false;
            }
        return false;
    }

    private boolean hasTag(String version) {
        for (String string : NO_UPDATE_TAG) {
            if (version.contains(string))
                return true;
        }
        return false;
    }

    private boolean read() {
        try {
            URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);
            if (this.apiKey != null)
                conn.addRequestProperty("X-API-Key", this.apiKey);
            conn.addRequestProperty("User-Agent", "Updater (by Gravity)");
            conn.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            JSONArray array = (JSONArray) JSONValue.parse(response);
            if (array.isEmpty()) {
                this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }
            JSONObject latestUpdate = (JSONObject) array.get(array.size() - 1);
            this.versionName = (String) latestUpdate.get("name");
            this.versionLink = (String) latestUpdate.get("downloadUrl");
            this.versionType = (String) latestUpdate.get("releaseType");
            this.versionGameVersion = (String) latestUpdate.get("gameVersion");
            return true;
        } catch (IOException e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
                this.plugin.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
                this.plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
                this.result = UpdateResult.FAIL_APIKEY;
            } else {
                this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
                this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
                this.result = UpdateResult.FAIL_DBO;
            }
            this.plugin.getLogger().log(Level.SEVERE, null, e);
            return false;
        }
    }

    private void fileIOOrError(File file, boolean result, boolean create) {
        if (!result)
            this.plugin.getLogger().severe("The updater could not " + (create ? "create" : "delete") + " file at: " + file.getAbsolutePath());
    }

    private File[] listFilesOrError(File folder) {
        File[] contents = folder.listFiles();
        if (contents == null) {
            this.plugin.getLogger().severe("The updater could not access files at: " + this.updateFolder.getAbsolutePath());
            return new File[0];
        }
        return contents;
    }

    private void runUpdater() {
        if (this.url != null && read() && versionCheck())
            if (this.versionLink != null && this.type != UpdateType.NO_DOWNLOAD) {
                String name = this.file.getName();
                if (this.versionLink.endsWith(".zip"))
                    name = this.versionLink.substring(this.versionLink.lastIndexOf("/") + 1);
                saveFile(name);
            } else {
                this.result = UpdateResult.UPDATE_AVAILABLE;
            }
        if (this.callback != null)
            (new BukkitRunnable() {
                public void run() {
                    Updater.this.runCallback();
                }
            }).runTask(this.plugin);
    }

    private void runCallback() {
        this.callback.onFinish(this);
    }

    public enum UpdateResult {
        SUCCESS, NO_UPDATE, DISABLED, FAIL_DOWNLOAD, FAIL_DBO, FAIL_NOVERSION, FAIL_BADID, FAIL_APIKEY, UPDATE_AVAILABLE
    }

    public enum UpdateType {
        DEFAULT, NO_VERSION_CHECK, NO_DOWNLOAD
    }

    public enum ReleaseType {
        ALPHA, BETA, RELEASE
    }

    public interface UpdateCallback {
        void onFinish(Updater param1Updater);
    }

    private class UpdateRunnable implements Runnable {
        private UpdateRunnable() {
        }

        public void run() {
            Updater.this.runUpdater();
        }
    }
}
