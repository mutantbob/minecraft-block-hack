package com.purplefrog.minecraftExplorer.spleef;

import com.purplefrog.minecraftExplorer.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/2/13
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpleefBasin1
    implements GeometryTree
{
    private final int cx;
    private final int y0;
    private final int cz;
    private final int outerRadius;
    protected final BlockPlusData air = new BlockPlusData(0);
    private int innerRadius;

    BlockPlusData wall;
    BlockPlusData water;
    protected BlockPlusData ironBars = new BlockPlusData(101);

    public SpleefBasin1(int cx, int y0, int cz, int outerRadius, int innerRadius)
    {
        this.cx = cx;
        this.y0 = y0;
        this.cz = cz;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        wall = new BlockPlusData(98, 3);
        water = new BlockPlusData(9);
    }

    @Override
    public BlockPlusData pickFor(int x_, int y_, int z_)
    {
        int x = x_-cx;
        int y = y_-y0;
        int z = z_-cz;

        if (x<-outerRadius
            || x > outerRadius
            || z< -outerRadius
            || z> outerRadius)
            return null;

        if (y<0)
            return null;

        if (y>3) {
            if (y<8 && outerRadius == Math.max(Math.abs(x),Math.abs(z))) {
                return ironBars;
            }
            else
                return air;
        }

        if (Math.abs(x)< innerRadius && Math.abs(z) < innerRadius && y>1) {
            return water;
        }

        return wall;
    }

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new BlenderBlockEditor("/tmp/spleef/basin.py");


        int cx = -140;
        int cz= 121;
        int y0 = 81;

        SpleefBasin1 basin = new SpleefBasin1(cx, y0, cz, 10,7);

        editor.apply(basin, basin.getBounds());

        editor.save();
    }

    public WormWorld.Bounds getBounds()
    {
        return new WormWorld.Bounds(cx- outerRadius, y0, cz- outerRadius,
            cx+ outerRadius +1, y0+3, cz+ outerRadius +1);
    }
}
