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

    public static final int BLOCK_TYPE_AIR = 0;
    public static final int BLOCK_TYPE_STONE = 1;
    public static final int BLOCK_TYPE_GRASS = 2;
    public static final int BLOCK_TYPE_DIRT = 3;
    public static final int BLOCK_TYPE_COBBLESTONE = 4;
    public static final int BLOCK_TYPE_PLANKS = 5;
    public static final int BLOCK_TYPE_SAPLING = 6;
    public static final int BLOCK_TYPE_BEDROCK = 7;
    public static final int BLOCK_TYPE_FLOWING_WATER = 8;
    public static final int BLOCK_TYPE_WATER = 9;
    public static final int BLOCK_TYPE_FLOWING_LAVA = 10;
    public static final int BLOCK_TYPE_LAVA = 11;
    public static final int BLOCK_TYPE_SAND = 12;
    public static final int BLOCK_TYPE_GRAVEL = 13;
    public static final int BLOCK_TYPE_GOLD_ORE = 14;
    public static final int BLOCK_TYPE_IRON_ORE = 15;
    public static final int BLOCK_TYPE_COAL_ORE = 16;
    public static final int BLOCK_TYPE_LOG = 17;
    public static final int BLOCK_TYPE_LEAVES = 18;
    public static final int BLOCK_TYPE_SPONGE = 19;
    public static final int BLOCK_TYPE_GLASS = 20;
    public static final int BLOCK_TYPE_LAPIS_ORE = 21;
    public static final int BLOCK_TYPE_LAPIS_BLOCK = 22;
    public static final int BLOCK_TYPE_DISPENSER = 23;
    public static final int BLOCK_TYPE_SANDSTONE = 24;
    public static final int BLOCK_TYPE_NOTEBLOCK = 25;
    public static final int BLOCK_TYPE_BED = 26;
    public static final int BLOCK_TYPE_GOLDEN_RAIL = 27;
    public static final int BLOCK_TYPE_DETECTOR_RAIL = 28;
    public static final int BLOCK_TYPE_STICKY_PISTON = 29;
    public static final int BLOCK_TYPE_WEB = 30;
    public static final int BLOCK_TYPE_TALLGRASS = 31;
    public static final int BLOCK_TYPE_DEADBUSH = 32;
    public static final int BLOCK_TYPE_PISTON = 33;
    public static final int BLOCK_TYPE_PISTON_HEAD = 34;
    public static final int BLOCK_TYPE_WOOL = 35;
    public static final int BLOCK_TYPE_PISTON_EXTENSION = 36;
    public static final int BLOCK_TYPE_YELLOW_FLOWER = 37;
    public static final int BLOCK_TYPE_RED_FLOWER = 38;
    public static final int BLOCK_TYPE_BROWN_MUSHROOM = 39;
    public static final int BLOCK_TYPE_RED_MUSHROOM = 40;
    public static final int BLOCK_TYPE_GOLD_BLOCK = 41;
    public static final int BLOCK_TYPE_IRON_BLOCK = 42;
    public static final int BLOCK_TYPE_DOUBLE_STONE_SLAB = 43;
    public static final int BLOCK_TYPE_STONE_SLAB = 44;
    public static final int BLOCK_TYPE_BRICK_BLOCK = 45;
    public static final int BLOCK_TYPE_TNT = 46;
    public static final int BLOCK_TYPE_BOOKSHELF = 47;
    public static final int BLOCK_TYPE_MOSSY_COBBLESTONE = 48;
    public static final int BLOCK_TYPE_OBSIDIAN = 49;
    public static final int BLOCK_TYPE_TORCH = 50;
    public static final int BLOCK_TYPE_FIRE = 51;
    public static final int BLOCK_TYPE_MOB_SPAWNER = 52;
    public static final int BLOCK_TYPE_OAK_STAIRS = 53;
    public static final int BLOCK_TYPE_CHEST = 54;
    public static final int BLOCK_TYPE_REDSTONE_WIRE = 55;
    public static final int BLOCK_TYPE_DIAMOND_ORE = 56;
    public static final int BLOCK_TYPE_DIAMOND_BLOCK = 57;
    public static final int BLOCK_TYPE_CRAFTING_TABLE = 58;
    public static final int BLOCK_TYPE_WHEAT = 59;
    public static final int BLOCK_TYPE_FARMLAND = 60;
    public static final int BLOCK_TYPE_FURNACE = 61;
    public static final int BLOCK_TYPE_LIT_FURNACE = 62;
    public static final int BLOCK_TYPE_STANDING_SIGN = 63;
    public static final int BLOCK_TYPE_WOODEN_DOOR = 64;
    public static final int BLOCK_TYPE_LADDER = 65;
    public static final int BLOCK_TYPE_RAIL = 66;
    public static final int BLOCK_TYPE_STONE_STAIRS = 67;
    public static final int BLOCK_TYPE_WALL_SIGN = 68;
    public static final int BLOCK_TYPE_LEVER = 69;
    public static final int BLOCK_TYPE_STONE_PRESSURE_PLATE = 70;
    public static final int BLOCK_TYPE_IRON_DOOR = 71;
    public static final int BLOCK_TYPE_WOODEN_PRESSURE_PLATE = 72;
    public static final int BLOCK_TYPE_REDSTONE_ORE = 73;
    public static final int BLOCK_TYPE_LIT_REDSTONE_ORE = 74;
    public static final int BLOCK_TYPE_UNLIT_REDSTONE_TORCH = 75;
    public static final int BLOCK_TYPE_REDSTONE_TORCH = 76;
    public static final int BLOCK_TYPE_STONE_BUTTON = 77;
    public static final int BLOCK_TYPE_SNOW_LAYER = 78;
    public static final int BLOCK_TYPE_ICE = 79;
    public static final int BLOCK_TYPE_SNOW = 80;
    public static final int BLOCK_TYPE_CACTUS = 81;
    public static final int BLOCK_TYPE_CLAY = 82;
    public static final int BLOCK_TYPE_REEDS = 83;
    public static final int BLOCK_TYPE_JUKEBOX = 84;
    public static final int BLOCK_TYPE_FENCE = 85;
    public static final int BLOCK_TYPE_PUMPKIN = 86;
    public static final int BLOCK_TYPE_NETHERRACK = 87;
    public static final int BLOCK_TYPE_SOUL_SAND = 88;
    public static final int BLOCK_TYPE_GLOWSTONE = 89;
    public static final int BLOCK_TYPE_PORTAL = 90;
    public static final int BLOCK_TYPE_LIT_PUMPKIN = 91;
    public static final int BLOCK_TYPE_CAKE = 92;
    public static final int BLOCK_TYPE_UNPOWERED_REPEATER = 93;
    public static final int BLOCK_TYPE_POWERED_REPEATER = 94;
    public static final int BLOCK_TYPE_STAINED_GLASS = 95;
    public static final int BLOCK_TYPE_TRAPDOOR = 96;
    public static final int BLOCK_TYPE_MONSTER_EGG = 97;
    public static final int BLOCK_TYPE_STONEBRICK = 98;
    public static final int BLOCK_TYPE_BROWN_MUSHROOM_BLOCK = 99;
    public static final int BLOCK_TYPE_RED_MUSHROOM_BLOCK = 100;
    public static final int BLOCK_TYPE_IRON_BARS = 101;
    public static final int BLOCK_TYPE_GLASS_PANE = 102;
    public static final int BLOCK_TYPE_MELON_BLOCK = 103;
    public static final int BLOCK_TYPE_PUMPKIN_STEM = 104;
    public static final int BLOCK_TYPE_MELON_STEM = 105;
    public static final int BLOCK_TYPE_VINE = 106;
    public static final int BLOCK_TYPE_FENCE_GATE = 107;
    public static final int BLOCK_TYPE_BRICK_STAIRS = 108;
    public static final int BLOCK_TYPE_STONE_BRICK_STAIRS = 109;
    public static final int BLOCK_TYPE_MYCELIUM = 110;
    public static final int BLOCK_TYPE_WATERLILY = 111;
    public static final int BLOCK_TYPE_NETHER_BRICK = 112;
    public static final int BLOCK_TYPE_NETHER_BRICK_FENCE = 113;
    public static final int BLOCK_TYPE_NETHER_BRICK_STAIRS = 114;
    public static final int BLOCK_TYPE_NETHER_WART = 115;
    public static final int BLOCK_TYPE_ENCHANTING_TABLE = 116;
    public static final int BLOCK_TYPE_BREWING_STAND = 117;
    public static final int BLOCK_TYPE_CAULDRON = 118;
    public static final int BLOCK_TYPE_END_PORTAL = 119;
    public static final int BLOCK_TYPE_END_PORTAL_FRAME = 120;
    public static final int BLOCK_TYPE_END_STONE = 121;
    public static final int BLOCK_TYPE_DRAGON_EGG = 122;
    public static final int BLOCK_TYPE_REDSTONE_LAMP = 123;
    public static final int BLOCK_TYPE_LIT_REDSTONE_LAMP = 124;
    public static final int BLOCK_TYPE_DOUBLE_WOODEN_SLAB = 125;
    public static final int BLOCK_TYPE_WOODEN_SLAB = 126;
    public static final int BLOCK_TYPE_COCOA = 127;
    public static final int BLOCK_TYPE_SANDSTONE_STAIRS = 128;
    public static final int BLOCK_TYPE_EMERALD_ORE = 129;
    public static final int BLOCK_TYPE_ENDER_CHEST = 130;
    public static final int BLOCK_TYPE_TRIPWIRE_HOOK = 131;
    public static final int BLOCK_TYPE_TRIPWIRE = 132;
    public static final int BLOCK_TYPE_EMERALD_BLOCK = 133;
    public static final int BLOCK_TYPE_SPRUCE_STAIRS = 134;
    public static final int BLOCK_TYPE_BIRCH_STAIRS = 135;
    public static final int BLOCK_TYPE_JUNGLE_STAIRS = 136;
    public static final int BLOCK_TYPE_COMMAND_BLOCK = 137;
    public static final int BLOCK_TYPE_BEACON = 138;
    public static final int BLOCK_TYPE_COBBLESTONE_WALL = 139;
    public static final int BLOCK_TYPE_FLOWER_POT = 140;
    public static final int BLOCK_TYPE_CARROTS = 141;
    public static final int BLOCK_TYPE_POTATOES = 142;
    public static final int BLOCK_TYPE_WOODEN_BUTTON = 143;
    public static final int BLOCK_TYPE_SKULL = 144;
    public static final int BLOCK_TYPE_ANVIL = 145;
    public static final int BLOCK_TYPE_TRAPPED_CHEST = 146;
    public static final int BLOCK_TYPE_LIGHT_WEIGHTED_PRESSURE_PLATE = 147;
    public static final int BLOCK_TYPE_HEAVY_WEIGHTED_PRESSURE_PLATE = 148;
    public static final int BLOCK_TYPE_UNPOWERED_COMPARATOR = 149;
    public static final int BLOCK_TYPE_POWERED_COMPARATOR = 150;
    public static final int BLOCK_TYPE_DAYLIGHT_DETECTOR = 151;
    public static final int BLOCK_TYPE_REDSTONE_BLOCK = 152;
    public static final int BLOCK_TYPE_QUARTZ_ORE = 153;
    public static final int BLOCK_TYPE_HOPPER = 154;
    public static final int BLOCK_TYPE_QUARTZ_BLOCK = 155;
    public static final int BLOCK_TYPE_QUARTZ_STAIRS = 156;
    public static final int BLOCK_TYPE_ACTIVATOR_RAIL = 157;
    public static final int BLOCK_TYPE_DROPPER = 158;
    public static final int BLOCK_TYPE_STAINED_HARDENED_CLAY = 159;
    public static final int BLOCK_TYPE_STAINED_GLASS_PANE = 160;
    public static final int BLOCK_TYPE_LEAVES2 = 161;
    public static final int BLOCK_TYPE_LOG2 = 162;
    public static final int BLOCK_TYPE_ACACIA_STAIRS = 163;
    public static final int BLOCK_TYPE_DARK_OAK_STAIRS = 164;
    public static final int BLOCK_TYPE_SLIME = 165;
    public static final int BLOCK_TYPE_BARRIER = 166;
    public static final int BLOCK_TYPE_IRON_TRAPDOOR = 167;
    public static final int BLOCK_TYPE_PRISMARINE = 168;
    public static final int BLOCK_TYPE_SEA_LANTERN = 169;
    public static final int BLOCK_TYPE_HAY_BLOCK = 170;
    public static final int BLOCK_TYPE_CARPET = 171;
    public static final int BLOCK_TYPE_HARDENED_CLAY = 172;
    public static final int BLOCK_TYPE_COAL_BLOCK = 173;
    public static final int BLOCK_TYPE_PACKED_ICE = 174;
    public static final int BLOCK_TYPE_DOUBLE_PLANT = 175;
    public static final int BLOCK_TYPE_STANDING_BANNER = 176;
    public static final int BLOCK_TYPE_WALL_BANNER = 177;
    public static final int BLOCK_TYPE_DAYLIGHT_DETECTOR_INVERTED = 178;
    public static final int BLOCK_TYPE_RED_SANDSTONE = 179;
    public static final int BLOCK_TYPE_RED_SANDSTONE_STAIRS = 180;
    public static final int BLOCK_TYPE_DOUBLE_STONE_SLAB2 = 181;
    public static final int BLOCK_TYPE_STONE_SLAB2 = 182;
    public static final int BLOCK_TYPE_SPRUCE_FENCE_GATE = 183;
    public static final int BLOCK_TYPE_BIRCH_FENCE_GATE = 184;
    public static final int BLOCK_TYPE_JUNGLE_FENCE_GATE = 185;
    public static final int BLOCK_TYPE_DARK_OAK_FENCE_GATE = 186;
    public static final int BLOCK_TYPE_ACACIA_FENCE_GATE = 187;
    public static final int BLOCK_TYPE_SPRUCE_FENCE = 188;
    public static final int BLOCK_TYPE_BIRCH_FENCE = 189;
    public static final int BLOCK_TYPE_JUNGLE_FENCE = 190;
    public static final int BLOCK_TYPE_DARK_OAK_FENCE = 191;
    public static final int BLOCK_TYPE_ACACIA_FENCE = 192;
    public static final int BLOCK_TYPE_SPRUCE_DOOR = 193;
    public static final int BLOCK_TYPE_BIRCH_DOOR = 194;
    public static final int BLOCK_TYPE_JUNGLE_DOOR = 195;
    public static final int BLOCK_TYPE_ACACIA_DOOR = 196;
    public static final int BLOCK_TYPE_DARK_OAK_DOOR = 197;

    public static Map<Integer, int[]> unknownBlockTypes = new TreeMap<Integer, int[]>();

    public static int lightLevel(int blockType)
    {
        switch (blockType) {
            case 10: // lava
            case 11: // lava
            case 89: // glowstone ore
            case 91: // jack-o-lantern
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
        OpaqueFunky,
        Glass,
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

        TransparencyClass.Glass,   // 20
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
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Solid,   // 45
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,

        TransparencyClass.Widget,   // 50
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Widget,
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
        TransparencyClass.OpaqueFunky,
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

        TransparencyClass.Widget,   // 90
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
        TransparencyClass.OpaqueFunky,
        TransparencyClass.OpaqueFunky,

        TransparencyClass.Solid,   // 110
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Widget,   // 115
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,

        TransparencyClass.Solid,   // 120
        TransparencyClass.Solid,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,   // 125
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Widget,
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Solid,

        TransparencyClass.Solid,   // 130
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.OpaqueFunky,
        TransparencyClass.OpaqueFunky,   // 135
        TransparencyClass.OpaqueFunky,
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
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Widget,  // 160
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.OpaqueFunky,
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Solid, // 165
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid, // 170
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Solid,
        TransparencyClass.Widget, // 175
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Solid,
        TransparencyClass.OpaqueFunky, // 180
        TransparencyClass.Solid,
        TransparencyClass.OpaqueFunky,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget, // 185
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget, // 190
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
        TransparencyClass.Widget,
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
