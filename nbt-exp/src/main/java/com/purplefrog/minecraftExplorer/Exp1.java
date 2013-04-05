package com.purplefrog.minecraftExplorer;

import net.minecraft.world.chunk.storage.*;
import org.jnbt.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/3/13
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Exp1
{
    public static void main(String[] argv)
        throws IOException
    {
        File saveDir = new File("/home/thoth/.minecraft/saves/menger-5");

        File  f;
        f = new File(saveDir, "region/r.-1.0.mca");

        if (true) {
            MinecraftWorld w = new MinecraftWorld(saveDir);

            ChunkTagCache cc = new ChunkTagCache(w);

            CompoundTag t = cc.getForRaw(-100, 100);

            System.out.println(t);

            System.out.println("\n\n\n\n\n\n\n\n");
            System.out.println(cc.getForRaw( 100, 0));
        }else {
            RegionFile rf = new RegionFile(f);

            DataInputStream is1 = rf.getChunkDataInputStream(0, 0);



            NBTInputStream i = new NBTInputStream(is1, false);

            Tag t = i.readTag();
            System.out.println(t);
        }
    }

    public static void exp1(File saveDir)
        throws IOException
    {
        File  f;
        f = new File(saveDir, "level.dat");
        f = new File(saveDir, "data/map_0.dat");
//        f = new File(saveDir, "region/r.0.0.mca");

        InputStream i9 = new FileInputStream(f);

        NBTInputStream i = new NBTInputStream(i9);

        Tag t = i.readTag();

        System.out.println(t);
    }
}
