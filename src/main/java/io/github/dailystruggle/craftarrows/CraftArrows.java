package io.github.dailystruggle.craftarrows;

import io.github.dailystruggle.craftarrows.Arrows.*;
import io.github.dailystruggle.craftarrows.Commands.executors.CraftArrowsCommandExecutor;
import io.github.dailystruggle.craftarrows.Commands.tabcompleters.CraftArrowsTabCompleter;
import io.github.dailystruggle.craftarrows.GUI.ArrowListMenu;
import io.github.dailystruggle.craftarrows.GUI.ArrowRecipeMenu;
import io.github.dailystruggle.craftarrows.Listener.CraftArrowListener;
import io.github.dailystruggle.craftarrows.Npc.NoCraftArrowTrait;
import io.github.dailystruggle.craftarrows.Objects.ArrowDropData;
import io.github.dailystruggle.craftarrows.Objects.Properties;
import io.github.dailystruggle.craftarrows.Objects.RandomCollection;
import io.github.dailystruggle.craftarrows.Util.ConfigHelper;
import io.github.dailystruggle.craftarrows.Util.NmsHelper;
import io.github.dailystruggle.craftarrows.Util.OutputHandler;
import io.github.dailystruggle.craftarrows.WorldGuard.NoCraftArrowFlag;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.util.*;

public class CraftArrows extends JavaPlugin {
    public static CraftArrows instance;
    public static ArrayList<ArrowVariant> ArrowVariantList;
    public static boolean WorldGuardEnabled = false;
    private static HashMap<String, ArrowVariant> arrowVariantsByItemName;

    private static HashMap<String, ArrowVariant> arrowVariantsByName;

    private static RandomCollection<ItemStack> randomArrowDrops;

    private static RandomCollection<ArrowVariant> randomArrowVariant;
    private static ArrayList<Recipe> arrowRecipes = new ArrayList<>();
    private YamlConfiguration skeletonArrows;
    private ArrowDropData arrowDropData;

    public static void reloadPlugin() {
        instance.RemoveAllRecipes();
        HandlerList.unregisterAll(instance);
        instance.startup(false);
    }

    public static ArrowVariant getArrowVariantForItemName(String name) {
        name = ChatColor.stripColor(name).toLowerCase();
        if (arrowVariantsByItemName.containsKey(name))
            return arrowVariantsByItemName.get(name);
        return null;
    }

    public static ArrowVariant getArrowVariantBySimpleName(String name) {
        name = ChatColor.stripColor(name).toLowerCase();
        return arrowVariantsByName.get(name);
    }

    public static List<ArrowVariant> getAllArrowVariants() {
        return ArrowVariantList;
    }

    public static ItemStack getRandomArrowDrop() {
        ItemStack drop = randomArrowDrops.next().clone();
        drop.setAmount(instance.arrowDropData.getValue());
        return drop;
    }

    public static ArrowVariant getRandomArrowVariant() {
        return randomArrowVariant.next();
    }

    public static void AddRecipe(Recipe recipe) {
        Bukkit.addRecipe(recipe);
        arrowRecipes.add(recipe);
    }

    private static void RemoveRecipe(Recipe r) {
        List<Recipe> backup = new ArrayList<>();
        Iterator<Recipe> a = instance.getServer().recipeIterator();
        while (a.hasNext()) {
            Recipe recipe = a.next();
            ItemStack result = recipe.getResult();
            if (!result.isSimilar(r.getResult()))
                backup.add(recipe);
        }
        instance.getServer().clearRecipes();
        for (a = backup.iterator(); a.hasNext(); ) {
            Recipe recipe = a.next();
            instance.getServer().addRecipe(recipe);
        }
    }

    public void onEnable() {
        int version = NmsHelper.getSimpleVersion();
        if (version < 13) {
            OutputHandler.PrintError("You are running a version less than 1.13. Please download the 1.12 version");
            return;
        }
        startup(true);
    }

    private void startup(boolean onEnable) {
        instance = this;
        ArrowVariantList = new ArrayList<>();
        arrowVariantsByItemName = new HashMap<>();
        arrowVariantsByName = new HashMap<>();
        randomArrowDrops = new RandomCollection<>();
        randomArrowVariant = new RandomCollection<>();
        firstRun();
        loadConfig();
        checkForNewArrows();
        loadArrows();
        loadArrowDropData();
        Bukkit.getPluginManager().registerEvents(new CraftArrowListener(), this);
        setupCommands();
        ArrowListMenu.load();
        ArrowRecipeMenu.load();
        if (onEnable)
            Bukkit.getScheduler().scheduleSyncDelayedTask(this,()->hookCitizens());
        Plugin worldGuard = getServer().getPluginManager().getPlugin("WorldGuard");
        WorldGuardEnabled = (worldGuard != null && worldGuard.isEnabled());
    }

