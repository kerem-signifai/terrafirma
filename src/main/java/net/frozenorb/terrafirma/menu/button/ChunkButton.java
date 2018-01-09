package net.frozenorb.terrafirma.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.menu.VirtualChunk;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ChunkButton extends Button {
    private VirtualChunk chunk;
    private Claim[] claims;

    @Override public String getName(Player player) {
        return claims.length == 0 ? (ChatColor.YELLOW + "No claims") : (ChatColor.GREEN + "" + claims.length + " claim" + (claims.length == 1 ? "" : "s"));
    }

    @Override public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.BLUE + "(" + chunk.getX() + ", " + chunk.getZ() + ")");

        if (claims.length != 0) {

            lore.add("");
            for (Claim claim : claims) {
                lore.add(ChatColor.AQUA + claim.getName() + ChatColor.YELLOW + " [" + claim.getOwnerUsername() + "]");
            }
        }

        Chunk chunk = player.getLocation().getChunk();

        if (chunk.getX() == this.chunk.getX() && chunk.getZ() == this.chunk.getZ()) {
            lore.add("");
            lore.add(ChatColor.YELLOW + "You are here!");

        }

        return lore;
    }

    @Override public Material getMaterial(Player player) {
        return Material.STAINED_GLASS_PANE;
    }

    @Override public byte getDamageValue(Player player) {
        return claims.length == 0 ? (byte) 15 : (byte) 5;
    }

    @Override public void clicked(Player player, int i, ClickType clickType) {
    }
}
