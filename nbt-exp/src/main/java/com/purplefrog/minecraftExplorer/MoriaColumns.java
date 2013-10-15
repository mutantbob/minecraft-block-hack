package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/18/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MoriaColumns
{

    protected int floorThickness = 2;
    protected final int columnIntervalU;
    protected final int columnIntervalV;
    protected final int phaseU;
    protected final int phaseV;

    public MoriaColumns()
    {
        columnIntervalU = 8;
        columnIntervalV = 11;
        phaseU = 3;
        phaseV = 3;
    }

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));

        Bounds3Di bounds = new Bounds3Di(1, 85, 1, 75, 110, 90);

        editor.fillCube(0, bounds);

        new MoriaColumns().render(editor, bounds);

        editor.relight();
        editor.save();
    }

    public void render(BasicBlockEditor editor, Bounds3Di bounds)
        throws IOException
    {

        for (int y=0; y< floorThickness; y++) {
            editor.drawBorderedRectangle(bounds.x0, y+bounds.y0, bounds.z0, bounds.x1, bounds.z1, null, null, null, null, new BlockTemplate(1));
        }

        int y3 = bounds.y1 - 2;

        for (int u=0; u* columnIntervalU + bounds.x0+ phaseU < bounds.x1; u++ ) {
            for (int v=0; v* columnIntervalV + bounds.z0 + phaseV< bounds.z1; v++ ) {

                int x = bounds.x0 + u * columnIntervalU + phaseU;
                int z = bounds.z0 + v * columnIntervalV + phaseV;

                drawColumn(editor, x, bounds.y0+ floorThickness, z, y3);
            }
        }

        for (int y=y3; y< bounds.y1; y++) {
            editor.drawBorderedRectangle(bounds.x0, y, bounds.z0, bounds.x1, bounds.z1, null, null, null, null, new BlockTemplate(1));
        }

    }

    public void drawColumn(BasicBlockEditor editor, int x, int y1, int z, int y2)
        throws IOException
    {
        BlockTemplate foot = new BlockTemplate(1, 1, 1, new BlockPlusData(109, 2));
        BlockTemplate corner = new BlockTemplate(new BlockPlusData(44, 5));
        BlockTemplate column = new BlockTemplate(2,2,1, 1,1,1,1);

        BlockTemplate header = new BlockTemplate(1, 1, 1, new BlockPlusData(109, 4|2));
        BlockTemplate uCorner = new BlockTemplate(new BlockPlusData(44,8|5));


        {
            int cycles = 1;

            int pad = foot.depth * cycles;
            editor.drawPyramid(x- pad, y1, z- pad, x+column.width+ pad, z+column.depth+ pad, foot, corner);
        }

        {
            int cycles = Math.min((columnIntervalU - column.width) /2, (columnIntervalV - column.depth)/2);

            int pad = header.depth * cycles;
            editor.drawPyramid(x- pad, y2 -1, z- pad, x+column.width+ pad, z+column.depth+ pad, header, uCorner, -1);
        }

        editor.fillCubeByCorners(column, x, y1, z, x + column.width, y2, z + column.depth);

    }
}
