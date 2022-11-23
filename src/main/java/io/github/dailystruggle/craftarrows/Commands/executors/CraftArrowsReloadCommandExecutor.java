package io.github.dailystruggle.craftarrows.Commands.executors;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Util.OutputHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CraftArrowsReloadCommandExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("ca.reload") && !sender.hasPermission("craftarrow.reload")) {
            OutputHandler.PrintError(sender, "You don't have permission to do that");
        } else {
            CraftArrows.reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "[CraftArrows]: Reloaded!");
        }
        return false;
    }
}
