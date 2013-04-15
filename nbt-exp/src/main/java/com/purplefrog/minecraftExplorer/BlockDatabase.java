package com.purplefrog.minecraftExplorer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/4/13
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockDatabase
{
    public final static BlockPlusData chiseledStone = new BlockPlusData(98, 3);
    public static Map<Integer, int[]> unknownBlockTypes = new TreeMap<Integer, int[]>();

    public static int lightLevel(int blockType)
    {
        switch (blockType) {
            case 10: // lava
            case 11: // lava
            case 89: // glowstone ore
            case 124: // glowstone lamp
                return 15;
            case 50: // torch
                return 14;
            default:
                return 0;
        }
    }

    enum TransparencyClass
    {
        Solid,
        Transparent,
        Water,
        Widget,
    }

    public static TransparencyClass[] TRANSPARENCY_CLASS = {
        TransparencyClass.Transparent,  // 0
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 5
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Water,
        TransparencyClass.Water,

        TransparencyClass.Solid,   // 10
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 15
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Solid,

        TransparencyClass.Widget,   // 20
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 25
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,

        TransparencyClass.Widget,   // 30
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 35
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,

        TransparencyClass.Widget,   // 40
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 45
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,

        TransparencyClass.Widget,   // 50
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 55
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Widget,

        TransparencyClass.Solid,   // 60
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,   // 65
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,

        TransparencyClass.Widget,   // 70
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Widget,   // 75
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,

        TransparencyClass.Solid,   // 80
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Widget,   // 85
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,

        TransparencyClass.Solid,   // 90
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,   // 95
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,

        TransparencyClass.Solid,   // 100
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Widget,   // 105
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,

        TransparencyClass.Solid,   // 110
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,   // 115
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,

        TransparencyClass.Solid,   // 120
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 125
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,

        TransparencyClass.Solid,   // 130
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Widget,   // 135
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Widget,

        TransparencyClass.Widget,   // 140
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 145
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,

        TransparencyClass.Widget,   // 150
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Solid,   // 155
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,

    };

    public static boolean transparent(int blockType)
    {
        switch ( tClass(blockType) ) {
            case Widget:
            case Transparent:
                return true;
            default:
                return false;
        }
    }

    public static TransparencyClass tClass(int blockType)
    {
        if (blockType <0 || blockType >= TRANSPARENCY_CLASS.length) {
            return TransparencyClass.Solid;
        }

        return TRANSPARENCY_CLASS[blockType];
    }

    public static int[] colorForBlockType(int blockType)
    {
        switch (blockType)
        {
            case 1:// stone
            case 4: // cobblestone
            case 13: //gravel
            case 15: // iron ore
            case 82: // clay
            case 98: // stone brick
                return new int[]{160,160,160};
            case 2: // grass
            case 31: // tall grass
                return new int[]{0,255,0};
            case 18: // leaves
                return new int[]{ 0,160,0};
            case 3:// dirt
            case 5: // wood plank
            case 17: // wood
                return new int[]{128,100,0};
            case 8: // water
            case 9: // stationary water
                return new int[]{0,0,255};
            case 12: // sand
            case 24: // sandstone
                return new int[]{255,255,128};
            case 16:
                return new int[]{100,100,100};

            case 50: // torch
            case 78: // snow
            case 79: // ice
                return new int[]{255,255,255};

            default:
                int[] count = unknownBlockTypes.get(blockType);
                if (null == count) {
                    System.out.println("unknown block type "+blockType);
                    count = new int[]{1};
                    unknownBlockTypes.put(blockType, count);
                } else {
                    count[0] ++;
                }

                return new int[]{255,0,255};
        }
    }
}
