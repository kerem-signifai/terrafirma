package net.frozenorb.terrafirma.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.terrafirma.claim.Selection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegionClearSelectionCommand {

    @Command(names = {"rg clearselection", "rg clearsel", "region clearselection", "clearselection", "rg clear"}, permissionNode = "terrafirma.use")
    public static void regionClearSel(Player sender) {
        Selection.createOrGetSelection(sender).clear();
        sender.sendMessage(ChatColor.YELLOW + "Your selection has been cleared!");
    }
}
