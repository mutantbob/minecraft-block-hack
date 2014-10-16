package com.purplefrog.minecraftExplorer.blockmodels;


import com.purplefrog.minecraftExplorer.*;
import org.json.*;

import java.io.*;
import java.util.*;

/**
 * Created by thoth on 9/25/14.
 * http://minecraft.gamepedia.com/Block_models
 */
public class BlockModels
{
    public static final OneBlockModel AIR = new OneBlockModel(0);
    private static BlockModels singleton;


    Map<Integer, BlockVariants> cache = new TreeMap<Integer, BlockVariants>();
    Map<Integer, BlockVariants> cache2 = new TreeMap<Integer, BlockVariants>();
    private BlockModels()
    {
    }

    public static BlockModels getInstance()
    {
        if (null==singleton) {
            singleton = new BlockModels();
        }
        return singleton;
    }

    public OneBlockModel modelFor(int blockType, int blockData)
        throws IOException, JSONException
    {
        if (blockType==0 || blockType == BlockDatabase.BLOCK_TYPE_PISTON_EXTENSION) {
            return AIR;
        }

        int combo = (blockType<<8) | (blockData&0xff);
        BlockVariants rval;

        rval = cache2.get(combo);
        if (rval==null) {
            String tag = tagFor2(blockType, blockData);

            if (tag != null) {
                rval = OneBlockModel.parse(new Resources(), tag);
                cache2.put(combo, rval);
                return rval.getVariant(blockType, blockData);
            }
        }

        rval = cache.get(blockType);
        if (null==rval){
            String tag = tagFor(blockType);

            rval = OneBlockModel.parse(new Resources(), tag);
            cache.put(blockType, rval);
        }

        return rval.getVariant(blockType, blockData);
    }

    public static String[] LOGS = ("oak_log spruce_log birch_log jungle_log").split(" +");
    public static String[] WOODS = ("oak spruce birch jungle acacia dark_oak oak oak").split(" +");
    public static String[] SANDSTONE = "sandstone chiseled_sandstone smooth_sandstone sandstone".split(" +");
    public static String[] TALL_GRASS = "dead_bush tall_grass fern dead_bush".split(" +");
    public static String[] WOOL = "white orange magenta light_blue yellow lime pink gray silver cyan purple blue brown green red black".split(" +");
    public static String[] POPPY = ("poppy blue_orchid allium houstonia red_tulip orange_tulip white_tulip pink_tulip " +
        "oxeye_daisy poppy poppy poppy poppy poppy poppy poppy").split(" +");

    public static String tagFor2(int blockType, int blockData)
    {

        switch (blockType) {
            case BlockDatabase.BLOCK_TYPE_PLANKS:
                return WOODS[blockData&7]+"_planks";
            case BlockDatabase.BLOCK_TYPE_SAPLING:
                return WOODS[blockData&7]+"_sapling";
            case BlockDatabase.BLOCK_TYPE_LOG:
                return LOGS[blockData&3];
            case BlockDatabase.BLOCK_TYPE_LEAVES:
                return WOODS[blockData&3]+"_leaves";
            case BlockDatabase.BLOCK_TYPE_SANDSTONE:
                return SANDSTONE[blockData&3];
            case BlockDatabase.BLOCK_TYPE_TALL_GRASS:
                return TALL_GRASS[blockData&3];
            case BlockDatabase.BLOCK_TYPE_WOOL:
                return WOOL[blockData]+"_wool";
            case BlockDatabase.BLOCK_TYPE_POPPY:
                return POPPY[blockData];

        }

        return null;
    }

