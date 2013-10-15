package com.purplefrog.minecraftExplorer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/12/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipArt
{
    public static SkyScraper1.WindowShape glassPlusFloor(int glassU, int glassV, BlockPlusData floor)
    {
        return somethingPlusFloor(glassU, glassV, floor, new BlockPlusData(102));
    }

    private static SkyScraper1.WindowShape somethingPlusFloor(int glassU, int glassV, BlockPlusData floor, BlockPlusData something)
    {
        BlockPlusData[] blocks = new BlockPlusData[glassU * (glassV + 1)];
        for (int u=0; u<glassU; u++) {
            for (int v=0; v<=glassV; v++) {
                blocks[u+v*glassU] = v==0 ? floor : something;
            }
        }
        return new SkyScraper1.WindowShape(glassU+1, blocks);
    }

    public static Object[] randomBuildingDecor(Random rand)
    {
        int w1 = rand.nextInt(3) +2;
        int w2 = rand.nextInt(3) +2;
        int floorHeight = rand.nextInt(2)+4;

        int decor = rand.nextInt(4);
        return getDecor(w1, w2, floorHeight, decor);
    }

    public static Object[] getDecor(int w1, int w2, int floorHeight, int decor)
    {
        int headRoom = floorHeight-1;
        SkyScraper1.WindowShape window1;
        SkyScraper1.WindowShape window2;
        int[] column;
        switch (decor) {
            case 0:
                window1 = ClipArt.glassPlusFloor(w1, headRoom, BlockDatabase.chiseledStone);
                window2 = ClipArt.glassPlusFloor(w2, headRoom, BlockDatabase.chiseledStone);
                column = columnA(floorHeight, 42);
                break;
            case 1:
                window1 = ClipArt.glassPlusFloor(w1, headRoom, new BlockPlusData(112));
                window2 = ClipArt.glassPlusFloor(w2, headRoom, new BlockPlusData(112));
                column = columnA(floorHeight, 49);
                break;
            case 2:
                window1 = ClipArt.somethingPlusFloor(w1, headRoom, new BlockPlusData(112), new BlockPlusData(101));
                window2 = ClipArt.somethingPlusFloor(w2, headRoom, new BlockPlusData(112), new BlockPlusData(101));
                column = columnA(floorHeight, 49);
                break;
            case 3:
                window1 = ClipArt.glassPlusFloor(w1, headRoom, new BlockPlusData(45));
                window2 = ClipArt.glassPlusFloor(w2, headRoom, new BlockPlusData(45));
                column = columnA(floorHeight, 45);
                break;
            default:
                throw new IllegalStateException();
        }

        return new Object[]{ window1, window2, column};
    }

    public static int[] columnA(int count, int val)
    {
        int[] rval = new int[count];
        Arrays.fill(rval, val);
        return rval;
    }
}
