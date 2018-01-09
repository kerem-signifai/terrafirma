package net.frozenorb.terrafirma.visual;

import net.frozenorb.qlib.util.ItemUtils;
import net.frozenorb.terrafirma.TerraFirma;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.claim.cuboid.Cuboid;
import net.frozenorb.terrafirma.map.Realm;
import net.frozenorb.terrafirma.map.RealmBoard;
import net.minecraft.server.v1_7_R4.Chunk;
import net.minecraft.server.v1_7_R4.PacketPlayOutMapChunkBulk;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.util.io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Task that draws existing Claims.
 * <p>
 * Since Claims do not update as fast as a player's selection will, this doesn't have to run very often
 */
public class ClaimDrawTask extends BukkitRunnable {
    private static final int DERENDER_DISTANCE = 250; // distance which causes a claim to be removed from player's visually cached claims

    private static final Material[] CLAIM_PILLAR_MATERIALS = new Material[]{
            Material.DIAMOND_ORE,
            Material.GOLD_ORE,
            Material.IRON_ORE,
            Material.LOG,
            Material.OBSIDIAN,
            Material.EMERALD_ORE,
            Material.BOOKSHELF,
            Material.CHEST,
    };

    private static Set<UUID> enabled = new ConcurrentSet<>();
    private static Map<UUID, Set<Claim>> claimsRendered = new HashMap<>();

