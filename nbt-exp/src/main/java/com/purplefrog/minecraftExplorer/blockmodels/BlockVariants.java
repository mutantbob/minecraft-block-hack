package com.purplefrog.minecraftExplorer.blockmodels;

import com.purplefrog.minecraftExplorer.*;

import java.util.*;

/**
 * Created by thoth on 9/26/14.
 */
public class BlockVariants
{
    private final List<OneBlockModel.Named> variants;

    public BlockVariants(List<OneBlockModel.Named> v2)
    {
        this.variants = v2;
    }

    public static Random rand = new Random();

    public static String[] torch_facings = {
        "down",
        "east",
        "west",
        "south",
        "north",
        "up"
    };

    public OneBlockModel getVariant(int blockType, int blocKData)
    {

        List<OneBlockModel> x = matchVariant(blockType, blocKData);

        if (null==x) {


            // XXX not right
            x = variants.get(variants.size()-1).model;
        }

        int idx = rand.nextInt(x.size());
        return x.get(idx);
    }

    public List<OneBlockModel> matchVariant(int blockType, int blocKData)
    {
//        if (true)
//            return null;

        for (OneBlockModel.Named variant : variants) {
            if (variant.name.startsWith("facing=")) {
                if (variant.name.equals("facing="+ facingName(blockType, blocKData))) {
                    return variant.model;
                }
            }
        }

        if (variants.size()==1 && variants.get(0).name.equals("normal"))
            return null;

        return null;
    }

    public static String facingName(int blockType, int blockData)
    {
        if (blockType == BlockDatabase.BLOCK_TYPE_TORCH) {
            return getOrNull(blockData, torch_facings);
        } else if (blockType == BlockDatabase.BLOCK_TYPE_LADDER) {
            return getOrNull(blockData, null, null, "north", "south", "west", "east");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_STONE_BRICK_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_QUARTZ_STAIRS) {
            return getOrNull(blockData&3, "east", "west", "south", "north");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_PUMPKIN) {
            return getOrNull(blockData, "south", "west", "north", "east");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_PUMPKIN_STEM
            ||blockType == BlockDatabase.BLOCK_TYPE_MELON_STEM) {
            return "south"; // XXX wrong
        } else if (blockType == BlockDatabase.BLOCK_TYPE_TRAPDOOR) {
            return getOrNull(blockData, "south,half=bottom,open=false",
                "north,half=bottom,open=false",
                "east,half=bottom,open=false",
                "west,half=bottom,open=false",
                "south,half=bottom,open=true",
                "north,half=bottom,open=true",
                "east,half=bottom,open=true",
                "west,half=bottom,open=true",
                "south,half=top,open=false",
                "north,half=top,open=false",
                "east,half=top,open=false",
                "west,half=top,open=false",
                "south,half=top,open=true",
                "north,half=top,open=true",
                "east,half=top,open=true",
                "west,half=top,open=true");
        } else if (blockType==26) {
            // XXX
            return null;
        }

        return null;
    }

    public static <T> T getOrNull(int idx, T... array)
    {
        if (idx < array.length)
            return array[idx];
        return null;
    }
}
