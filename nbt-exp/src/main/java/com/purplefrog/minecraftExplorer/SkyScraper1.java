package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/11/13
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SkyScraper1
{
    public int uCells=5;
    public int vCells=7;
    public int nFloors=10;
    public int floorHeight=4;
    public int[] column={42,42,42,42};

    WindowShape window1 = new WindowShape(3, new Object[]{
        89, 89,
        102, 102,
        102, 102,
        102, 102,
    });

    public final BlockPlusData chiseledStone = new BlockPlusData(98, 3);

    WindowShape window2 = new WindowShape(4, new Object[]{
        chiseledStone, chiseledStone, chiseledStone,
        102, 102,102,
        102, 102,102,
        102, 102,102,
    });

    public int floorType=1;

    public void render(BlockEditor editor, int x0, int y0, int z0)
        throws IOException
    {
        {
            int z2 = z0 + vCells * cellWidthV();
            for (int u=0; u<uCells; u++) {
                int x = x0 + u * cellWidthU();
                exteriorWallColumn(editor, x, y0, z0);
                exteriorWallColumn(editor, x, y0, z2);
                exteriorWindowStack(editor, x, y0, z0, 1, 0, window1);
                exteriorWindowStack(editor, x, y0, z2, 1, 0, window1);
            }
        }

        {
            int x2 = x0 + uCells * cellWidthU();
            for (int v=0; v<vCells; v++) {
                int z = z0 + v * cellWidthV();
                exteriorWallColumn(editor, x0, y0, z);
                exteriorWallColumn(editor, x2, y0, z);
                exteriorWindowStack(editor, x0, y0, z, 0, 1, window2);
                exteriorWindowStack(editor, x2, y0, z, 0, 1, window2);
            }
        }

        exteriorWallColumn(editor, x0+uCells*cellWidthU(), y0, z0+vCells*cellWidthV());


        for (int c=0; c<=nFloors; c++) {
            for (int u=1; u<uCells*cellWidthU(); u++) {
                for (int v=1; v<vCells*cellWidthV(); v++) {
                    editor.setBlock(x0+u, y0+c*floorHeight, z0+v, floorType);
                }
            }
        }
    }

    public int cellWidthU()
    {
        return window1.cellWidth;
    }

    public int cellWidthV()
    {
        return window2.cellWidth;
    }

    public void exteriorWindowStack(BlockEditor editor, int x0, int y0, int z0, int dx, int dz, WindowShape wShape)
        throws IOException
    {
        for (int c=0; c<nFloors; c++) {
            int y1 = y0+c*floorHeight;
            exteriorWindow(editor, x0,y1, z0, dx, dz, wShape);
        }
    }

    public void exteriorWindow(BlockEditor editor, int x0, int y0, int z0, int dx, int dz, WindowShape wShape)
        throws IOException
    {
        int ptr=0;
        for (int v=0; ptr< wShape.blocks.length; v++) {
            for (int u=1; u<wShape.cellWidth; u++, ptr++)
            {
                editor.setBlock(x0+u*dx, y0+v, z0+u*dz, wShape.blocks[ptr]);
            }
        }
    }

    public void exteriorWallColumn(BlockEditor editor, int x, int y, int z)
        throws IOException
    {
        for (int c=0; c<nFloors; c++) {

            int y1 = y + c * floorHeight;

            for (int y2 = 0; y2<floorHeight; y2++) {
                editor.setBlock(x,y1+y2,z, column[y2]);
            }
        }
    }

    public static void main(String[] argv)
        throws IOException
    {
        SkyScraper1 q = new SkyScraper1();

        File saveDir = GenerateMaze1.pickSaveDir();

        BlockEditor editor = new BlockEditor(new MinecraftWorld(saveDir));


        int x0=200;
        int y0=80;
        int z0 =100;

        q.render(editor, x0, y0, z0);

        editor.relight();

        editor.save();
    }


    public static class WindowShape
    {
        public final int cellWidth;

        public final BlockPlusData[] blocks;

        public WindowShape(int cellWidth, Object[] blocks)
        {
            this.blocks = new BlockPlusData[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                Object block_ = blocks[i];
                if (block_ instanceof BlockPlusData) {
                    this.blocks[i] =  (BlockPlusData) block_;
                } else {
                    this.blocks[i] = new BlockPlusData((Integer)block_, 0);
                }
            }
            this.cellWidth = cellWidth;
        }
    }
}
