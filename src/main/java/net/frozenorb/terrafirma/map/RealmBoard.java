package net.frozenorb.terrafirma.map;

import net.frozenorb.terrafirma.TerraFirma;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.visual.ClaimDrawTask;
import net.frozenorb.terrafirma.data.StorageHandler;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.minecraft.util.io.netty.util.internal.ConcurrentSet;
import net.minecraft.util.org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the entire world and all of the claims and which Realm they are in.
 *
 * @see Realm
 */
public class RealmBoard {
    private static Map<Realm, Collection<Claim>> realms = new ConcurrentHashMap<>();
    private static Map<UUID, Collection<Realm>> playerRealms = new ConcurrentHashMap<>();

    /**
     * Gets the Claim at a certain Location
     *
     * @param location the location to check
     * @return null if no claim exists, existing claim otherwise
     */
    public static Claim getClaimAt(Location location) {
        Validate.notNull(location, "Location cannot be null!");

        Realm realm = new Realm(location);

        if (realms.containsKey(realm)) {
            for (Claim claim : realms.get(realm)) {
                if (claim.getCuboid().contains(location)) {
                    return claim;
                }
            }
        }

        return null;
    }

    /**
     * @return Immutable Map of UUIDs mapped to Realms
     */
    public static Map<UUID, Collection<Realm>> getPlayerRealms() {
        return ImmutableMap.copyOf(playerRealms);
    }

    /**
     * @return Immutable Map of Realms mapped to Claims
     */
    public static Map<Realm, Collection<Claim>> getRealms() {
        return ImmutableMap.copyOf(realms);
    }

    /**
     * Gets if a player has any Claims. This checks if the playerRealms map contains a UUID, it is fine
     * to check this map, since we clear UUIDs on deletion of a Claim
     *
     * @param owner the UUID to check
     * @return if the UUID has any claims attached
     */
    public static boolean hasAnyClaims(UUID owner) {
        return playerRealms.containsKey(owner);
    }

    /**
     * @param owner the owner to check for
     * @param name  name of the claim
     * @return Player Claim with the given name
     */
    public static Claim getClaim(UUID owner, String name) {
        if (playerRealms.containsKey(owner)) {
            for (Realm realm : playerRealms.get(owner)) {
                for (Claim claim : realms.get(realm)) {
                    if (claim.getName().equalsIgnoreCase(name) && claim.getOwner().equals(owner)) {
                        return claim;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Gets all of a player's claims
     *
     * @param owner UUID of the Claim owner
     * @return array of Claims
     */
    public static Claim[] getPlayerClaims(UUID owner) {
        Set<Claim> claims = new HashSet<>();

        if (playerRealms.containsKey(owner)) {
            for (Realm realm : playerRealms.get(owner)) {
                for (Claim claim : realms.get(realm)) {
                    if (claim.getOwner().equals(owner)) {
                        claims.add(claim);
                    }
                }
            }
        }

        return claims.toArray(new Claim[]{});
    }

    public static Claim[] getClaimsInChunk(Chunk chunk) {
        Set<Claim> claims = new HashSet<>();

        int bx = chunk.getX() << 4;
        int bz = chunk.getZ() << 4;

        for (int xx = bx; xx < bx + 16; xx++) {
            for (int zz = bz; zz < bz + 16; zz++) {

                Location loc = new Location(chunk.getWorld(), xx, 80, zz);

                Claim claim = getClaimAt(loc);

                if (claim != null) {
                    claims.add(claim);
                }
            }
        }

        return claims.toArray(new Claim[]{});
    }

    /**
     * Deletes a claim.
     * <p>
     * <b>This should be run asynchronously</b>
     *
     * @param claim the claim to delete
     */
    public static void deleteClaim(Claim claim) {
        UUID owner = claim.getOwner();
        Set<Realm> needsToPersist = new HashSet<>();

        // List of Realms that need to be saved
        // (all Realms but the ones that are being deleted)

        for (Realm realm : claim.getCuboid().getRealms()) {
            realms.get(realm).remove(claim);
        }

        ClaimDrawTask.removeClaim(claim);

        if (hasAnyClaims(owner)) {
            for (Claim existing : getPlayerClaims(owner)) {
                if (existing != claim) {
                    for (Realm realm : existing.getCuboid().getRealms()) {
                        needsToPersist.add(realm);
                    }
                }
            }
        }

        if (needsToPersist.isEmpty()) { // No need to UUID in memory if player has no other realms
            playerRealms.remove(owner);
        } else {
            playerRealms.put(owner, needsToPersist);
        }

        Bukkit.getScheduler().runTaskAsynchronously(TerraFirma.getInstance(), () -> TerraFirma.getInstance().getStorageHandler().updatePlayer(owner));
    }

    /**
     * Adds a claim to the RealmBoard, by getting each Realm the claim is in, and setting the realm in the map.
     *
     * @param claim the claim to add
     */
    public static void addClaim(Claim claim) {
        Validate.notNull(claim, "Claim cannot be null!");

        for (Realm realm : claim.getCuboid().getRealms()) {
            if (!realms.containsKey(realm)) {
                realms.put(realm, new ConcurrentSet<>());
            }

            realms.get(realm).add(claim);

            if (!playerRealms.containsKey(claim.getOwner())) {
                playerRealms.put(claim.getOwner(), new ConcurrentSet<>());
            }

            playerRealms.get(claim.getOwner()).addAll(claim.getCuboid().getRealms());

            if (!StorageHandler.isLoading()) { // We don't want to save data if we are still reading from Mongo
                Bukkit.getScheduler().runTaskAsynchronously(TerraFirma.getInstance(), () -> TerraFirma.getInstance().getStorageHandler().updatePlayer(claim.getOwner()));
            }

        }
    }

    /**
     * Gets all of the claims existing in the world
     *
     * @return claims
     */
    public static Collection<Claim> getClaims() {
        Set<Claim> claims = new HashSet<>();

        realms.values().forEach(coll -> claims.addAll(coll));

        return claims;
    }
}
