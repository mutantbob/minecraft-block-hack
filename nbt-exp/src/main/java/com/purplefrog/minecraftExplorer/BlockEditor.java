package com.purplefrog.minecraftExplorer;

import javafx.geometry.*;
import net.minecraft.world.chunk.storage.*;
import org.jnbt.*;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/5/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockEditor
{
    protected final ChunkTagCache chunkTagCache;
    private MinecraftWorld world;
    private Map<Point3D, Anvil.Section> sectionCache = new HashMap<Point3D, Anvil.Section>();

    public BlockEditor(MinecraftWorld world)
    {
        this.world = world;
        chunkTagCache = new ChunkTagCache(this.world);
    }

    public void setBlock(int x, int y, int z, byte bt)
        throws IOException
    {
        Anvil.Section s = getSection(x, y, z);


        s.getBlocks()[BlockVoxels.encodePos(x,y,z)] = bt;
    }

    protected Anvil.Section getSection(int x, int y, int z)
        throws IOException
    {
        Point3D key = new Point3D(x>>4, y>>4, z>>4);
        Anvil.Section rval = sectionCache.get(key);

        if (null==rval) {
            CompoundTag chunk = chunkTagCache.getForChunk(x >> 4, z >> 4);
            Anvil anvil = new Anvil(chunk);

            rval = anvil.getOrCreateSectionFor(y);
            sectionCache.put(key, rval);
        }

        return rval;
    }

    public void save()
        throws IOException
    {
        for (Map.Entry<Point, CompoundTag> en : chunkTagCache.cache.entrySet()) {
            Point chunkXz = en.getKey();
            RegionFile rf = world.getRegionFile(chunkXz.x, chunkXz.y);

            CompoundTag tag = en.getValue();
            ByteArrayOutputStream ostr = new ByteArrayOutputStream();
            NBTOutputStream o2 = new NBTOutputStream(ostr, false);
            o2.writeTag(tag);
            o2.close();
            byte[] chunkBytes = ostr.toByteArray();
            System.out.println("chunk "+chunkXz+" ["+chunkBytes.length+"]");
            rf.write(chunkXz.x&0x1f, chunkXz.y&0x1f, chunkBytes, chunkBytes.length);
        }
    }
}
