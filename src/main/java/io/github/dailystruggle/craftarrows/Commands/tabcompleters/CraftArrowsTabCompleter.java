package io.github.dailystruggle.craftarrows.Commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class CraftArrowsTabCompleter implements TabCompleter {
    private final List<String> subCommands = Arrays.asList("give", "list", "recipes", "reload");

    private final CraftArrowsGiveTabCompleter giveTabCompleter = new CraftArrowsGiveTabCompleter();

    private final CraftArrowsRecipeTabCompleter recipeTabCompleter = new CraftArrowsRecipeTabCompleter();

    private final List<String> emptyList = Arrays.asList(new String[0]);

    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 1)
            return this.subCommands;
        if (args.length > 1) {
            String subCommand = args[0];
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            if (subCommand.equalsIgnoreCase("give"))
                return this.giveTabCompleter.onTabComplete(sender, cmd, s, subArgs);
            if (subCommand.equalsIgnoreCase("list"))
                return this.emptyList;
            if (subCommand.equalsIgnoreCase("recipes"))
                return this.recipeTabCompleter.onTabComplete(sender, cmd, s, subArgs);
            if (subCommand.equalsIgnoreCase("reload"))
                return this.emptyList;
        }
        return this.emptyList;
    }
}
