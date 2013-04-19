package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/12/13
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaintToolsTest
{

    public static void main(String[] argv)
        throws IOException
    {
        BlockEditor editor = new BlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));

        int x0=250;
        int y0=80;
        int z0= -20;

        editor.fillCube(0, x0, 70, z0, x0+7*15, 150, z0+7*15);

        testBorderedRectangle(editor, x0, y0, z0);

        testPyramid(editor, x0, y0 + 10, z0);

        testPyramid2(editor, x0, y0 + 20, z0);

        editor.relight();

        editor.save();

    }

    public static void testBorderedRectangle(BlockEditor editor, int x0, int y0, int z0)
        throws IOException
    {
        BlockPlusData paleBlue = new BlockPlusData(35, 3);
        BlockPlusData yellow = new BlockPlusData(35, 4);
        BlockPlusData lime = new BlockPlusData(35, 5);

        BlockTemplate north= new BlockTemplate(35);
        BlockTemplate south = new BlockTemplate(1, 2, 1, new BlockPlusData(35, 1), new BlockPlusData(35, 2));
        BlockTemplate east = new BlockTemplate(1, 3, 1, paleBlue, paleBlue, new BlockPlusData(35, 6));
        BlockTemplate west = new BlockTemplate(2, 4, 1, yellow, lime, yellow, lime, lime, yellow, lime, yellow );
        BlockTemplate meadow = new BlockTemplate(1,1,2, new BlockPlusData(17), new BlockPlusData(18));

        for (int u=0; u<7; u++) {
            for (int v=0; v<7; v++) {
                int x1 = x0 + u * 15;
                int z1 = z0 + v * 15;
                editor.drawBorderedRectangle(x1, y0, z1, x1 + 3 + u, z1 + 3 + v, north, south, east, west, meadow);
            }
        }
    }

    public static void testPyramid(BlockEditor editor, int x0, int y0, int z0)
        throws IOException
    {
        BlockPlusData paleBlue = new BlockPlusData(35, 3);
        BlockPlusData yellow = new BlockPlusData(35, 4);
        BlockPlusData lime = new BlockPlusData(35, 5);

        BlockTemplate north= new BlockTemplate(35);
        BlockTemplate south = new BlockTemplate(1, 2, 1, new BlockPlusData(35, 1), new BlockPlusData(35, 2));
        BlockTemplate east = new BlockTemplate(1, 3, 1, paleBlue, paleBlue, new BlockPlusData(35, 6));
        BlockTemplate west = new BlockTemplate(2, 4, 1, yellow, lime, yellow, lime, lime, yellow, lime, yellow );
        BlockTemplate meadow = new BlockTemplate(1,1,2, new BlockPlusData(17), new BlockPlusData(18));

        for (int u=0; u<7; u++) {
            for (int v=0; v<7; v++) {
                int x1 = x0 + u * 15;
                int z1 = z0 + v * 15;
                editor.drawPyramid(x1, y0, z1, x1 + 3 + u, z1 + 3 + v, north, east, south, west, meadow, 1);
            }
        }
    }

    public static void testPyramid2(BlockEditor editor, int x0, int y0, int z0)
        throws IOException
    {

        BlockTemplate side= new BlockTemplate(new BlockPlusData(109,2));
        BlockTemplate corner =new BlockTemplate(44);

        for (int u=0; u<7; u++) {
            for (int v=0; v<7; v++) {
                int x1 = x0 + u * 15;
                int z1 = z0 + v * 15;
                editor.drawPyramid(x1, y0, z1, x1 + 3 + u, z1 + 3 + v, side,corner);
            }
        }
    }
}
