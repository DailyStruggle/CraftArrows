package io.github.dailystruggle.craftarrows.Commands.executors;

import io.github.dailystruggle.craftarrows.Arrows.ArrowVariant;
import io.github.dailystruggle.craftarrows.GUI.ArrowListMenu;
import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Util.OutputHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftArrowsListCommandExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("ca.list") && !sender.hasPermission("craftarrow.list")) {
            OutputHandler.PrintError(sender, "You don't have permission to do that");
        } else if (sender instanceof Player && args.length == 1 && args[0].equalsIgnoreCase("gui")) {
            if (!sender.hasPermission("ca.menu.view")) {
                OutputHandler.PrintError(sender, "You don't have permission to do that!");
                return false;
            }
            ArrowListMenu.open((Player) sender);
        } else {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + "Craft Arrows List");
            for (ArrowVariant variant : CraftArrows.getAllArrowVariants())
                sender.sendMessage("- " + variant.getName());
        }
        return false;
    }
}
