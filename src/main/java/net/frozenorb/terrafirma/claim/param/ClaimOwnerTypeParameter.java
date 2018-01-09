package net.frozenorb.terrafirma.claim.param;

import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.terrafirma.map.RealmBoard;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ClaimOwnerTypeParameter implements ParameterType<ClaimOwner> {

    @Override public ClaimOwner transform(CommandSender commandSender, String s) {

        if (s.equals("self") && commandSender instanceof Player) {
            UUID senderUUID = ((Player) commandSender).getUniqueId();
            if (RealmBoard.hasAnyClaims(senderUUID)) {
                return new ClaimOwner(senderUUID);
            }
            commandSender.sendMessage(ChatColor.RED + "You have no claims!");
            return null;
        }

        UUID uuid = UUIDUtils.uuid(s);

        if (uuid == null) {

            commandSender.sendMessage(ChatColor.RED + "That player has no data!");
            return null;
        }

        if (RealmBoard.hasAnyClaims(uuid)) {
            return new ClaimOwner(uuid);
        }

        commandSender.sendMessage(ChatColor.RED + "That player has no claims!");
        return null;
    }

    @Override public List<String> tabComplete(Player player, Set<String> set, String s) {
        List<String> completions = new ArrayList<>();

        for (UUID uuid : RealmBoard.getPlayerRealms().keySet()) {
            String name = UUIDUtils.name(uuid);

            if (StringUtils.startsWithIgnoreCase(name, s)) {
                completions.add(name);
            }
        }

        return completions;
    }
}