    public void onLoad() {
        OutputHandler.PrintInfo("Lorinths Craft Arrows : On Load");
        hookWorldGuard();
    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        Properties.UsePermissions = config.getBoolean("UsePermissions");
        Properties.InfinityBowWorks = config.getBoolean("InfinityBowWorks");
        Properties.SkeletonCanShootArrow = config.getBoolean("SkeletonCanShootArrow");
        Properties.SkeletonsDropArrows = config.getBoolean("SkeletonsDropArrows");
        boolean changed = false;
        if (ConfigHelper.ConfigContainsPath(config, "SkeletonsDropArrowsOnlyOnPlayerKill")) {
            Properties.SkeletonsDropArrowsOnlyOnPlayerKill = config.getBoolean("SkeletonsDropArrowsOnlyOnPlayerKill");
        } else {
            config.set("SkeletonsDropArrowsOnlyOnPlayerKill", true);
            changed = true;
        }
        if (changed)
            try {
                config.save(configFile);
                loadConfig();
            } catch (Exception e) {
                OutputHandler.PrintException("Error saving config on startup", e);
            }
    }

    private void setupCommands() {
        PluginCommand craftArrowsCmd = Objects.requireNonNull(getCommand("ca"));
        craftArrowsCmd.setExecutor(new CraftArrowsCommandExecutor());
        craftArrowsCmd.setTabCompleter(new CraftArrowsTabCompleter());
    }

