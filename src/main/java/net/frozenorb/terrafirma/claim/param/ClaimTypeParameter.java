package net.frozenorb.terrafirma.claim.param;

import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.map.RealmBoard;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ClaimTypeParameter implements ParameterType<Claim> {

    @Override public Claim transform(CommandSender commandSender, String s) {

        UUID senderUUID = ((Player) commandSender).getUniqueId();
        if (RealmBoard.hasAnyClaims(senderUUID)) {
            for (Claim claim : RealmBoard.getPlayerClaims(senderUUID)) {
                if (claim.getName().equalsIgnoreCase(s)) {
                    return claim;
                }
            }
        }

        commandSender.sendMessage(ChatColor.RED + "You have no claims!");
        return null;

    }

    @Override public List<String> tabComplete(Player player, Set<String> set, String s) {
        List<String> completions = new ArrayList<>();
        UUID uuid = player.getUniqueId();

        for (Claim claim : RealmBoard.getPlayerClaims(uuid)) {
            String name = claim.getName();

            if (StringUtils.startsWithIgnoreCase(name, s)) {
                completions.add(name);
            }
        }

        return completions;
    }
}
