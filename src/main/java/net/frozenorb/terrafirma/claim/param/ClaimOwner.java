package net.frozenorb.terrafirma.claim.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.frozenorb.qlib.util.UUIDUtils;

import java.util.UUID;

@AllArgsConstructor
@Data
public class ClaimOwner {
    private UUID uuid;

    private String getName() {
        return UUIDUtils.name(uuid);
    }
}
