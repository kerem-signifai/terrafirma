package net.frozenorb.terrafirma.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.terrafirma.claim.Selection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegionWandCommand {

    @Command(names = {"rg tool", "rg wand", "region tool", "rg wand"}, permissionNode = "terrafirma.use")
    public static void regionWand(Player sender) {
        if (sender.getInventory().contains(Selection.SELECTION_WAND)) {
            sender.sendMessage(ChatColor.RED + "You already have a §bregion tool §cin your inventory!");
            return;
        }

        sender.getInventory().addItem(Selection.SELECTION_WAND);
        sender.sendMessage(ChatColor.YELLOW + "You have been given the Region Tool!");
        sender.sendMessage(ChatColor.GREEN + "Left-click" + ChatColor.YELLOW + ": Set corner 1");
        sender.sendMessage(ChatColor.RED + "Right-click" + ChatColor.YELLOW + ": Set corner 2");

    }
}
