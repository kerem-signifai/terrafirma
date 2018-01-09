package net.frozenorb.terrafirma.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.claim.Selection;
import net.frozenorb.terrafirma.claim.cuboid.Cuboid;
import net.frozenorb.terrafirma.map.RealmBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegionCreate {

    @Command(names = {"rg create", "region create"}, permissionNode = "terrafirma.use")
    public static void regionCreate(Player sender, @Parameter(name = "name") String name) {
        Selection selection = Selection.createOrGetSelection(sender);

        if (selection == null || !selection.isFullObject()) {
            sender.sendMessage(ChatColor.RED + "You do not have a full region selected!");
            return;
        }

        Claim existing = RealmBoard.getClaim(sender.getUniqueId(), name);

        if (existing != null) {
            sender.sendMessage(ChatColor.RED + "You already have a claim named '" + existing.getName() + "'!");
            existing.getFancyDisplay().send(sender);
            return;
        }

        Cuboid cuboid = selection.getCuboid();
        cuboid.setY1(0); // Make sure to occupy every Y-Level
        cuboid.setY2(256);

        Claim claim = new Claim(sender.getUniqueId(), name, cuboid);
        RealmBoard.addClaim(claim);

        sender.sendMessage(ChatColor.YELLOW + "You have created a claim with name §b" + name + "§e!");
        claim.getFancyDisplay().send(sender);

        Selection.createOrGetSelection(sender).clear();

    }
}
