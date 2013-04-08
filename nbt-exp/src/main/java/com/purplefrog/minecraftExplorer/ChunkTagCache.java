package com.purplefrog.minecraftExplorer;

import com.mojang.nbt.*;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/4/13
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChunkTagCache
{
    private MinecraftWorld world;
    public Map<Point, CompoundTag> cache = new HashMap<Point, CompoundTag>();

    public ChunkTagCache(MinecraftWorld world)
    {
        this.world = world;
    }

    public CompoundTag getForRaw(int x, int z)
        throws IOException
    {
        return getForChunk(x >> 4, z >> 4);
    }

    public CompoundTag maybeGetForChunk(int chunkX, int chunkZ)
        throws IOException
    {
        Point key = new Point(chunkX, chunkZ);
        return cache.get(key);
    }

    public CompoundTag getForChunk(int chunkX, int chunkZ)
        throws IOException
    {
        Point key = new Point(chunkX, chunkZ);
        CompoundTag rval = cache.get(key);
        if (null==rval) {
            rval = (CompoundTag) world.makeChunkFor(key.x, key.y);
            cache.put(key, rval);
        }
        return rval;
    }
}
