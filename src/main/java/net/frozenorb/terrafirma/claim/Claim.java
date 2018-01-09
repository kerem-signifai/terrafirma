package net.frozenorb.terrafirma.claim;

import lombok.AllArgsConstructor;
import lombok.Data;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.terrafirma.claim.cuboid.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.UUID;

@AllArgsConstructor
@Data
/**
 * Data object wrapping a Cuboid, and containing a name and a UUID.
 * Represents a Selection object that has been saved.
 */
public class Claim {
    private UUID owner;
    private String name; // this is the name of the claim
    private Cuboid cuboid;

    /**
     * @return username associated with the owner of this Claim
     */
    public String getOwnerUsername() {
        return UUIDUtils.name(owner);
    }

    /**
     * @return formatted and color message displaying Claim data
     */
    public FancyMessage getFancyDisplay() {
        // This looks super disgusting, but it is the proper way to handle JSON chat with correct linebreaking and color codes

        FancyMessage fancy = new FancyMessage("[");
        fancy.color(ChatColor.BLUE);

        fancy.then(getOwnerUsername() + "'s Claim");
        fancy.color(ChatColor.YELLOW);

        fancy.then("] ");
        fancy.color(ChatColor.BLUE);

        fancy.then("[");
        fancy.color(ChatColor.BLUE);

        Location center = cuboid.getCenter();
        fancy.then(name);
        fancy.color(ChatColor.AQUA);
        fancy.command("/tppos " + center.getX() + " " + center.getWorld().getHighestBlockYAt(center.getBlockX(), center.getBlockZ()) + " " + center.getZ());
        fancy.tooltip(ChatColor.AQUA + "Teleport to this claim!");

        fancy.then("] ");
        fancy.color(ChatColor.BLUE);

        Location c1 = cuboid.getLowerCorner();
        Location c2 = cuboid.getUpperCorner();

        fancy.then("(");
        fancy.color(ChatColor.YELLOW);

        fancy.then(c1.getBlockX() + "");
        fancy.color(ChatColor.LIGHT_PURPLE);

        fancy.then(", ");
        fancy.color(ChatColor.YELLOW);

        fancy.then(c1.getBlockY() + "");
        fancy.color(ChatColor.LIGHT_PURPLE);

        fancy.then(", ");
        fancy.color(ChatColor.YELLOW);

        fancy.then(c1.getBlockZ() + "");
        fancy.color(ChatColor.LIGHT_PURPLE);

        fancy.then(")");
        fancy.color(ChatColor.YELLOW);

        fancy.then(", ");
        fancy.color(ChatColor.GREEN);

        fancy.then("(");
        fancy.color(ChatColor.YELLOW);

        fancy.then(c2.getBlockX() + "");
        fancy.color(ChatColor.LIGHT_PURPLE);

        fancy.then(", ");
        fancy.color(ChatColor.YELLOW);

        fancy.then(c2.getBlockY() + "");
        fancy.color(ChatColor.LIGHT_PURPLE);

        fancy.then(", ");
        fancy.color(ChatColor.YELLOW);

        fancy.then(c2.getBlockZ() + "");
        fancy.color(ChatColor.LIGHT_PURPLE);

        fancy.then(")");
        fancy.color(ChatColor.YELLOW);

        return fancy;
    }
}