    private void firstRun() {
        try {
            File arrowEffects = new File(getDataFolder(), "ArrowEffects.yml");
            File skeletonArrows = new File(getDataFolder(), "SkeletonArrows.yml");
            File config = new File(getDataFolder(), "config.yml");
            if (!arrowEffects.exists()) {
                boolean mkdirs = arrowEffects.getParentFile().mkdirs();
                if(!mkdirs) throw new IllegalStateException();
                copy(getResource("ArrowEffects.yml"), arrowEffects);
            }
            if (!skeletonArrows.exists()) {
                boolean mkdirs = skeletonArrows.getParentFile().mkdirs();
                if(!mkdirs) throw new IllegalStateException();
                copy(getResource("SkeletonArrows.yml"), skeletonArrows);
            }
            if (!config.exists()) {
                boolean mkdirs = config.getParentFile().mkdirs();
                if(!mkdirs) throw new IllegalStateException();
                copy(getResource("config.yml"), config);
            }
        } catch (Exception exception) {
            OutputHandler.PrintException("Problem with first run", exception);
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map.Entry<Integer, Integer> getShootAndDropChance(String name) {
        return new AbstractMap.SimpleEntry<>(
                this.skeletonArrows.getInt("ShootChance." + name),
                this.skeletonArrows.getInt("DropChance." + name));
    }

    private void checkForNewArrows() {
        File file = new File(getDataFolder(), "ArrowEffects.yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "ArrowEffects.yml"));
            Reader textResource = Objects.requireNonNull(getTextResource("ArrowEffects.yml"));
            YamlConfiguration yamlConfiguration1 = YamlConfiguration.loadConfiguration(textResource);

            ConfigurationSection recipes = config.getConfigurationSection("Recipes");
            if(recipes!=null) {
                Set<String> currentRecipes = recipes.getKeys(false);
                ConfigurationSection recipes1 = yamlConfiguration1.getConfigurationSection("Recipes");
                if(recipes1!=null) {
                    Set<String> updatedRecipes = recipes1.getKeys(false);
                    for (String key : updatedRecipes) {
                        if (!currentRecipes.contains(key))
                            setConfigValues(yamlConfiguration1, config, "Recipes." + key);
                    }
                }
            }
            config.setDefaults(yamlConfiguration1);
            try {
                config.save(file);
            } catch (Exception exception) {
                OutputHandler.PrintException("Error while updating config with new arrows", exception);
            }
        }
    }

    private void setConfigValues(FileConfiguration from, FileConfiguration to, String keyPath) {
        ConfigurationSection section = from.getConfigurationSection(keyPath);
        if(section == null) {
            throw new IllegalArgumentException("expected a valid keypath. Received - " + keyPath);
        }
        for (String key : section.getKeys(true)) {
            if (from.get(keyPath + "." + key) != null)
                to.set(keyPath + "." + key, from.get(keyPath + "." + key));
        }
    }

    private void loadArrows() {
        File arrowFile = new File(getDataFolder(), "ArrowEffects.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(arrowFile);
        File skeletonFile = new File(getDataFolder(), "SkeletonArrows.yml");
        this.skeletonArrows = YamlConfiguration.loadConfiguration(skeletonFile);
        addVariant(new BatArrowVariant(config));
        addVariant(new PotionArrowVariant(config, "Blinding", PotionEffectType.BLINDNESS));
        addVariant(new SplashPotionArrow(config, "BlindingAoe", PotionEffectType.BLINDNESS));
        addVariant(new BloodArrowVariant(config));
        addVariant(new PotionArrowVariant(config, "Confusion", PotionEffectType.CONFUSION));
        addVariant(new SplashPotionArrow(config, "ConfusionAoe", PotionEffectType.CONFUSION));
        addVariant(new CoolingArrowVariant(config));
        addVariant(new PotionArrowVariant(config, "Crippling", PotionEffectType.SLOW));
        addVariant(new PotionArrowVariant(config, "CripplingAoe", PotionEffectType.SLOW));
        addVariant(new ExplosiveArrowVariant(config));
        addVariant(new FireArrowVariant(config));
        addVariant(new ForcefieldArrowVariant(config));
        addVariant(new HomingArrowVariant(config));
        addVariant(new IceArrowVariant(config));
        addVariant(new JackOArrow(config));
        addVariant(new LadderArrowVariant(config));
        addVariant(new LanceArrow(config));
        addVariant(new PotionArrowVariant(config, "Levitation", PotionEffectType.LEVITATION));
        addVariant(new SplashPotionArrow(config, "LevitationAoe", PotionEffectType.LEVITATION));
        addVariant(new LightningArrowVariant(config));
        addVariant(new MedicArrowVariant(config));
        addVariant(new MultishotArrowVariant(config));
        addVariant(new NetArrowVariant(config));
        addVariant(new PiercingArrowVariant(config));
        addVariant(new PotionArrowVariant(config, "Poison", PotionEffectType.POISON));
        addVariant(new PullArrowVariant(config));
        addVariant(new PushArrowVariant(config));
        addVariant(new RazorArrowVariant(config));
        addVariant(new ShuffleArrowVariant(config));
        addVariant(new SniperArrowVariant(config));
        addVariant(new SoundArrowVariant(config));
        addVariant(new TeleportArrowVariant(config));
        addVariant(new TorchArrowVariant(config));
        addVariant(new TorpedoArrowVariant(config));
        addVariant(new TrickOrTreatArrow(config));
        addVariant(new VortexArrowVariant(config));
        addVariant(new VolleyArrowVariant(config));
        addVariant(new WallingArrowVariant(config));
        addVariant(new WaterArrowVariant(config));
        addVariant(new PotionArrowVariant(config, "Weakness", PotionEffectType.WEAKNESS));
        addVariant(new WitherArrowVariant(config));
        try {
            config.save(arrowFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        randomArrowVariant.add(this.skeletonArrows.getInt("ShootChance.Normal"), null);
        randomArrowDrops.add(this.skeletonArrows.getInt("DropChance.Normal"), new ItemStack(Material.ARROW));
    }

    private void addVariant(ArrowVariant variant) {
        if (variant.getRecipe().isEnabled()) {
            ArrowVariantList.add(variant);
            String arrowName = variant.getRecipe().getItemName().trim();
            arrowName = ChatColor.stripColor(arrowName).toLowerCase();
            arrowVariantsByItemName.put(arrowName, variant);
            arrowVariantsByName.put(variant.getName().trim().toLowerCase(), variant);
            Map.Entry<Integer, Integer> chances = getShootAndDropChance(variant.getName().trim());
            randomArrowVariant.add(chances.getKey(), variant);
            randomArrowDrops.add(chances.getValue(), variant.getRecipe().getItem());
        }
    }

    private void loadArrowDropData() {
        this.arrowDropData = new ArrowDropData();
        this.arrowDropData.Amount = this.skeletonArrows.getInt("Amount");
        this.arrowDropData.DropFixedAmount = this.skeletonArrows.getBoolean("DropFixedAmount");
        this.arrowDropData.Min = this.skeletonArrows.getInt("Min");
        this.arrowDropData.Max = this.skeletonArrows.getInt("Max");
    }

    private void hookCitizens() {
        Plugin citizens = getServer().getPluginManager().getPlugin("Citizens");
        if (citizens == null || !citizens.isEnabled() || !(citizens instanceof CitizensPlugin))
            return;
        CitizensAPI.setImplementation((CitizensPlugin) citizens);
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NoCraftArrowTrait.class));
    }

    private void hookWorldGuard() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") == null)
            return;
        NoCraftArrowFlag.onLoad();
    }

    private void RemoveAllRecipes() {
        for (Recipe recipe : arrowRecipes)
            RemoveRecipe(recipe);
        arrowRecipes = new ArrayList<>();
    }
}
