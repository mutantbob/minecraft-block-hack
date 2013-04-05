package com.purplefrog.minecraftExplorer;

import net.minecraft.world.chunk.storage.*;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/3/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegionFileCache
{
    private static Map<Point, RegionFile> cache = new HashMap<Point, RegionFile>();

    public static RegionFile getRegionFile(int metaX, int metaZ, File saveDir)
    {
        Point key = new Point(metaX, metaZ);

        RegionFile rval = cache.get(key);

        if (rval==null) {

            File f = new File(saveDir, "region/r."+metaX+"."+metaZ+".mca");
            if (!f.exists())
                return null;

            rval = new RegionFile(f);
            cache.put(key, rval);
        }
        return rval;
    }
}
