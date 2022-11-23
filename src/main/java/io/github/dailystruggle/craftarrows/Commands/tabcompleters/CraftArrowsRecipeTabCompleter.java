package io.github.dailystruggle.craftarrows.Commands.tabcompleters;

import io.github.dailystruggle.craftarrows.Arrows.ArrowVariant;
import io.github.dailystruggle.craftarrows.CraftArrows;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CraftArrowsRecipeTabCompleter implements TabCompleter {
    private final List<String> emptyList = Collections.emptyList();
    private List<String> arrowNames;

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            if (this.arrowNames == null)
                this.arrowNames = getArrowNames();
            return this.arrowNames;
        }
        return this.emptyList;
    }

    private List<String> getArrowNames() {
        Stream<String> arrowNames = Arrays.stream(CraftArrows.getAllArrowVariants().toArray()).map(o -> ((ArrowVariant) o).getName());
        return arrowNames.collect(Collectors.toList());
    }
}
