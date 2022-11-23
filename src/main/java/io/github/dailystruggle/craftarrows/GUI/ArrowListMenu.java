package io.github.dailystruggle.craftarrows.GUI;

import io.github.dailystruggle.craftarrows.Arrows.ArrowVariant;
import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Util.OutputHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ArrowListMenu {
    private static io.github.dailystruggle.craftarrows.GUI.IconMenu menu;

    private static HashMap<Integer, String> arrowPositionNames = new HashMap<>();

    public static void load() {
        arrowPositionNames = new HashMap<>();
        menu = new io.github.dailystruggle.craftarrows.GUI.IconMenu(ChatColor.GREEN + "Craft Arrows List!", 5, ArrowListMenu::click);
        int i = 0;
        for (ArrowVariant variant : CraftArrows.ArrowVariantList) {
            arrowPositionNames.put(i, variant.getName());
            menu.addButton(menu.getRow(i / 9), i % 9, variant.getRecipe().getItem(), null);
            i++;
        }
    }

    private static boolean click(Player clicker, io.github.dailystruggle.craftarrows.GUI.IconMenu menu, IconMenu.Row row, int slot, ItemStack item, ClickType clickType) {
        slot = row.row * 9 + slot;
        if (item != null) {
            String arrowName = arrowPositionNames.get(slot);
            if (arrowName != null)
                if (clickType == ClickType.LEFT) {
                    if (!clicker.hasPermission("ca.menu.recipe")) {
                        OutputHandler.PrintError(clicker, "You don't have permission to do that!");
                        return true;
                    }
                    clicker.closeInventory();
                    Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> ArrowRecipeMenu.open(clicker, arrowName), 1L);
                } else if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
                    if (!clicker.hasPermission("ca.menu.take")) {
                        OutputHandler.PrintError(clicker, "You don't have permission to do that!");
                        return true;
                    }
                    int amount = (clickType == ClickType.RIGHT) ? 1 : 64;
                    ArrowVariant variant = CraftArrows.getArrowVariantBySimpleName(arrowName);
                    if (variant == null)
                        return true;
                    ItemStack itemClone = variant.getRecipe().getItem().clone();
                    itemClone.setAmount(amount);
                    clicker.getInventory().addItem(itemClone);
                }
        }
        return true;
    }

    public static void open(Player player) {
        menu.open(player);
    }
}
