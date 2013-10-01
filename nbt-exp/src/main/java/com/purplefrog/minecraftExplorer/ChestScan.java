package com.purplefrog.minecraftExplorer;

import com.mojang.nbt.*;
import net.minecraft.world.chunk.storage.*;
import org.apache.log4j.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 9/19/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChestScan
{
    private static final Logger logger = Logger.getLogger(ChestScan.class);

    public static void main(String[] argv)
    {
        File f2 = new File(System.getProperty("user.home"), ".minecraft/saves/chest-survey-1");
        MinecraftWorld world = new MinecraftWorld(f2);

        ChestLogger minecarts = new ChestLogger();
        minecarts.label = "minecart";
        scanAllChests(world, new TileEntityScanner(new ChestLogger()), minecarts);

        minecarts.finish();

    }

    public static void debug1(MinecraftWorld world)
    {
        try {
            Tag t = world.makeChunkFor(-347>>4,284>>4);

            Anvil anvil = new Anvil((CompoundTag) t);

            Tag tiles = anvil.getTileEntities();
            System.out.println(tiles);
        } catch (IOException e) {
            logger.warn("", e);
        }
    }

    public static class TileEntityScanner
    {
        ChestLogger cl;

        public TileEntityScanner(ChestLogger chestLogger)
        {
            cl = chestLogger;
        }

        public void processTileEntity(Tag tile)
        {
            if (tile instanceof CompoundTag) {
                CompoundTag compoundTag = (CompoundTag) tile;

                String id = compoundTag.getString("id");
                if ("Chest".equals(id)) {
                    processChest(compoundTag, cl);
                } else {
                    //debugPrint(id);
                }
            }
        }

        public void finish()
        {
            cl.finish();
        }
    }

    public static void scanAllChests(MinecraftWorld world, TileEntityScanner scanner, ChestLogger minecarts)
    {
        Iterable<RegionFile> iter = world.allRegions();

        for (RegionFile rf:iter) {
//            System.out.println(rf);

            for (Iterator<Anvil> iterator = rf.allChunks().iterator(); iterator.hasNext(); ) {
                try {
                    Anvil anvil = iterator.next();

                    if (true)
                        iterateTileEntities(scanner, anvil);

                    Tag entities = anvil.getEntities();
                    if (entities instanceof ListTag) {
                        ListTag listTag = (ListTag) entities;
                        for (int i=0; i<listTag.size(); i++) {
                            Tag entity = listTag.get(i);
                            if (entity instanceof CompoundTag) {
                                CompoundTag compoundTag = (CompoundTag) entity;
                                String id = compoundTag.getString("id");


//                                debugPrint("entity.id = "+id);
                                if ("MinecartChest".equals(id)) {
                                    System.out.println(compoundTag);
                                    processChest(compoundTag, minecarts);
                                }
                            } else {
                                debugPrint("strange entity tag "+entity);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warn("failed to parse chunk", e);
                }
            }

            try {
                rf.close();
            } catch (IOException e) {
                logger.warn("", e);
            }
        }

        scanner.finish();
    }

    private static void iterateTileEntities(TileEntityScanner scanner, Anvil anvil)
    {
        Tag t2 = anvil.getTileEntities();
        if (t2 instanceof ListTag) {
            ListTag listTag = (ListTag) t2;
            if (0!=listTag.size()) {
//                System.out.println(listTag.size()+" tile entities in chunk "+x+","+z+" of "+rf);
            }
            for (int i=0; i<listTag.size(); i++) {
                Tag tile = listTag.get(i);
                scanner.processTileEntity(tile);
            }
        }
    }

    public static void processChest(CompoundTag compoundTag, ChestLogger cl)
    {
        int x = compoundTag.getInt("x");
        int y = compoundTag.getInt("y");
        int z = compoundTag.getInt("z");

        ChestLogger.PerChestLogger pcl = cl.foundChest(x, y, z);

        ListTag items = compoundTag.getList("Items");

        for (int i=0; i<items.size(); i++) {
            CompoundTag item = (CompoundTag) items.get(i);
            int id = item.getShort("id");
            int count = item.getByte("Count");
            int slot = item.getByte("Slot");

            pcl.logSlot(id, count, slot);
        }

        pcl.finish();

//        debugPrint(items);
    }

    private static void debugPrint(Object x)
    {
        System.out.println(x);
    }

    private static boolean empty(Tag t2)
    {
        if (t2 instanceof ListTag) {
            ListTag lt = (ListTag) t2;
            return lt.size()==0;
        } else {
            return false;
        }
    }

}
