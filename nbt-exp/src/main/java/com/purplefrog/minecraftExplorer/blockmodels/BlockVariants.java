package com.purplefrog.minecraftExplorer.blockmodels;

import com.purplefrog.minecraftExplorer.*;
import org.apache.log4j.*;

import java.util.*;

/**
 * Created by thoth on 9/26/14.
 */
public class BlockVariants
{
    private static final Logger logger = Logger.getLogger(BlockVariants.class);

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

    public OneBlockModel getVariant(int blockType, int blocKData, BlockEnvironment env)
    {

        List<OneBlockModel> x = matchVariant(blockType, blocKData, env);

        if (null==x) {


            // XXX not right
            x = variants.get(variants.size()-1).model;
        }

        int idx = rand.nextInt(x.size());
        return x.get(idx);
    }

    public List<OneBlockModel> matchVariant(int blockType, int blockData, BlockEnvironment env)
    {
//        if (true)
//            return null;

        for (OneBlockModel.Named variant : variants) {
            if (matchAll(variant.name, blockType, blockData, env))
                return variant.model;
        }

        if (variants.size()==1 && variants.get(0).name.equals("normal"))
            return null;

        return null;
    }

    public boolean matchAll(String variantSpec, int blockType, int blockData, BlockEnvironment env)
    {
        String[] parts = variantSpec.split(",");
        for (String part : parts) {
            if (!matches(part, blockType, blockData, env))
                return false;
        }
        return true;
    }

    public boolean matches(String part, int blockType, int blockData, BlockEnvironment env)
    {
        if (part.startsWith("facing=")) {
            return part.equals("facing="+ facingName(blockType, blockData));
        } else if (part.startsWith("axis=")) {
            return part.equals("axis="+axisName(blockType, blockData));
        } else if (part.startsWith("half=")) {
            return part.equals("half=" + halfName(blockType, blockData));
        } else if (part.startsWith("shape=")) {
            return part.equals("shape=straight"); // XXX
        } else if (part.startsWith("north=")||
            part.startsWith("south=")||
            part.startsWith("east=")||
            part.startsWith("up=")||
            part.startsWith("down=")||
            part.startsWith("west=")) {

            int split = part.indexOf('=');
            BlockEnvironment.Orientation dir = BlockEnvironment.Orientation.valueOf(part.substring(0, split));

            String tail = part.substring(split +1);
            boolean actual = env.fenceConnectivity[dir.ordinal()];
            String actual_ = Boolean.toString(actual);
            return tail.equals(actual_);
        } else {
            System.err.println("unrecognized variant criteria "+part);
            return false;
        }
    }

    public final static String[] LOG_FACING = "y z x none".split(" +");
    private String axisName(int blockType, int blockData)
    {
        int idx = 3&(blockData >> 2);
        return LOG_FACING[idx];
    }

    public static String facingName(int blockType, int blockData)
    {
        if (blockType == BlockDatabase.BLOCK_TYPE_DISPENSER) {
            return getOrNull(blockData&7, "down", "up", "north", "south", "east", "west");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_TORCH) {
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

    public static String halfName(int blockType, int blockData)
    {
        if (blockType == BlockDatabase.BLOCK_TYPE_STONE_BRICK_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_QUARTZ_STAIRS) {
            return getOrNull(1&(blockData>>3), "bottom", "top");
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
