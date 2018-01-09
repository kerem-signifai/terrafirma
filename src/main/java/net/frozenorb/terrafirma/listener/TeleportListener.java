package net.frozenorb.terrafirma.listener;

import net.frozenorb.terrafirma.TerraFirma;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class TeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo().distanceSquared(event.getFrom()) > 64 * 64) {
            // If they teleport far enough, let's make them invulnerable
            // from pillars updating until all chunks are loaded.
            event.getPlayer().setMetadata("teleported", new FixedMetadataValue(TerraFirma.getInstance(), System.currentTimeMillis()));
        }
    }
}
