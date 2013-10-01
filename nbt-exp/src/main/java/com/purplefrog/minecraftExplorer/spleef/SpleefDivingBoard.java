package com.purplefrog.minecraftExplorer.spleef;

import com.purplefrog.minecraftExplorer.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/2/13
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpleefDivingBoard
    implements GeometryTree
{
    private final int cx;
    private final int y0;
    private final int z1;
    private final int y2;
    private int z2;
    protected BlockPlusData mat = new BlockPlusData(7);
    protected BlockPlusData ladder = new BlockPlusData(65, 3);
    protected final BlockPlusData air = new BlockPlusData(0);

    public SpleefDivingBoard(int cx, int y0, int z1, int y2, int z2)
    {
        this.cx = cx;
        this.y0 = y0;
        this.z1 = z1;
        this.y2 = y2;
        this.z2 = z2;
    }

    @Override
    public BlockPlusData pickFor(int x_, int y, int z)
    {
        int x = x_-cx;

        if (y<y0)
            return null;

        if (x<-1 || x>1)
            return null;
        if (y>y2+3)
            return null;

        if (z> z1 +1)
            return null;
        if (z > z2 && z<=z1) {
            if (y == y2) {
                return mat;
            } else if (y>y2)
                return air;
        }

        if (y>y2)
            return null;

        if (z== z1 +1) {
            return ladder;
        }
        if (z== z1) {
            return mat;
        }

        return null;
    }
}
