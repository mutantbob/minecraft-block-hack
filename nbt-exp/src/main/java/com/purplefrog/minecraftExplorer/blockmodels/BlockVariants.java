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
        if (part.equals("normal")) {
            return true; // XXX
        } else if (part.startsWith("facing=")) {
            return part.equals("facing="+ facingName(blockType, blockData, env));
        } else if (part.startsWith("axis=")) {
            return part.equals("axis="+axisName(blockType, blockData));
        } else if (part.startsWith("half=")) {
            return part.equals("half=" + halfName(blockType, blockData));
        } else if (part.startsWith("shape=")) {
            return part.equals("shape="+ shapeName(blockType, blockData)); // XXX
        } else if (part.startsWith("snowy=")) {
            return false; // XXX
        } else if (part.startsWith("age=")) {
            return part.equals("age="+ageValue(blockType, blockData));
        } else if (part.startsWith("moisture=")
            || part.startsWith("stage=")
            || part.startsWith("bites=")
            || part.startsWith("level=")
            || part.startsWith("power=")
            ) {
            int arg = numericCriteriaArg(part);
            return blockData == arg;
        } else if (part.startsWith("layers=")) {
            int arg = numericCriteriaArg(part);
            return blockData+1 == arg;
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
        } else if (part.startsWith("powered=")) {
            return part.equals("powered="+ poweredValue(blockType,  blockData)); // XXX
        } else if (part.startsWith("extended=")) {
            return false; // XXX
        } else if (part.startsWith("alt=")) {
            return false; // XXX
        } else if (part.startsWith("wet=")) {
            return part.equals("wet="+(blockData>=7));
        } else if (part.startsWith("variant=")) {
            return false; // XXX
        } else if (part.startsWith("has_bottle_0=")) {
            boolean hasBottle0 = 0 != (blockData & 1);
            return part.equals("has_bottle_0="+ hasBottle0);
        } else if (part.startsWith("has_bottle_1=")) {
            boolean hasBottle1 = 0 != (blockData & 2);
            return part.equals("has_bottle_1="+ hasBottle1);
        } else if (part.startsWith("has_bottle_2=")) {
            boolean hasBottle2 = 0 != (blockData & 4);
            return part.equals("has_bottle_2="+ hasBottle2);
        } else if(part.startsWith("delay=")) {
            int delay = blockData>>2;
            return delay == numericCriteriaArg(part);
        } else if (part.startsWith("eye=")) {
            boolean eye = 0 != (blockData&4);
            return part.equals("eye="+ eye);
        } else if (part.startsWith("attached=")) {
            boolean attached = 0 != (blockData&4);
            return part.equals("attached="+ attached);
        } else if (part.startsWith("contents=")) {
            return part.equals("contents="+ contentsString(blockType,  blockData));
        } else if (part.startsWith("open=")) {
            return part.equals("open="+ openValue(blockType, blockData));
        } else if (part.startsWith("in_wall=")) {
            return part.equals("in_wall="+ false); // XXX
        } else {
            System.err.println("unrecognized variant criteria "+part);
            return false;
        }
    }

    public static boolean openValue(int blockType, int blockData)
    {
        if (blockType==BlockDatabase.BLOCK_TYPE_TRAPDOOR
            || blockType == BlockDatabase.BLOCK_TYPE_IRON_TRAPDOOR) {
            return 0 != (blockData & 4);
        } else if (isFenceGate(blockType)) {
            return 0 != (blockData&4);
        } else {
            return false;
        }
    }

    public boolean poweredValue(int blockType, int blockData)
    {
        if (isRail(blockType))
            return 0 != (blockData&8);

        return false;
    }

    public static String shapeName(int blockType, int blockData)
    {
        if (isRail(blockType)) {
            return getOrNull(blockData&7, "north_south", "east_west", "ascending_east", "ascending_west", "ascending_north", "ascending_south", "south_east", "south_west", "north_west", "north_east");
        } else if (isStairs(blockType)) {
            return "straight";
        } else {
            return "straight";
        }
    }

    public static boolean isRail(int blockType)
    {
        return blockType == BlockDatabase.BLOCK_TYPE_RAIL
            ||blockType == BlockDatabase.BLOCK_TYPE_ACTIVATOR_RAIL
            ||blockType == BlockDatabase.BLOCK_TYPE_DETECTOR_RAIL
            ||blockType == BlockDatabase.BLOCK_TYPE_GOLDEN_RAIL;
    }

    private String contentsString(int blockType, int blockData)
    {
        if (blockType == BlockDatabase.BLOCK_TYPE_FLOWER_POT) {
            // before 1.7:
            return getOrNull(blockData, "empty", "rose", "dandelion", "oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "mushroom_red", "mushroom_brown", "cactus", "dead_bush", "fern", "acacia_sapling", "dark_oak_sapling");
            // >=1.7 we need the Tile Entity to figure out what is inside
        } else {
            return null;
        }
    }

    private int ageValue(int blockType, int blockData)
    {
        if (blockType == BlockDatabase.BLOCK_TYPE_COCOA) {
            return (blockData >> 2);
        } else if (blockType == BlockDatabase.BLOCK_TYPE_WHEAT
            || blockType == BlockDatabase.BLOCK_TYPE_CARROTS
            || blockType == BlockDatabase.BLOCK_TYPE_POTATOES) {
            return blockData;
        } else {
            return blockData; // XXX
        }
    }

    public int numericCriteriaArg(String part)
    {
        int arg = 0;
        try {
            arg = Integer.parseInt(part.substring(part.indexOf('=')+1));
        } catch (NumberFormatException e) {
            logger.warn("bad blockstate variant criteria "+part);
        }
        return arg;
    }

    public final static String[] LOG_FACING = "y z x none".split(" +");
    private String axisName(int blockType, int blockData)
    {
        int idx = 3&(blockData >> 2);
        return LOG_FACING[idx];
    }

    public static String facingName(int blockType, int blockData, BlockEnvironment env)
    {
        if (blockType == BlockDatabase.BLOCK_TYPE_DISPENSER
            || blockType == BlockDatabase.BLOCK_TYPE_DROPPER) {
            return getOrNull(blockData&7, "down", "up", "north", "south", "east", "west");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_TORCH
            || blockType == BlockDatabase.BLOCK_TYPE_UNLIT_REDSTONE_TORCH
            || blockType == BlockDatabase.BLOCK_TYPE_REDSTONE_TORCH) {
            return getOrNull(blockData, torch_facings);
        } else if (blockType == BlockDatabase.BLOCK_TYPE_LADDER
            || blockType == BlockDatabase.BLOCK_TYPE_FURNACE
            || blockType == BlockDatabase.BLOCK_TYPE_LIT_FURNACE) {
            return getOrNull(blockData, null, null, "north", "south", "west", "east");
        } else if (isStairs(blockType)) {
            return getOrNull(blockData&3, "east", "west", "south", "north");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_PUMPKIN
            || blockType == BlockDatabase.BLOCK_TYPE_LIT_PUMPKIN) {
            return getOrNull(blockData, "south", "west", "north", "east");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_PUMPKIN_STEM
            ||blockType == BlockDatabase.BLOCK_TYPE_MELON_STEM) {
            for (BlockEnvironment.Orientation or : new BlockEnvironment.Orientation[] {
                BlockEnvironment.Orientation.north, BlockEnvironment.Orientation.south, BlockEnvironment.Orientation.east, BlockEnvironment.Orientation.west
            }) {
                if (env.fenceConnectivity[or.ordinal()])
                    return or.name();
            }
            return "up";
        } else if (blockType == BlockDatabase.BLOCK_TYPE_TRAPDOOR
            || blockType == BlockDatabase.BLOCK_TYPE_IRON_TRAPDOOR) {
            return getOrNull(blockData&3, "north", "south", "west", "east");
        } else if (blockType==BlockDatabase.BLOCK_TYPE_BED) {
            // XXX
            return null;
        } else if (blockType == BlockDatabase.BLOCK_TYPE_WOODEN_DOOR) {
            return getOrNull(blockData&3, "west", "north", "east", "south");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_LEVER) {
            return getOrNull(blockData&7, "east", "west", "south", "north", "down_z", "down_x", "up_z", "up_x");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_COCOA) {
            return getOrNull(blockData&3, "north", "east", "south", "west");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_HOPPER) {
            return getOrNull(blockData&7, "down", "bogus", "north", "south", "west", "east");
        } else if (isFenceGate(blockType)) {
            return getOrNull(blockData&3, "south", "west", "north", "east");
        }

        return null;
    }

    private static boolean isFenceGate(int blockType)
    {
        return blockType == BlockDatabase.BLOCK_TYPE_FENCE_GATE
            || blockType == BlockDatabase.BLOCK_TYPE_SPRUCE_FENCE_GATE
            || blockType == BlockDatabase.BLOCK_TYPE_BIRCH_FENCE_GATE
            || blockType == BlockDatabase.BLOCK_TYPE_JUNGLE_FENCE_GATE
            || blockType == BlockDatabase.BLOCK_TYPE_ACACIA_FENCE_GATE
            || blockType == BlockDatabase.BLOCK_TYPE_DARK_OAK_FENCE_GATE
            ;
    }

    public static boolean isStairs(int blockType)
    {
        return blockType == BlockDatabase.BLOCK_TYPE_OAK_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_STONE_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_BRICK_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_STONE_BRICK_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_NETHER_BRICK_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_SANDSTONE_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_SPRUCE_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_BIRCH_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_JUNGLE_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_QUARTZ_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_ACACIA_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_DARK_OAK_STAIRS
            || blockType == BlockDatabase.BLOCK_TYPE_RED_SANDSTONE_STAIRS;
    }

    public static boolean isDoor(int blockType)
    {
        return blockType == BlockDatabase.BLOCK_TYPE_WOODEN_DOOR
            || blockType == BlockDatabase.BLOCK_TYPE_SPRUCE_DOOR
            || blockType == BlockDatabase.BLOCK_TYPE_BIRCH_DOOR
            || blockType == BlockDatabase.BLOCK_TYPE_JUNGLE_DOOR
            || blockType == BlockDatabase.BLOCK_TYPE_ACACIA_DOOR
            || blockType == BlockDatabase.BLOCK_TYPE_DARK_OAK_DOOR
            || blockType == BlockDatabase.BLOCK_TYPE_IRON_DOOR
            ;
    }

    public static String halfName(int blockType, int blockData)
    {
        if (isStairs(blockType)) {
            return getOrNull(1 & (blockData >> 2), "bottom", "top");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_WOODEN_SLAB
            || blockType == BlockDatabase.BLOCK_TYPE_STONE_SLAB
            || blockType == BlockDatabase.BLOCK_TYPE_STONE_SLAB2) {
            return getOrNull(1 & (blockData >> 3), "bottom", "top");
        } else if (blockType == BlockDatabase.BLOCK_TYPE_TRAPDOOR
            || blockType == BlockDatabase.BLOCK_TYPE_IRON_TRAPDOOR) {
            return (0 != (blockData & 8)) ? "top" : "bottom";
        } else if (blockType == BlockDatabase.BLOCK_TYPE_DOUBLE_PLANT) {
            return ( 0 != (blockData&8)) ? "upper": "lower";
        } else if (isDoor(blockType))
            return (0 != (blockData&8)) ? "upper" : "lower";

        return null;
    }

    public static <T> T getOrNull(int idx, T... array)
    {
        if (idx < array.length)
            return array[idx];
        return null;
    }
}
