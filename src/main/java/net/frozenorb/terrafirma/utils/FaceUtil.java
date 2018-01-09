package net.frozenorb.terrafirma.utils;

import org.bukkit.block.BlockFace;

public class FaceUtil {

    public static final BlockFace[] axis = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

    /**
     * Gets the horizontal Block Face from a given yaw angle
     *
     * @param yaw                      angle
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw) {
        return axis[Math.round(yaw / 90f) & 0x3];
    }

    public static BlockFace toRightOf(BlockFace blockFace) {
        switch (blockFace) {
            case NORTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.WEST;
            case WEST:
                return BlockFace.NORTH;

        }
        return blockFace;
    }
}
