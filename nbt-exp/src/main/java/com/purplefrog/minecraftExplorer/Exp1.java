package com.purplefrog.minecraftExplorer;

import com.mojang.nbt.*;
import net.minecraft.world.chunk.storage.*;

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

        exp3(saveDir);
    }

    private static void exp3(File saveDir)
        throws IOException
    {
        MinecraftWorld w = new MinecraftWorld(saveDir);

        ChunkTagCache cc = new ChunkTagCache(w);

        CompoundTag t = cc.getForRaw(-312, 128);

        Anvil anvil = new Anvil(t);
        NibbleCube sl = anvil.getSectionFor(0).getSkyLight();

        System.out.println(t);

        System.out.println(dumpLightMap(sl));


//            System.out.println("\n\n\n\n\n\n\n\n");
//            System.out.println(cc.getForRaw( 100, 0));
    }

    private static void exp2(File saveDir)
        throws IOException
    {
        File  f;
        f = new File(saveDir, "region/r.-1.0.mca");

        RegionFile rf = new RegionFile(f);

        DataInputStream is1 = rf.getChunkDataInputStream(0, 0);

        Tag t = NbtIo.read(is1);
        System.out.println(t);
    }

    public static StringBuilder dumpLightMap(NibbleCube sl)
    {
        StringBuilder buf = new StringBuilder();
        for (int y=0; y<16; y++) {
            buf.append("y="+y+"\n");
            for (int z=0; z<16; z++) {
                for (int x=0; x<16; x++) {
                    int i = sl.get(x, y, z);
                    String padded = i+" ";
                    while (padded.length()<3)
                        padded = " "+padded;
                    buf.append(padded);
                }
                buf.append("\n");
            }
        }
        return buf;
    }

    public static void exp1(File saveDir)
        throws IOException
    {
        File  f;
        f = new File(saveDir, "level.dat");
        f = new File(saveDir, "data/map_0.dat");
//        f = new File(saveDir, "region/r.0.0.mca");

        InputStream i9 = new FileInputStream(f);

        Tag t = NbtIo.read(f);

        System.out.println(t);
    }
}
