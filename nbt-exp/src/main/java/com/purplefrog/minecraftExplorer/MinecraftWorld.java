package com.purplefrog.minecraftExplorer;

import net.minecraft.world.chunk.storage.*;
import org.jnbt.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/3/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinecraftWorld
{
    private File saveDir;

    public MinecraftWorld(File saveDir)
    {
        this.saveDir = saveDir;
    }

    public Tag makeChunkFor(int chunkX, int chunkZ)
        throws IOException
    {

        RegionFile rf = getRegionFile(chunkX, chunkZ);
        if (rf == null) return null;

        DataInputStream dis = rf.getChunkDataInputStream(chunkX & 0x1f, chunkZ & 0x1f);
        if (null==dis)
            return null;

        NBTInputStream nis = new NBTInputStream(dis, true);
        return nis.readTag();
    }

    public RegionFile getRegionFile(int chunkX, int chunkZ)
    {
        int metaX = chunkX >> 5;
        int metaZ = chunkZ >> 5;
        return getRegionFile_(metaX, metaZ);

    }

    public RegionFile getRegionFile_(int metaX, int metaZ)
    {
        return RegionFileCache.getRegionFile(metaX, metaZ, saveDir);
    }

}
