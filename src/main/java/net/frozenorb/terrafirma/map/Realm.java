package net.frozenorb.terrafirma.map;

import lombok.AllArgsConstructor;
import org.bukkit.Location;

@AllArgsConstructor
public class Realm {

    public static final int SHIFT_OPERATION = 5; // Value that we shift the location x, z values over, 5 -> 2^5 -> 32

    private final int x;
    private final int z;
    private final String world;

    public Realm(Location location) {
        this.x = location.getBlockX() >> SHIFT_OPERATION;
        this.z = location.getBlockZ() >> SHIFT_OPERATION;
        this.world = location.getWorld().getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Realm)) {
            return false;
        }

        Realm other = (Realm) obj;
        return (other.x == x) && (other.z == z) && (other.world.equals(world));
    }

    @Override
    public int hashCode() {
        int code = 21;

        code = (37 * code) + x;
        code = (37 * code) + z;
        code = (37 * code) + (world == null ? 0 :world.hashCode());

        return (code);
    }

}
