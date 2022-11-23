package io.github.dailystruggle.craftarrows.Commands;

import io.github.dailystruggle.craftarrows.Util.OutputHandler;
import org.bukkit.command.CommandSender;

public class CommandHelper {
    public static void sendHelp(CommandSender sender) {
        if (sender.hasPermission("ca.give") || sender.hasPermission("craftarrow.give"))
            OutputHandler.PrintRawInfo(sender, "/ca give <player> <arrow> [count]");
        if (sender.hasPermission("ca.list") || sender.hasPermission("craftarrow.list"))
            OutputHandler.PrintRawInfo(sender, "/ca list");
        OutputHandler.PrintRawInfo(sender, "/ca recipes [name] - lookup a recipe by simple name (e.g. Freezing Arrow = freezing)");
        if (sender.hasPermission("ca.reload") || sender.hasPermission("craftarrow.reload"))
            OutputHandler.PrintRawInfo(sender, "/ca reload");
    }
}
