package net.frozenorb.terrafirma.claim;

import lombok.Getter;

public enum BlockSelectResult {

    /**
     * Result returned when the horizontal area of a cross-section
     * of a selection  is greater than internally set.
     * @see Selection
     */
    SELECTION_TOO_LARGE(false),

    /**
     * Result returned when this Selection intersects an existing Claim.
     */
    OVERLAPPING_CLAIM(false),

    /**
     * Result returned when a certain point has been set, but not both.
     */
    LOCATION_SET(true),

    /**
     * Result returned when both locations have been successfully set.
     */
    SELECTION_COMPLETE(true);

    @Getter private boolean success;

    BlockSelectResult(boolean success) {
        this.success = success;
    }

}
