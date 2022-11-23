package io.github.dailystruggle.craftarrows.GUI;

import io.github.dailystruggle.craftarrows.CraftArrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IconMenu implements Listener {
    private final String name;
    private final int size;
    private final onClick click;
    List<String> viewing = new ArrayList<>();
    private ItemStack[] items;

    public IconMenu(String name, int size, onClick click) {
        this.name = name;
        this.size = size * 9;
        this.items = new ItemStack[this.size];
        this.click = click;
        Bukkit.getPluginManager().registerEvents(this, CraftArrows.instance);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player p : getViewers())
            close(p);
    }

    public IconMenu open(Player p) {
        p.openInventory(getInventory(p));
        this.viewing.add(p.getName());
        return this;
    }

    private Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, this.size, this.name);
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] != null)
                inv.setItem(i, this.items[i]);
        }
        return inv;
    }

    private IconMenu close(Player p) {
        if (p.getOpenInventory().getTitle().equals(this.name))
            p.closeInventory();
        return this;
    }

    private List<Player> getViewers() {
        List<Player> viewers = new ArrayList<>();
        for (String s : this.viewing)
            viewers.add(Bukkit.getPlayer(s));
        return viewers;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.viewing.contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            Row row = getRowFromSlot(event.getSlot());
            if (!this.click.click(p, this, row, event.getSlot() - row.getRow() * 9, event.getCurrentItem(), event.getClick()))
                close(p);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        this.viewing.remove(event.getPlayer().getName());
    }

    public IconMenu addButton(Row row, int position, ItemStack item, String name, String... lore) {
        this.items[row.getRow() * 9 + position] = getItem(item, name, lore);
        return this;
    }

    private Row getRowFromSlot(int slot) {
        return new Row(slot / 9, this.items);
    }

    public Row getRow(int row) {
        return new Row(row, this.items);
    }

    public void clear(Player p) {
        this.items = new ItemStack[this.size];
        open(p);
    }

    private ItemStack getItem(ItemStack item, String name, String... lore) {
        if (name != null || (lore != null && lore.length > 0)) {
            ItemMeta im = item.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            if (name != null)
                im.setDisplayName(name);
            if (lore.length > 0)
                im.setLore(Arrays.asList(lore));
            item.setItemMeta(im);
        }
        return item;
    }

    public interface onClick {
        boolean click(Player param1Player, IconMenu param1IconMenu, Row param1Row, int param1Int, ItemStack param1ItemStack, ClickType param1ClickType);
    }

    public class Row {
        private final ItemStack[] rowItems = new ItemStack[9];
        int row;

        private Row(int row, ItemStack[] items) {
            this.row = row;
            int j = 0;
            try {
                for (int i = row * 9; i < row * 9 + 9; i++) {
                    this.rowItems[j] = items[i];
                    j++;
                }
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            }
        }

        public ItemStack[] getRowItems() {
            return this.rowItems;
        }

        public ItemStack getRowItem(int item) {
            return (this.rowItems[item] == null) ? new ItemStack(Material.AIR) : this.rowItems[item];
        }

        private int getRow() {
            return this.row;
        }
    }
}
