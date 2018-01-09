package net.frozenorb.terrafirma.listener;

import net.frozenorb.terrafirma.claim.BlockSelectResult;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.claim.Selection;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ToolInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        Selection selection = Selection.createOrGetSelection(player);

        BlockSelectResult result = null;
        Block clicked = event.getClickedBlock();
        int location = 0;

        if (item != null && item.getType() == Selection.SELECTION_WAND.getType() && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Selection Wand")) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                result = selection.setPoint2(clicked.getLocation());
                location = 2;
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                result = selection.setPoint1(clicked.getLocation());
                location = 1;

            }

            if (result != null) {
                event.setCancelled(true);
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);

                if (result.isSuccess()) {

                    String message = ChatColor.AQUA + (location == 1 ? "First" : "Second") +
                            " location " + ChatColor.YELLOW + "(" + ChatColor.GREEN +
                            clicked.getX() + ChatColor.YELLOW + ", " + ChatColor.GREEN +
                            clicked.getY() + ChatColor.YELLOW + ", " + ChatColor.GREEN +
                            clicked.getZ() + ChatColor.YELLOW + ")" + ChatColor.AQUA + " has been set!";

                    if (selection.isFullObject() && result == BlockSelectResult.SELECTION_COMPLETE) {
                        message += ChatColor.RED + " (" + ChatColor.YELLOW + selection.getCuboid().volume()
                                + ChatColor.AQUA + " blocks" + ChatColor.RED + ")";
                    }

                    player.sendMessage(message);

                } else {

                    String error = ChatColor.RED + "[Error] " + ChatColor.YELLOW;

                    if (result == BlockSelectResult.OVERLAPPING_CLAIM) {
                        Claim overlapped = selection.getIntersector();

                        error += "Your claim overlaps " + ChatColor.AQUA + overlapped.getOwnerUsername() + "'s "
                                + ChatColor.YELLOW + "'" + ChatColor.RED + overlapped.getName() + ChatColor.YELLOW + "' claim!";

                    } else if (result == BlockSelectResult.SELECTION_TOO_LARGE) {
                        error += "Your claim is too large! (Max area: " + Selection.MAX_SELECTION_AREA + ")";
                    }

                    player.sendMessage(error);

                }
            }
        }
    }
}
