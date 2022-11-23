package io.github.dailystruggle.craftarrows.Commands.executors;

import io.github.dailystruggle.craftarrows.GUI.ArrowRecipeMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftArrowsRecipeExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0)
                ArrowRecipeMenu.open(player);
            if (args.length == 1)
                ArrowRecipeMenu.open(player, args[0]);
        }
        return false;
    }
}
