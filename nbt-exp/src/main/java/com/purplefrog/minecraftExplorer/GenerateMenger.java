package com.purplefrog.minecraftExplorer;

import org.jnbt.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/5/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateMenger
{

    public static void main(String[] argv)
        throws IOException
    {
        File saveDir;
        saveDir = new File("/home/thoth/.minecraft/saves/menger-5");


        MinecraftWorld world = new MinecraftWorld(saveDir);

        BlockEditor editor = new BlockEditor(world);

        System.out.println("generating Menger Sponge geometry");
        int m5 = 3 * 3 * 3 * 3 * 3;
        for (int x = 0; x< m5; x++) {
            for (int y = 0; y< m5; y++) {
                for (int z = 0; z< m5; z++)
                {

                    int bt = decideBlockType(x,y,z);
                    editor.setBlock(x-m5-100,y+5,z, (byte) bt);
                }
            }
        }

        {
            System.out.println("relighting");

            long start = System.currentTimeMillis();
            editor.relight();
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("lighting pass: "+elapsed+" ms");
        }

        System.out.println("saving");

        editor.save();
    }

    public static int decideBlockType(int x, int y, int z)
    {
        int x1 = x/9;
        int y1 = y/9;
        int z1 = z/9;

        if (isGap(x1,y1,z1))
            return 0;

        int x9 = x%9;
        int y9 = y%9;
        int z9 = z%9;

        int shellType = (x1+y1+z1)%2 ==0 ?
            41:57 // gold and diamond
//            9:11 // water and lava
            ;
        int floorType =
            49 // obsidian
//            20 // glass
            ;

        if (x9==0 || x9==8 || y9==0 || y9==8 || z9==0 || z9==8) {
            if (isGap(x9,y9,z9))
                return 0;
            else
                return shellType;
        }

        if (y9==2)
            return floorType;

        if (y9==6 && (
            x9==4 && (z9==1 || z9==7)
                ||
                z9==4 && (x9==1||x9==7))
            )
            return 50;

        return 0;
    }

    public static boolean isGap(int x, int y, int z)
    {
        if ( (x%3==1?1:0)
            + (y%3==1?1:0)
            +(z%3==1?1:0)  >=2)
            return true;

        if (x>0 || y>0 || z>0)
            return isGap(x/3, y/3, z/3);
        return false;
    }
}