    private static String tagFor(int blockType)
    {
        switch (blockType) {
            case 1:
                return "stone";
            case 2:
                return "grass";
            case 3:
                return "dirt";
            case 4:
                return "cobblestone";
            case 5:
                return "oak_planks";
            case 6:
                return "oak_sapling";
            case 7:
                return "bedrock";
            case 12:
                return "sand";
            case 13:
                return "gravel";
            case 14:
                return "gold_ore";
            case 15:
                return "iron_ore";

            case 16:
                return "coal_ore";
            case 17:
                return "oak_log";
            case 18:
                return "oak_leaves";
            case 19:
                return "sponge";
            case 20:
                return "glass";
            case 21:
                return "lapis_ore";
            case 22:
                return "lapis_block";
            case 23:
                return "dispenser";
            case 24:
                return "sandstone";
            case 25:
                return "noteblock";
            case 26:
                return "bed";
            case 27:
                return "golden_rail";
            case 28:
                return "detector_rail";
            case 29:
                return "sticky_piston";
            case 30:
                return "web";
            case 31:
                return "tall_grass";

            case 32:
                return "dead_bush";
            case 33:
                return "piston";
            case 34:
                return "piston_head";
            case 35:
                return "white_wool";
            // 36 special case
            case 37:
                return "dandelion";
            case 38:
                return "poppy";
            case 39:
                return "brown_mushroom";
            case 40:
                return "red_mushroom";
            case 41:
                return "gold_block";
            case 42:
                return "iron_block";
            case 43:
                return "stone_double_slab";
            case 44:
                return "stone_slab";
            case 45:
                return "brick_block";
            case 46:
                return "tnt";
            case 47:
                return "bookshelf";

            case 48:
                return "mossy_cobblestone";
            case 49:
                return "obsidian";
            case 50:
                return "torch";
            case 51:
                return "fire";
            case 52:
                return "mob_spawner";
            case 53:
                return "oak_stairs";
//            case 54:
//                return "chest";
            case 55:
                return "redstone_wire";
            case 56:
                return "diamond_ore";
            case 57:
                return "diamond_block";
            case 58:
                return "crafting_table";
            case 59:
                return "wheat";
            case 60:
                return "farmland";
            case 61:
                return "furnace";
            case 62:
                return "lit_furnace";
//            case 63:
//                return "sign";

            case 64:
                return "wooden_door"; // XXX
            case 65:
                return "ladder";
            case 66:
                return "rail";
            case 67:
                return "stone_stairs";
//            case 68:
//                return "wall_sign";
            case 69:
                return "lever";
            case 70:
                return "stone_pressure_plate";
            case 71:
                return "iron_door";
            case 72:
                return "wooden_pressure_plate";
            case 73:
                return "redstone_ore";
            case 74:
                return "lit_redstone_ore";
            case 75:
                return "unlit_redstone_torch";
            case 76:
                return "redstone_torch";
            case 77:
                return "stone_button";
            case 78:
                return "snow_layer";
            case 79:
                return "ice";

            case 80:
                return "snow";
            case 81:
                return "cactus";
            case 82:
                return "clay";
            case 83:
                return "reeds";
            case 84:
                return "jukebox";
            case 85:
                return "fence";
            case 86:
                return "pumpkin";
            case 87:
                return "netherrack";
            case 88:
                return "soul_sand";
            case 89:
                return "glowstone";
            case 90:
                return "portal";
            case 91:
                return "lit_pumpkin";
            case 92:
                return "cake";
            case 93:
                return "unpowered_repeater";
            case 94:
                return "powered_repeater";
            case 95:
                //XXX
                return "purple_stained_glass";

            case 96:
                return "trapdoor";
            case 97:
                // XXX
                return "stone_monster_egg";
            case 98:
                return "stonebrick";
            case 99:
                return "brown_mushroom_block";
            case 100:
                return "red_mushroom_block";
            case 101:
                return "iron_bars";
            case 102:
                return "glass_pane";
            case 103:
                return "melon_block";
            case 104:
                return "pumpkin_stem";
            case 105:
                return "melon_stem";
            case 106:
                return "vine";
            case 107:
                return "fence_gate";
            case 108:
                return "brick_stairs";
            case 109:
                return "stone_brick_stairs";
            case 110:
                return "mycelium";
            case 111:
                return "waterlily";

            case 112:
                return "nether_brick";
            case 113:
                return "nether_brick_fence";
            case 114:
                return "nether_brick_stairs";
            case 115:
                return "nether_wart";
            case 116:
                return "enchanting_table";
            case 117:
                return "brewing_stand";
            case 118:
                return "cauldron";
//            case 119:
//                return "end_portal";
            case 120:
                return "end_portal_frame";
            case 121:
                return "end_stone";
            case 122:
                return "dragon_egg";
            case 123:
                return "redstone_lamp";
            case 124:
                return "lit_redstone_lamp";
            case 125:
                return "oak_double_slab";
            case 126:
                return "oak_slab";
            case 127:
                return "cocoa";

            case 128:
                return "sandstone_stairs";
            case 129:
                return "emerald_ore";
//            case 130:
//                return "ender_chest";
            case 131:
                return "tripwire_hook";
            case 132:
                return "tripwire";
            case 133:
                return "emerald_block";
            case 134:
                return "spruce_stairs";
            case 135:
                return "birch_stairs";
            case 136:
                return "jungle_stairs";
            case 137:
                return "command_block";
            case 138:
                return "beacon";
            case 139:
                return "cobblestone_wall";
            case 140:
                return "flower_pot";
            case 141:
                return "carrots";
            case 142:
                return "potatoes";
            case 143:
                return "wooden_button";

//            case 144:
//                return "skull";
            case 145:
                return "anvil";
//            case 146:
//                return "trapped_chest";
            case 147:
                return "light_weighted_pressure_plate";
            case 148:
                return "heavy_weighted_pressure_plate";
            case 149:
                return "unpowered_comparator";
            case 150:
                return "powered_comparator";
            case 151:
                return "daylight_detector";
            case 152:
                return "redstone_block";
            case 153:
                return "quartz_ore";
            case 154:
                return "hopper";
            case 155:
                return "quartz_block";
            case 156:
                return "quartz_stairs";
            case 157:
                return "activator_rail";
            case 158:
                return "dropper";
            case 159:
                // XXX
                return "purple_stained_hardened_clay";
            case 160:
                ///XXX
                return "purple_stained_glass_pane";
            case 161:
                // XXX
                return "acacia_leaves";
            case 162:
                //XXX
                return "acacia_log";
            case 163:
                return "acacia_stairs";
            case 164:
                return "dark_oak_stairs";
            case 165:
                return "slime";
//            case 166:
//                return "barrier";
            case 167:
                return "iron_trapdoor";
            case 168:
                return "prismarine";
            case 169:
                return "sea_lantern";
            case 170:
                return "hay_block";
            case 171:
                // XXX
                return "purple_carpet";
            case 172:
                return "hardened_clay";
            case 173:
                return "coal_block";
            case 174:
                return "packed_ice";
            case 175:
                // XXX
                return "sunflower";
//            case 176:
//                return "standing_banner";
//            case 177:
//                return "wall_banner";
            case 178:
                return "daylight_detector_inverted";
            case 179:
                return "red_sandstone";
            case 180:
                return "red_sandstone_stairs";
            case 181:
                // XXX
                return "red_sandstone_double_slab";
            case 182:
                return "red_sandstone_slab";
            case 183:
                return "spruce_fence_gate";
            case 184:
                return "birch_fence_gate";
            case 185:
                return "jungle_fence_gate";
            case 186:
                return "dark_oak_fence_gate";
            case 187:
                return "acacia_fence_gate";
            case 188:
                return "spruce_fence";
            case 189:
                return "birch_fence";
            case 190:
                return "jungle_fence";
            case 191:
                return "dark_oak_fence";
            case 192:
                return "acacia_fence";
            case 193:
                return "spruce_door";
            case 194:
                return "birch_door";
            case 195:
                return "jungle_door";
            case 196:
                return "acacia_door";
            case 197:
                return "dark_oak_door";
        }
        System.out.println("missing model for "+blockType);
        return "dirt";
    }


    public static class Resources
    {
        File root = new File("/var/tmp/assets/minecraft");
        File blockstates = new File(root, "blockstates");
        File models = new File(root, "models");

        public InputStream getBlockstate(String path)
            throws FileNotFoundException
        {
            return new FileInputStream(new File(blockstates, path+".json"));
        }

        public InputStream getBlockModel(String path)
            throws FileNotFoundException
        {
            return new FileInputStream(new File(models, path+".json"));
        }
    }
}
