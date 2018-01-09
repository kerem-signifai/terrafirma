package net.frozenorb.terrafirma.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

@AllArgsConstructor
@Data
public class VirtualChunk implements Chunk {
    private World world;
    private int x, z;

    public VirtualChunk relative(BlockFace bf) {
        return new VirtualChunk(world, x + bf.getModX(), z + bf.getModZ());
    }

    @Override
    public Block getBlock(int i, int i1, int i2) {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public ChunkSnapshot getChunkSnapshot() {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public ChunkSnapshot getChunkSnapshot(boolean b, boolean b1, boolean b2) {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public Entity[] getEntities() {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public BlockState[] getTileEntities() {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public boolean isLoaded() {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public boolean load(boolean b) {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public boolean load() {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public boolean unload(boolean b, boolean b1) {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public boolean unload(boolean b) {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }

    @Override
    public boolean unload() {
        throw new UnsupportedOperationException("This is a wrapper class; it contains no actual Chunk methods!");
    }
}
