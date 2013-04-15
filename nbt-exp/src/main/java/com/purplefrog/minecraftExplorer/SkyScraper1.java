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
    public int uCells;
    public int vCells;
    public int nFloors;
    public int floorHeight;
    public int[] column;

    WindowShape window1;

    WindowShape window2;

    public int floorType=1;

    public SkyScraper1(int uCells, int vCells, int nFloors, int[] column, WindowShape northWall, WindowShape eastWall)
    {
        this.uCells = uCells;
        this.vCells = vCells;
        this.nFloors = nFloors;
        this.column = column;
        this.window1 = northWall;
        this.window2 = eastWall;

        floorHeight = Math.max(this.column.length,
            Math.max(this.window1.height(), this.window2.height()));
    }

    public static SkyScraper1 cliche1()
    {
        WindowShape window1 = new WindowShape(3,
            89, 89,
            102, 102,
            102, 102,
            102, 102
        );

        BlockPlusData cs = BlockDatabase.chiseledStone;
        WindowShape window2 = new WindowShape(5, new Object[]{
            cs, cs, cs,cs,
            102, 102, 102, 102,
            102, 102, 102, 102,
            new BlockPlusData(109,7), 102, 102, new BlockPlusData(109, 6),
        });

        return new SkyScraper1(5, 7, 10, new int[]{42, 42, 42, 42}, window1, window2);

    }


    public void render(BlockEditor editor, int x0, int y0, int z0)
        throws IOException
    {
        int y2 = y0 + nFloors * floorHeight;
        {
            int z2 = z0 + vCells * cellWidthV();
            for (int u=0; u<uCells; u++) {
                int x = x0 + u * cellWidthU();
                exteriorWallColumn(editor, x, y0, z0);
                exteriorWallColumn(editor, x, y0, z2);
                exteriorWindowStack(editor, x, y0, z0, 1, 0, window1);
                exteriorWindowStack(editor, x, y0, z2, 1, 0, window1);
                roofTrim(editor, x, y2, z0, 1,0, window1);
                roofTrim(editor, x, y2, z2, 1,0, window1);
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
                roofTrim(editor, x0, y2, z, 0, 1, window2);
                roofTrim(editor, x2, y2, z, 0, 1, window2);
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

    /**
     * <p>&lt;x0,y0,z0&gt; determines the start point of the roof trim.
     *
     * <p>&lt;dx,dz&gt; determines the orientation of the roof line.
     *
     * @param wShape the bottom row, combined with {@link #column}[0] will be used as the shape of the trim.
     * @throws IOException
     */
    public void roofTrim(BlockEditor editor, int x0, int y0, int z0, int dx, int dz, WindowShape wShape)
        throws IOException
    {
        editor.setBlock(x0, y0, z0, column[0]);
        for (int u=1; u<wShape.cellWidth; u++) {
            editor.setBlock(x0+u*dx, y0, z0+u*dz, wShape.blocks[u-1]);
        }
        int u=wShape.cellWidth;
        editor.setBlock(x0+u*dx, y0, z0+u*dz, column[0]);
    }

    public void exteriorWindow(BlockEditor editor, int x0, int y0, int z0, int dx, int dz, WindowShape wShape)
        throws IOException
    {
        int ptr=0;
        for (int v=0; ptr< wShape.blocks.length; v++) {
            for (int u=1; u<wShape.cellWidth; u++, ptr++)
            {
                if (ptr >= wShape.blocks.length)
                    throw new ArrayIndexOutOfBoundsException();
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
                if (y2 >= column.length)
                    throw new ArrayIndexOutOfBoundsException(y2);
                editor.setBlock(x,y1+y2,z, column[y2]);
            }
        }
    }

    public static void main(String[] argv)
        throws IOException
    {
        SkyScraper1 q = cliche1();

        File saveDir = WorldPicker.pickSaveDir();

        BlockEditor editor = new BlockEditor(new MinecraftWorld(saveDir));


        int x0=200;
        int y0=80;
        int z0 =100;

        q.render(editor, x0, y0, z0);

        editor.relight();

        editor.save();
    }

    public int xDimension()
    {
        return uCells * window1.cellWidth +1;
    }

    public int zDimension()
    {
        return vCells * window2.cellWidth +1;
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

            if (blocks.length != (cellWidth -1)* height())
                throw new IllegalArgumentException("blocks.length=" +blocks.length+ " is not an even multiple of "+(cellWidth-1));
        }

        public WindowShape(int cellWidth, int... blocks)
        {
            this.cellWidth = cellWidth;

            this.blocks = new BlockPlusData[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                this.blocks[i] = new BlockPlusData(blocks[i], 0);

            }
        }

        public int height()
        {
            return blocks.length / (cellWidth-1);
        }
    }
}
