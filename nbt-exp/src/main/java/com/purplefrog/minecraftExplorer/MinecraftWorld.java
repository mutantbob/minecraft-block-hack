package com.purplefrog.minecraftExplorer;

import com.mojang.nbt.*;
import net.minecraft.world.chunk.storage.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

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

        return NbtIo.read(dis);
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

    public Iterable<RegionFile> allRegions()
    {
        return new Iterable<RegionFile>()
        {
            @Override
            public Iterator<RegionFile> iterator()
            {
                return new RegionFileIterator();
            }
        };
    }

    private class RegionFileIterator
        implements Iterator<RegionFile>
    {
        private RegionFile cache;
        protected File[] fs;
        private int dirPtr=0;

        public RegionFileIterator()
        {
            File dir = new File(saveDir, "region");
            fs = dir.listFiles();
            if (fs==null)
                fs = new File[0];
        }

        @Override
        public boolean hasNext()
        {
            maybeFillCache();
            return cache != null;
        }

        @Override
        public RegionFile next()
        {
            maybeFillCache();
            if (cache==null)
                throw new NoSuchElementException();
            RegionFile rval = cache;
            cache = null;
            return rval;
        }

        /**
         * @see RegionFileCache#getRegionFile(int, int, java.io.File)
         */
        private void maybeFillCache()
        {
            if (cache!=null)
                return;

            File found = nextRegionFile();
            if (found==null)
                return;

            cache = new RegionFile(found);
        }

        private File nextRegionFile()
        {
            while (dirPtr<fs.length) {
                File candidate = fs[dirPtr++];
                Pattern p = Pattern.compile("r\\.-?\\d+\\.-?\\d+\\.mca");
                Matcher m = p.matcher(candidate.getName());
                if (m.matches()) {
                    return candidate;
                }
                System.out.println("rejected "+candidate.getName());
            }
            return null;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
