package io.github.dailystruggle.craftarrows.GUI;

import io.github.dailystruggle.craftarrows.Arrows.ArrowVariant;
import io.github.dailystruggle.craftarrows.CraftArrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class ArrowRecipeMenu {
    private static ArrayList<io.github.dailystruggle.craftarrows.GUI.IconMenu> recipeMenus;

    private static HashMap<ArrowVariant, Integer> variantPage;

    private static HashMap<Player, Integer> playerIndexes;

    public static void load() {
        recipeMenus = new ArrayList<>();
        variantPage = new HashMap<>();
        playerIndexes = new HashMap<>();
        int index = 0;
        for (ArrowVariant variant : CraftArrows.ArrowVariantList) {
            if (variant.getRecipe().isCraftable()) {
                io.github.dailystruggle.craftarrows.GUI.IconMenu menu = new io.github.dailystruggle.craftarrows.GUI.IconMenu(variant.getRecipe().getItemName(), 5, ArrowRecipeMenu::click);
                ItemStack[][] materials = variant.getRecipe().getRecipeMaterials();
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++)
                        menu.addButton(menu.getRow(1 + y), 2 + x, materials[y][x], null);
                }
                menu.addButton(menu.getRow(2), 6, variant.getRecipe().getItem(), null);
                menu.addButton(menu.getRow(2), 0, new ItemStack(Material.PAPER), ChatColor.YELLOW + "Left");
                menu.addButton(menu.getRow(2), 8, new ItemStack(Material.PAPER), ChatColor.YELLOW + "Right");
                recipeMenus.add(menu);
                variantPage.put(variant, Integer.valueOf(index));
                index++;
            }
        }
    }

    private static boolean click(Player clicker, io.github.dailystruggle.craftarrows.GUI.IconMenu menu, io.github.dailystruggle.craftarrows.GUI.IconMenu.Row row, int slot, ItemStack item, ClickType clickType) {
        if (item != null &&
                row.row == 2) {
            int newIndex = playerIndexes.get(clicker).intValue();
            if (slot == 0) {
                newIndex--;
                if (newIndex < 0)
                    newIndex = recipeMenus.size() - 1;
                open(clicker, Integer.valueOf(newIndex));
            } else {
                newIndex++;
                if (slot == 8 && newIndex >= recipeMenus.size())
                    newIndex = 0;
            }
            int index = newIndex;
            Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> open(clicker, Integer.valueOf(index)), 1L);
        }
        return true;
    }

    public static void open(Player player) {
        open(player, playerIndexes.getOrDefault(player, Integer.valueOf(0)));
    }

    public static void open(Player player, String name) {
        ArrowVariant variant = CraftArrows.getArrowVariantBySimpleName(name);
        int page = 0;
        if (variant != null)
            page = variantPage.getOrDefault(variant, Integer.valueOf(0)).intValue();
        open(player, Integer.valueOf(page));
    }

    private static void open(Player player, Integer index) {
        playerIndexes.put(player, index);
        recipeMenus.get(index.intValue()).open(player);
    }
}
