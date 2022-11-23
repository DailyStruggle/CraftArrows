package io.github.dailystruggle.craftarrows.Objects;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Util.ConfigHelper;
import io.github.dailystruggle.craftarrows.Util.OutputHandler;
import io.github.dailystruggle.craftarrows.Util.TryParse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrowRecipe {
    private final HashMap<Character, Material> recipeMaterials = new HashMap<>();

    private final HashMap<Character, Byte> recipeData = new HashMap<>();
    private final ItemStack item = new ItemStack(Material.ARROW);
    private final String nameGarbage = "" + ChatColor.BOLD + ChatColor.UNDERLINE + ChatColor.ITALIC;
    private boolean isEnabled = false;
    private boolean isCraftable = true;
    private String itemName = "";
    private List<String> description = new ArrayList<>();
    private Integer craftCount;
    private String recipe1 = "";
    private String recipe2 = "";
    private String recipe3 = "";

    public ArrowRecipe(FileConfiguration config, String prefix, String name) {
        load(config, prefix, name);
    }

    public String getItemName() {
        return this.itemName;
    }

    public ItemStack getItem() {
        return this.item.clone();
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean isCraftable() {
        return this.isCraftable;
    }

    private void load(FileConfiguration config, String prefix, String name) {
        prefix = prefix + "." + name;
        loadConfigValues(config, prefix, name);
        loadRecipeData(config, prefix, name);
        makeItem();
        if (this.isEnabled)
            makeRecipe(name);
    }

    private void loadConfigValues(FileConfiguration config, String prefix, String name) {
        this.isEnabled = config.getBoolean(prefix + ".Enabled");
        if (ConfigHelper.ConfigContainsPath(config, prefix + ".Craftable")) {
            this.isCraftable = config.getBoolean(prefix + ".Craftable");
        } else {
            config.set(prefix + ".Craftable", true);
        }
        this.recipe1 = config.getString(prefix + ".Shape-1");
        this.recipe2 = config.getString(prefix + ".Shape-2");
        this.recipe3 = config.getString(prefix + ".Shape-3");
        this.itemName = config.getString(prefix + ".Name").replace("&", "§") + this.nameGarbage;
        this.description = new ArrayList<>();
        Object obj = config.get(prefix + ".Desc");
        if (obj instanceof String) {
            this.description.add(ChatColor.translateAlternateColorCodes('&', (String) obj));
        } else {
            for (String line : config.getStringList(prefix + ".Desc"))
                this.description.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        this.craftCount = Integer.valueOf(config.getInt(prefix + ".Amount"));
    }

    private void loadRecipeData(FileConfiguration config, String prefix, String name) {
        List<String> shapeChars = config.getStringList(prefix + ".ShapeChars");
        List<String> charMats = config.getStringList(prefix + ".CharMats");
        if (shapeChars.size() == charMats.size()) {
            for (int i = 0; i < shapeChars.size(); i++) {
                String line = charMats.get(i);
                if (line.contains(",")) {
                    String[] split = charMats.get(0).split(",");
                    if (TryParse.parseByte(split[1]))
                        this.recipeData.put(Character.valueOf(shapeChars.get(i).charAt(0)), Byte.valueOf(Byte.parseByte(split[1])));
                    if (TryParse.parseMaterial(split[0])) {
                        this.recipeMaterials.put(Character.valueOf(shapeChars.get(i).charAt(0)), Material.valueOf(split[0]));
                    } else {
                        OutputHandler.PrintError("Invalid material, " + charMats.get(i) + ", in recipe, " + name);
                        OutputHandler.PrintError("Recipe disabled");
                        return;
                    }
                } else if (TryParse.parseMaterial(charMats.get(i))) {
                    this.recipeMaterials.put(Character.valueOf(shapeChars.get(i).charAt(0)), Material.valueOf(charMats.get(i)));
                } else {
                    OutputHandler.PrintError("Invalid material, " + charMats.get(i) + ", in recipe, " + name);
                    OutputHandler.PrintError("Recipe disabled");
                    return;
                }
            }
        } else {
            OutputHandler.PrintError("Recipe for, " + name + ", does not work");
            OutputHandler.PrintError("Mistmatched items between, ShapeChars and CharMats");
            OutputHandler.PrintError("Recipe disabled");
        }
    }

    private void makeItem() {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(this.itemName);
        meta.setLore(this.description);
        this.item.setItemMeta(meta);
        this.item.setAmount(this.craftCount.intValue());
    }

    private void makeRecipe(String name) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(CraftArrows.instance, name), this.item);
        for (Character cha : this.recipeMaterials.keySet()) {
            if (this.recipeMaterials.get(cha) == Material.AIR) {
                this.recipe1.replace(cha.charValue(), ' ');
                this.recipe2.replace(cha.charValue(), ' ');
                this.recipe3.replace(cha.charValue(), ' ');
            }
        }
        recipe.shape(this.recipe1, this.recipe2, this.recipe3);
        for (Character cha : this.recipeMaterials.keySet()) {
            if (this.recipeData.containsKey(cha)) {
                recipe.setIngredient(cha.charValue(), new MaterialData(this.recipeMaterials.get(cha), this.recipeData.get(cha).byteValue()));
                continue;
            }
            recipe.setIngredient(cha.charValue(), this.recipeMaterials.get(cha));
        }
        if (this.isCraftable)
            CraftArrows.AddRecipe(recipe);
    }

    public ItemStack[][] getRecipeMaterials() {
        ItemStack[][] recipeMaterialArray = new ItemStack[3][3];
        recipeMaterialArray[0] = convertLineToItemStacks(this.recipe1);
        recipeMaterialArray[1] = convertLineToItemStacks(this.recipe2);
        recipeMaterialArray[2] = convertLineToItemStacks(this.recipe3);
        return recipeMaterialArray;
    }

    private ItemStack[] convertLineToItemStacks(String line) {
        ItemStack[] newLine = new ItemStack[3];
        for (int i = 0; i < 3; i++) {
            char c = line.charAt(i);
            if (this.recipeData.containsKey(Character.valueOf(c))) {
                newLine[i] = new ItemStack(this.recipeMaterials.get(Character.valueOf(c)), 1, (short) 0, this.recipeData.get(Character.valueOf(c)));
            } else {
                newLine[i] = new ItemStack(this.recipeMaterials.get(Character.valueOf(c)));
            }
        }
        return newLine;
    }
}
