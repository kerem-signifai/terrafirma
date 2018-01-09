package net.frozenorb.terrafirma.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.terrafirma.visual.ClaimDrawTask;
import org.bukkit.entity.Player;

public class RegionMap {

    @Command(names = {"rg m", "rg map", "region map"}, permissionNode = "terrafirma.use")
    public static void regionMap(Player sender) {
        
        if (ClaimDrawTask.isEnabled(sender)) {
            ClaimDrawTask.hide(sender);
        } else {
            ClaimDrawTask.show(sender);
        }

        sender.sendMessage("§eClaim Map " + (ClaimDrawTask.isEnabled(sender) ? "§aenabled!" : "§cdisabled!"));
    }
}
