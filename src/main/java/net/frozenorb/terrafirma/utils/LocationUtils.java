package net.frozenorb.terrafirma.utils;

import org.bukkit.Location;

import java.util.Collection;

public class LocationUtils {

    public static boolean contains(Collection<Location> locations, Location checkFor, boolean accountY) {
        return locations.stream().anyMatch(loc -> closeEnough(loc, checkFor, accountY));
    }

    public static boolean closeEnough(Location loc, Location loc2, boolean accountY) {
        return loc.getBlockX() == loc2.getBlockX()
                && loc.getBlockZ() == loc2.getBlockZ()
                && (accountY ? loc.getBlockY() == loc2.getBlockY() : true);
    }
}
