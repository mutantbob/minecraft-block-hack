package com.purplefrog.minecraftExplorer.blockmodels;

/**
 * Created by thoth on 9/26/14.
 */
public class BlockEnvironment
{

    private boolean[] culling;
    public boolean[] fenceConnectivity;

    public BlockEnvironment(boolean [] culling, boolean[] fenceConnectivity)
    {
        this.culling = culling;
        this.fenceConnectivity = fenceConnectivity;
    }

    public boolean notCulled(String cullface)
    {
        if (cullface == null)
            return true;

        Orientation or = Orientation.valueOf(cullface);
        int idx = or.ordinal();
        return !culling[idx];
    }

    public BlockEnvironment rotated90()
    {
        return new BlockEnvironment(rotated90(culling), rotated90(fenceConnectivity));
    }

    public BlockEnvironment rotated180()
    {
        return new BlockEnvironment(rotated180(culling), rotated180(fenceConnectivity));
    }

    public BlockEnvironment rotated270()
    {
        return new BlockEnvironment(rotated270(culling), rotated270(fenceConnectivity));
    }

    private boolean[] rotated270(boolean[] dirs)
    {
        return new boolean[] {
            dirs[0],
            dirs[1],
            dirs[4],
            dirs[5],
            dirs[3],
            dirs[2],
        };
    }

    private boolean[] rotated180(boolean[] dirs)
    {
        return new boolean[] {
            dirs[0],
            dirs[1],
            dirs[3],
            dirs[2],
            dirs[5],
            dirs[4],
        };
    }

    private boolean[] rotated90(boolean[] dirs)
    {
        return new boolean[] {
            dirs[0],
            dirs[1],
            dirs[5],
            dirs[4],
            dirs[2],
            dirs[3],
        };
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
