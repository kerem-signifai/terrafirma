package net.frozenorb.terrafirma.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.claim.param.ClaimOwner;
import net.frozenorb.terrafirma.map.RealmBoard;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegionList {

    @Command(names = {"rg l", "rg list", "region list"}, permissionNode = "terrafirma.use")
    public static void region(Player sender, @Parameter(name = "owner", defaultValue = "self") ClaimOwner claimOwner) {

        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 22)
                + "§e Claims " + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 22));

        for (Claim claim : RealmBoard.getPlayerClaims(claimOwner.getUuid())) {

            claim.getFancyDisplay().send(sender);
        }

        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 50));

    }
}
