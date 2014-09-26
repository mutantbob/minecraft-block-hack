package com.purplefrog.minecraftExplorer.blockmodels;

/**
 * Created by thoth on 9/26/14.
 */
public class BlockEnvironment
{

    private boolean[] culling;

    public BlockEnvironment(boolean [] culling)
    {
        this.culling = culling;
    }

    public boolean notCulled(String cullface)
    {
        if (cullface == null)
            return true;

        Orientation or = Orientation.valueOf(cullface);
        int idx = or.ordinal();
        return !culling[idx];
    }

    public enum Orientation
    {
        up(0,1,0),
        down(0,-1,0),
        east(1,0,0),
        west(-1,0,0),
        north(0,0,-1),
        south(0,0,1);
        public final int dx;
        public final int dy;
        public final int dz;

        Orientation(int dx, int dy, int dz)
        {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }
    }

}
