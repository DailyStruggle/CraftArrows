package io.github.dailystruggle.craftarrows.Commands.executors;

import io.github.dailystruggle.craftarrows.Commands.CommandHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CraftArrowsCommandExecutor implements CommandExecutor {
    private final CraftArrowsGiveCommandExecutor giveCommandExecutor = new CraftArrowsGiveCommandExecutor();

    private final CraftArrowsListCommandExecutor listCommandExecutor = new CraftArrowsListCommandExecutor();

    private final io.github.dailystruggle.craftarrows.Commands.executors.CraftArrowsRecipeExecutor recipeCommandExecutor = new CraftArrowsRecipeExecutor();

    private final CraftArrowsReloadCommandExecutor reloadCommandExecutor = new CraftArrowsReloadCommandExecutor();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0) {
            String command = args[0];
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            if (command.equalsIgnoreCase("give")) {
                this.giveCommandExecutor.onCommand(sender, cmd, command, subArgs);
            } else if (command.equalsIgnoreCase("list")) {
                this.listCommandExecutor.onCommand(sender, cmd, command, subArgs);
            } else if (command.equalsIgnoreCase("recipes")) {
                this.recipeCommandExecutor.onCommand(sender, cmd, command, subArgs);
            } else if (command.equalsIgnoreCase("reload")) {
                this.reloadCommandExecutor.onCommand(sender, cmd, command, subArgs);
            } else {
                CommandHelper.sendHelp(sender);
            }
        } else {
            CommandHelper.sendHelp(sender);
        }
        return false;
    }
}