    /**
     * Called when a claim is deleted, to update existing maps
     *
     * @param claim the claim to delete
     */
    public static void removeClaim(Claim claim) {
        Iterator<Map.Entry<UUID, Set<Claim>>> iter = claimsRendered.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<UUID, Set<Claim>> entry = iter.next();

            if (entry.getValue().contains(claim)) {

                Player player = Bukkit.getPlayer(entry.getKey());

                refreshPlayer(player);
            }
        }
    }

    /**
     * Refreshes visible pillar for a player
     *
     * @param player the player to refresh for
     */
    public static void refreshPlayer(Player player) {
        hide(player);
        show(player);
    }

    /**
     * @param player player to check
     * @return if the player has the map enabled
     */
    public static boolean isEnabled(Player player) {
        return enabled.contains(player.getUniqueId());
    }

    /**
     * Enables the ClaimDrawTask for this player
     *
     * @param player the player to draw claims for
     */
    public static void show(Player player) {
        enabled.add(player.getUniqueId());
    }

    /**
     * Disables the ClaimDrawTask for this player
     *
     * @param player the player to hide claims for
     */
    public static void hide(Player player) {
        enabled.remove(player.getUniqueId());

        LinkedList<Chunk> nmsChunks = new LinkedList<>();

        synchronized (claimsRendered) {

            if (claimsRendered.containsKey(player.getUniqueId())) {
                for (Claim claim : claimsRendered.remove(player.getUniqueId())) {
                    claim.getCuboid().getChunks().forEach(bukkitChunk -> {

                        Chunk nmsCk = ((CraftChunk) bukkitChunk).getHandle();

                        if (!nmsChunks.contains(nmsCk)) {
                            nmsChunks.add(nmsCk);
                        }
                    });
                }
            }

            player.removeMetadata("claimsDrawn", TerraFirma.getInstance());

            PlayerConnection playerCon = ((CraftPlayer) player).getHandle().playerConnection;

            //split the packet into a few packets, if the compressed size is too large
            while (!nmsChunks.isEmpty()) {

                List<Chunk> queuedChunks = new ArrayList<>();

                for (int i = 0; i < 20; i++) {
                    if (nmsChunks.isEmpty()) {
                        break;
                    }
                    queuedChunks.add(nmsChunks.pop());
                }

                // Send a MapChunkBulk packet in order to hide all of the Claim Pillars that we drew
                PacketPlayOutMapChunkBulk cb = new PacketPlayOutMapChunkBulk(queuedChunks, playerCon.networkManager.getVersion());
                playerCon.sendPacket(cb);
            }


        }

    }

    @Override public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("teleported")) { // Ensure chunks are loaded before redrawing chunks
                if (player.getMetadata("teleported").get(0).asLong() + 5000 <= System.currentTimeMillis()) {
                    player.removeMetadata("teleported", TerraFirma.getInstance());

                    Bukkit.getScheduler().runTask(TerraFirma.getInstance(), () -> refreshPlayer(player));
                }
                continue;

            }

            int claimsDrawn = player.hasMetadata("claimsDrawn") ? player.getMetadata("claimsDrawn").get(0).asInt() : 0;

            Set<Claim> taskDrawn = new HashSet<>(); // store Claim objects drawn during this iteration so we can remove old claim objects

            synchronized (claimsRendered) {
                if (enabled.contains(player.getUniqueId())) {

                    for (Realm realm : getPlayerNearbyRealms(player)) {

                        if (RealmBoard.getRealms().containsKey(realm)) {

                            for (Claim claim : RealmBoard.getRealms().get(realm)) {

                                taskDrawn.add(claim);
                                syncDrawClaim(claim, player, getPillarMaterial(++claimsDrawn));

                                player.setMetadata("claimsDrawn", new FixedMetadataValue(TerraFirma.getInstance(), claimsDrawn));
                            }
                        }
                    }

                    if (claimsRendered.containsKey(player.getUniqueId())) {
                        // Remove claims that used to be rendered, but are no longer in range
                        claimsRendered.get(player.getUniqueId()).removeIf(c ->
                                !taskDrawn.contains(c)
                                        && c.getCuboid().getCenter().distanceSquared(player.getLocation()) >= DERENDER_DISTANCE * DERENDER_DISTANCE
                        );
                    }
                } else {
                    claimsRendered.remove(player.getUniqueId());
                }
            }
        }
    }

    /**
     * Draws a claim for a player
     *
     * @param claim  the claim to draw
     * @param player the player to draw for
     * @param type   material to draw pillar with
     */
    private void syncDrawClaim(Claim claim, Player player, Material type) {
        if (!claimsRendered.containsKey(player.getUniqueId()) || !claimsRendered.get(player.getUniqueId()).contains(claim)) {
            // send the player the claim Pillar message data if this is their first time rendering this claim

            String message = ChatColor.YELLOW + "Claim [" + ChatColor.AQUA + claim.getName() + ChatColor.YELLOW
                    + "] (" + ChatColor.GREEN + ItemUtils.getName(new ItemStack(type))
                    + ChatColor.YELLOW + ") is owned by " + ChatColor.RED + claim.getOwnerUsername();

            player.sendMessage(message);


            claimsRendered.putIfAbsent(player.getUniqueId(), new HashSet<>());
            claimsRendered.get(player.getUniqueId()).add(claim);

            Cuboid cuboid = claim.getCuboid();
            Location[] corners = cuboid.getCorners();

            syncDrawPillar(corners[0], player, type, Coordinate.X, 1, cuboid.getSizeX() - 2);
            syncDrawPillar(corners[1], player, type, Coordinate.Z, 1, cuboid.getSizeZ() - 2);
            syncDrawPillar(corners[2], player, type, Coordinate.X, -1, cuboid.getSizeX() - 2);
            syncDrawPillar(corners[3], player, type, Coordinate.Z, -1, cuboid.getSizeZ() - 2);

        }
    }

    /**
     * Draws a pillar of a claim
     *
     * @param loc        the corner to draw at
     * @param player     the player to draw for
     * @param material   the material of the pillar
     * @param coordinate whether the stained glass should be drawn in the X or Z direction
     * @param sign       whether the stained glass should be built in the + or - directions
     * @param travel     the distance for which the stained glass panels should be built
     */
    private void syncDrawPillar(Location loc, Player player, Material material, Coordinate coordinate, int sign, int travel) {
        // We provide the Coordinate and sign here, so that each pillar also draws a line of stained glass every 4 blocks to an adjacent pillar

        Location base = loc.clone();

        for (int y = 0; y < 256; y++) {
            base.setY(y);

            if (y % 2 == 0) {
                player.sendBlockChange(base, material, (byte) 0);
                if (y % 4 == 0) {
                    Location add = null;

                    if (coordinate == Coordinate.X) {
                        add = new Location(base.getWorld(), sign, 0, 0);
                    } else if (coordinate == Coordinate.Z) {
                        add = new Location(base.getWorld(), 0, 0, sign);

                    }

                    Location stained = base.clone();
                    for (int i = 0; i < travel; i++) {
                        Location stainedGlass = stained.add(add);
                        player.sendBlockChange(stainedGlass, Material.STAINED_GLASS_PANE, (byte) 1);
                    }
                }
            } else {
                player.sendBlockChange(base, Material.GLASS, (byte) 0);
            }

        }
    }

    /**
     * @param player player to get nearby Realms for
     * @return gets adjacent + 1 realms and the current realm a player is in
     */
    private Realm[] getPlayerNearbyRealms(Player player) {
        Realm[] realm = new Realm[25];
        Location center = player.getLocation();

        int delta = (int) Math.pow(2, Realm.SHIFT_OPERATION); // Realm size, so that we can add to our central location to get nearby Realms

        int index = 0;

        for (int i = -2; i <= 2; i++) { // Nested loop so we can iterate through every permutation of adding the Realm size
            for (int j = -2; j <= 2; j++) {
                realm[index++] = new Realm(center.clone().add(i * delta, 0, j * delta));
            }
        }

        return realm;
    }

    /**
     * Selects a Material from the materials array
     *
     * @param index the index to use, if greater than array length, array length will be subtracted
     * @return selected material
     */
    private Material getPillarMaterial(int index) {
        while (index >= CLAIM_PILLAR_MATERIALS.length) {
            index = index - CLAIM_PILLAR_MATERIALS.length;
        }

        return CLAIM_PILLAR_MATERIALS[index];
    }

    /**
     * Utility enum class to differ between X and Z values
     */
    enum Coordinate {
        X,
        Z
    }

}
