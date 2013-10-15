package com.purplefrog.minecraftExplorer.spleef;

import com.purplefrog.minecraftExplorer.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/2/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpleefArena
    implements GeometryTree
{
    private final int cx;
    private final int cz;
    private final int y0;
    private final int innerRadius;
    private final int outerRadius;
    private final BlockPlusData floor;

    public SpleefArena(int cx, int cz, int y0, int innerRadius, int outerRadius, BlockPlusData obsidian)
    {
        this.cx = cx;
        this.cz = cz;
        this.y0 = y0;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.floor = obsidian;
    }

    @Override
    public BlockPlusData pickFor(int x_, int y_, int z_)
    {
        int x = x_-cx;
        int y = y_-y0;
        int z = z_-cz;

        int r1 = Math.max(Math.abs(x), Math.abs(z));
        if (r1<innerRadius)
            return null;
        if (r1==innerRadius) {

            if (y<0 || y>1)
                return null;

            return floor;
        }

        int r2 = Math.abs(x) + Math.abs(z);
        int r3 = (int) Math.max(r1, r2 * 0.7);

        if (r3>outerRadius)
            return null;

        if (r3==outerRadius) {
            if (y<0 || y> 3)
                return null;
            return floor;
        }

        switch (y)
        {
            case 0:
            case 2:
                return floor;
            case 1:
                return new BlockPlusData(76);
        }

        return null;
    }
}
