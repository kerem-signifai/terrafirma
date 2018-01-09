package net.frozenorb.terrafirma.listener;

import net.frozenorb.terrafirma.TerraFirma;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.map.RealmBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();

        Bukkit.getScheduler().runTaskAsynchronously(TerraFirma.getInstance(), () -> { // asynchronously dispatch semi-resource-intensive task
            Claim claim = RealmBoard.getClaimAt(to);

            if (claim != null && !claim.getCuboid().contains(from)) { // Make sure they are not in the same cuboid as before
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Now entering claim " + ChatColor.AQUA + claim.getName()
                        + ChatColor.YELLOW +" of " + ChatColor.RED + claim.getOwnerUsername() + ChatColor.YELLOW + ".");
            }
        });
    }
}
