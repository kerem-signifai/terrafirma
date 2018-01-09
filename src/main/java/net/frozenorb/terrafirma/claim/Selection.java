package net.frozenorb.terrafirma.claim;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.terrafirma.TerraFirma;
import net.frozenorb.terrafirma.claim.cuboid.Cuboid;
import net.frozenorb.terrafirma.map.Realm;
import net.frozenorb.terrafirma.map.RealmBoard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Represents a region, either completely selection, or not, that has not been created and saved yet.
 */
@RequiredArgsConstructor
public class Selection {

    public static final int         MAX_SELECTION_AREA      = 10_000;                // Max area for a cross-section of the selection (100x100)
    public static final String      SELECTION_METADATA_KEY  = "terraFirmaSelection"; // Key to store the Selection object under in a player's metadata
    public static final ItemStack   SELECTION_WAND;

    // The two corners of the selection
    @NonNull @Getter private Location point1;
    @NonNull @Getter private Location point2;

    @Getter private Claim intersector; // Set intersector Claim here so that we can cleanly return an enum in Selection#verifySelection

    /**
     * Initialize our ItemStack object statically, to ensure the ItemStack and ItemMeta libraries are fully initialized,
     * as well as taking into account the fact that we actually have to modify the object in order to set its meta
     */
    static {

        SELECTION_WAND = new ItemStack(Material.BONE);

        ItemMeta meta = SELECTION_WAND.getItemMeta();
        meta.setDisplayName("§eSelection Wand");

        SELECTION_WAND.setItemMeta(meta);
    }

    /**
     * Private, so that we can create a new instance in the Selection#createOrGetSelection method.
     */
    private Selection() {}

    /**
     * @param point1 the new point1
     * @return a result based on the circumstances
     */
    public BlockSelectResult setPoint1(Location point1) {
        final Location oldPoint = this.point1 == null ? null : this.point1.clone();

        this.point1 = point1;

        BlockSelectResult result = verifySelection();

        if (!result.isSuccess()) {
            this.point1 = oldPoint; // set the location to the original location if the result is a failure
        }

        return result;
    }

    /**
     * @param point2 the new point1
     * @return a result based on the circumstances
     */
    public BlockSelectResult setPoint2(Location point2) {
        final Location oldPoint = this.point2 == null ? null : this.point2.clone();

        this.point2 = point2;

        BlockSelectResult result = verifySelection();

        if (!result.isSuccess()) {
            this.point2 = oldPoint; // set the location to the original location if the result is a failure
        }

        return result;
    }

    /**
     * @return if the Selection can form a full cuboid object
     */
    public boolean isFullObject() {
        return point1 != null && point2 != null;
    }

    /**
     * Resets both locations in the Selection
     */
    public void clear() {
        point1 = null;
        point2 = null;
    }

    /**
     * @return null if both corners are not set, else a Cuboid object representing this selection
     */
    public Cuboid getCuboid() {
        if (!isFullObject()) {
            return null;
        }

        return new Cuboid(point1, point2);
    }

    private BlockSelectResult verifySelection() {
        if (isFullObject()) {
            Cuboid cuboid = getCuboid();

            if ((cuboid.getSizeX() * cuboid.getSizeZ()) > MAX_SELECTION_AREA) {
                return BlockSelectResult.SELECTION_TOO_LARGE;
            }

            for (Realm realm : cuboid.getRealms()) {
                if (RealmBoard.getRealms().containsKey(realm)) {
                    for (Claim claim : RealmBoard.getRealms().get(realm)) {

                        for (Location location : cuboid) {
                            if (claim.getCuboid().contains(location)) {
                                intersector = claim;
                                return BlockSelectResult.OVERLAPPING_CLAIM;
                            }
                        }
                    }
                }
            }

            return BlockSelectResult.SELECTION_COMPLETE;

        }
        return BlockSelectResult.LOCATION_SET;
    }

    /**
     * Selections are stored in the player's metadata. This method removes the need
     * to use Bukkit Metadata API calls all over the place.
     * <p>
     * This method can be modified structurally as needed, the plugin only access Selection objects
     * via this method.
     *
     * @param player the player for whom to grab the Selection object for
     * @return selection object, either new or created
     */
    public static Selection createOrGetSelection(Player player) {
        if (player.hasMetadata(SELECTION_METADATA_KEY)) {
            return (Selection) player.getMetadata(SELECTION_METADATA_KEY).get(0).value();
        }
        Selection selection = new Selection();
        player.setMetadata(SELECTION_METADATA_KEY, new FixedMetadataValue(TerraFirma.getInstance(), selection));

        return selection;
    }
}
