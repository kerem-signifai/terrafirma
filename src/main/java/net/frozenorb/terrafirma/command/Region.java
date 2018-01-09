package net.frozenorb.terrafirma.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class Region {

    @Command(names = {"rg", "region"}, permissionNode = "terrafirma.use")
    public static void region(Player sender) {
        sender.sendMessage(new String[]{

                " §e§m------§r §7TerraFirma Help §e§m------",
                " §e/rg §7- displays this message",
                " §e/rg tool §7- gives you the region tool",
                " §e/rg clear §7- clears your selection",
                " §e/rg list §7- shows existing claims",
                " §e/rg map §7- toggles the region map",
                " §e/rg chunkmap §7- shows a map of chunks",
                " §e/rg create <name> §7- creates a claim",
                " §e/rg del <name> §7- deletes a claim",

        });
    }
}
