package com.purplefrog.minecraftExplorer;

import org.apache.log4j.*;
import org.jnbt.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/4/13
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockVoxels
{
    private static final Logger logger = Logger.getLogger(BlockVoxels.class);


    MinecraftWorld world ;

    ChunkTagCache chunkCache ;

    public BlockVoxels(MinecraftWorld world)
    {
        this.world = world;
        chunkCache = new ChunkTagCache(world);
    }

    public int getBlockType(int x, int y, int z)
    {
        try {
            CompoundTag tag = chunkCache.getForRaw(x,z);
            if (tag==null)
                return -1;

            Anvil anvil = new Anvil(tag);

            Anvil.Section s0 = anvil.getSectionFor(y);

            if (s0==null)
                return -1;

            int pos = encodePos(x, y, z);
            return s0.getBlocks()[pos];

        } catch (IOException e) {
            logger.debug("malfunction getting chunk", e);
            return 0;
        }
    }

    public static int encodePos(int x, int y, int z)
    {
        return (y&0xf)<<8
                    |
                    (z&0xf)<<4
                    |
                    (x&0xf);
    }
}
