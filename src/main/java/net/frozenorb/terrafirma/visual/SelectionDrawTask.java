package net.frozenorb.terrafirma.visual;

import net.frozenorb.terrafirma.TerraFirma;
import net.frozenorb.terrafirma.claim.Selection;
import net.frozenorb.terrafirma.claim.cuboid.Cuboid;
import net.frozenorb.terrafirma.utils.LocationUtils;
import net.minecraft.util.io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Task that draws the outline of the currently selection Selection, that can be either completed or being set
 */
public class SelectionDrawTask extends BukkitRunnable {
    private static final int MAX_BLOCKCHANGES_PER_TASK = 3000;

    private Map<UUID, Set<Location>> moddedLocations = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            Selection selection = Selection.createOrGetSelection(player);

            if (player.getItemInHand() == null || player.getItemInHand().getType() != Selection.SELECTION_WAND.getType()) {
                continue;
            }

            if (selection.getPoint2() == null && selection.getPoint1() == null) { // player has no selection, clear data
                if (moddedLocations.containsKey(player.getUniqueId())) {
                    for (Location oldLocation : moddedLocations.get(player.getUniqueId())) {
                        player.sendBlockChange(oldLocation, oldLocation.getBlock().getType(), oldLocation.getBlock().getData());
                    }
                }
                moddedLocations.remove(player.getUniqueId());
            }

            if (selection.getPoint1() != null || selection.getPoint2() != null) {

                Location setCorner = selection.getPoint1() != null ? selection.getPoint1() : selection.getPoint2();
                Location secondaryLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2D));

                if (selection.isFullObject()) { // draw the actual selection, instead of the block the player is looking at
                    if (selection.getPoint1().equals(setCorner)) {
                        secondaryLocation = selection.getPoint2();
                    } else {
                        secondaryLocation = selection.getPoint1();
                    }
                }

                if (player.getLocation().getChunk().isLoaded() && secondaryLocation.getChunk().isLoaded() && setCorner.getChunk().isLoaded()) {
                    Cuboid sendPlayer = new Cuboid(setCorner, secondaryLocation);

                    int crossSection = sendPlayer.getSizeX() * sendPlayer.getSizeZ();

                    if (crossSection <= MAX_BLOCKCHANGES_PER_TASK) { // limit size, could cause connection issues

                        Set<Location> locationsChanged = new ConcurrentSet<>(); // block changes that have been sent to player in the current iteration

                        List<Location> playerTargettedBlocks = new ArrayList<>();
                        BlockIterator blockIter = new BlockIterator(player, 3); // This allows us to get the 3 blocks in the player's line of sight

                        while (blockIter.hasNext()) {
                            playerTargettedBlocks.add(blockIter.next().getLocation());
                        }

                        for (Cuboid cuboid : sendPlayer.getWalls()) {

                            // Speeds up operation, y-val doesn't matter since we are sending the highest y-coord anyways
                            cuboid.setY1(1);
                            cuboid.setY2(1);

                            for (Location loc : cuboid) {

                                if (!selection.isFullObject()) {
                                    if (loc.getBlockX() == player.getLocation().getBlockX() && loc.getBlockZ() == player.getLocation().getBlockZ()) {
                                        // Don't send the corner a player is on
                                        continue;
                                    }

                                    if (LocationUtils.contains(playerTargettedBlocks, loc, false)) {
                                        // Don't send their 3-block line of sight
                                        continue;
                                    }

                                }

                                // Only send a thin border, full Y-values is too slow :(
                                loc.setY(loc.getWorld().getHighestBlockYAt(loc));
                                player.sendBlockChange(loc, Material.STAINED_GLASS, (byte) 14);

                                locationsChanged.add(loc);
                            }

                        }

                        // Here we check the old locations and remove locations that are no longer part of the Selection
                        if (moddedLocations.containsKey(player.getUniqueId())) {
                            for (Location oldLocation : moddedLocations.get(player.getUniqueId())) {
                                if (!locationsChanged.contains(oldLocation)) {
                                    player.sendBlockChange(oldLocation, oldLocation.getBlock().getType(), oldLocation.getBlock().getData());

                                }
                            }
                        }

                        // Set their old locations for the next task iteration
                        moddedLocations.put(player.getUniqueId(), locationsChanged);

                        player.removeMetadata("selTooLargeMsg", TerraFirma.getInstance());

                    } else {
                        if (player.hasMetadata("selTooLargeMsg")) {
                            return;
                        }

                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[!] " + ChatColor.YELLOW
                                + "Your selection is too large to render properly! " + ChatColor.RED + "("
                                + crossSection + " > " + MAX_BLOCKCHANGES_PER_TASK + ")");

                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[!] " + ChatColor.YELLOW
                                + "You will not see the stained glass border outside of this range.");

                        player.setMetadata("selTooLargeMsg", new FixedMetadataValue(TerraFirma.getInstance(), true));
                    }
                } else {
                    if (moddedLocations.containsKey(player.getUniqueId())) {
                        for (Location oldLocation : moddedLocations.remove(player.getUniqueId())) {
                            player.sendBlockChange(oldLocation, oldLocation.getBlock().getType(), oldLocation.getBlock().getData());

                        }
                    }
                }
            }
        }
    }
}
