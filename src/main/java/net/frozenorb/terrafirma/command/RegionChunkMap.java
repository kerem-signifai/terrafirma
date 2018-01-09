package net.frozenorb.terrafirma.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.terrafirma.menu.ChunkMapMenu;
import org.bukkit.entity.Player;

public class RegionChunkMap {

    @Command(names = {"rg cm", "rg chunkmap", "region chunkmap"}, permissionNode = "terrafirma.use")
    public static void regionChunkMap(Player sender) {

        new ChunkMapMenu().openMenu(sender);
    }
}
