package com.purplefrog.minecraftExplorer;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/3/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapThingy
{

    public static void main(String[] argv)
    {

        File saveDir;
        saveDir = new File("/home/thoth/.minecraft/saves/menger-5");
        //saveDir = new File("/home/dvds/thoth/minecraft-backups/minecraft-4/world");

        JFrame fr = new JFrame("minecraft map thingy");

        MinecraftWorld world = new MinecraftWorld(saveDir);
        MinecraftMinimap minimap = new MinecraftMinimap(world);
        fr.getContentPane().add(minimap);
        fr.pack();
        fr.setVisible(true);

        JFrame fr2 = new JFrame("minecraft map thingy 2");
        MinecraftMap worldMap = new MinecraftMap(world);
        fr2.getContentPane().add(worldMap);
        fr2.pack();
        fr2.setVisible(true);

        minimap.linkClickEvents(worldMap);

        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(20*1000);

                    for (Map.Entry<Integer, int[]> en : BlockDatabase.unknownBlockTypes.entrySet()) {
                        System.out.println(en.getKey()+"\t[" + en.getValue()[0] + "]");
                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();

                }
            }
        };

        new Thread(r).start();
    }
}
