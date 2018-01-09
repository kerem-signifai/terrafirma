package net.frozenorb.terrafirma.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.terrafirma.map.RealmBoard;
import net.frozenorb.terrafirma.menu.button.ChunkButton;
import net.frozenorb.terrafirma.utils.FaceUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ChunkMapMenu extends Menu {

    @Override public String getTitle(Player player) {
        BlockFace bf = FaceUtil.yawToFace(player.getLocation().getYaw());

        return "Chunkmap (Top: " + StringUtils.capitalize(bf.name().toLowerCase()) + ")";
    }

    /**
     * Places a button in the given Map based on the x and z coordinates.
     * Takes x and z as parameters, using a cartesian-based coordinate system, with a central origin of (0, 0)
     * E.g: The actual '0' slot in the Menu would be -4, -2;
     * The actual center is (4, 3
     *
     * @param button  button to add
     * @param buttons map to add to
     * @param x       x coord
     * @param z       z coord
     */
    private void center(Button button, Map<Integer, Button> buttons, int x, int z) {
        buttons.put(super.getSlot(x + 4, z + 3), button);
    }

    @Override public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        
        Chunk cur = player.getLocation().getChunk();

        BlockFace bf = FaceUtil.yawToFace(player.getLocation().getYaw());
        BlockFace right = FaceUtil.toRightOf(bf);
        BlockFace left = right.getOppositeFace();

        for (int lrDelta = -4; lrDelta <= 4 ; lrDelta++) {
            VirtualChunk vc = new VirtualChunk(cur.getWorld(), cur.getX(), cur.getZ());

            BlockFace shift = lrDelta < 0 ? left : right;

            for (int shiftVal = 0; shiftVal < Math.abs(lrDelta); shiftVal++) {
                vc = vc.relative(shift);
            }

            for (int center = 3; center >= 0; center--) {
                vc = vc.relative(bf.getOppositeFace());
                // Make the center of the menu the player's chunk
            }

            for (int i = 2; i >= -3; i--) {
                // This starts at the bottom of the Menu, and traverses to the top

                vc = vc.relative(bf);
                center(new ChunkButton(vc, RealmBoard.getClaimsInChunk(vc)), buttons, 0 + lrDelta, i);
            }
        }




        return buttons;
    }
}
