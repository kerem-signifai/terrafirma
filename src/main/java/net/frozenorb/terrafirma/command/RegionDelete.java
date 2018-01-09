package net.frozenorb.terrafirma.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.map.RealmBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegionDelete {

    @Command(names = {"rg del", "rg delete", "region delete"}, permissionNode = "terrafirma.use")
    public static void regionCreate(Player sender, @Parameter(name = "claim") Claim claim) {
        RealmBoard.deleteClaim(claim);
        sender.sendMessage(ChatColor.RED + "You have deleted claim '" + ChatColor.YELLOW + claim.getName() + ChatColor.RED + "'!");
    }
}
