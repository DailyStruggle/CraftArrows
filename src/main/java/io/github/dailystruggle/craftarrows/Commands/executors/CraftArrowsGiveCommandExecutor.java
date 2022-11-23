package io.github.dailystruggle.craftarrows.Commands.executors;

import io.github.dailystruggle.craftarrows.Arrows.ArrowVariant;
import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Util.OutputHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftArrowsGiveCommandExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("ca.give") && !sender.hasPermission("craftarrow.give")) {
            OutputHandler.PrintError(sender, "You don't have permission to do that");
        } else if (args.length == 3) {
            Player player = Bukkit.getPlayer(args[0]);
            ArrowVariant arrowVariant = CraftArrows.getArrowVariantBySimpleName(args[1]);
            int count = 1;
            try {
                count = Integer.valueOf(args[2]).intValue();
            } catch (Exception exception) {
            }
            if (player != null && arrowVariant != null) {
                ItemStack arrow = arrowVariant.getRecipe().getItem();
                arrow.setAmount(count);
                player.getInventory().addItem(arrow);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("[CraftArrows]: Invalid command usage, /ca give <player> <arrow> <count>");
        }
        return false;
    }
}
