package io.github.dailystruggle.craftarrows.Commands.tabcompleters;

import io.github.dailystruggle.craftarrows.Arrows.ArrowVariant;
import io.github.dailystruggle.craftarrows.CraftArrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CraftArrowsGiveTabCompleter implements TabCompleter {
    private final List<String> countList = Collections.singletonList("[count]");
    private final List<String> emptyList = Collections.emptyList();
    private List<String> arrowNames;

    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 1)
            return getPlayerNames();
        if (args.length == 2) {
            if (this.arrowNames == null)
                this.arrowNames = getArrowNames();
            return this.arrowNames;
        }
        if (args.length > 2)
            return this.countList;
        return this.emptyList;
    }

    private List<String> getPlayerNames() {
        Stream<String> playerNames = Arrays.stream(Bukkit.getOnlinePlayers().toArray()).map(o -> ((Player) o).getName());
        return playerNames.collect(Collectors.toList());
    }

    private List<String> getArrowNames() {
        Stream<String> arrowNames = Arrays.stream(CraftArrows.getAllArrowVariants().toArray()).map(o -> ((ArrowVariant) o).getName());
        return arrowNames.collect(Collectors.toList());
    }
}
