package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/3/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MobFarm2Template
    implements GeometryTree
{
    private final int cx;
    private final int y0;
    private final int cz;
    private final int nFloors;
    protected final BlockPlusData air = new BlockPlusData(0);
    private BlockPlusData stone = new BlockPlusData(1);
    private BlockPlusData redstone = new BlockPlusData(55);
    private BlockPlusData ironBars = new BlockPlusData(101);
    private BlockPlusData water = new BlockPlusData(9);
    private BlockPlusData slab = new BlockPlusData(44, 7);
    private BlockPlusData dirt = new BlockPlusData(3);

    public MobFarm2Template(int cx, int y0, int cz, int nFloors)
    {

        this.cx = cx;
        this.y0 = y0;
        this.cz = cz;
        this.nFloors = nFloors;
    }

    @Override
    public BlockPlusData pickFor(int x_, int y_, int z_)
    {
        int x = x_-cx;
        int y = y_-y0;
        int z = Math.abs(z_-cz);

        if (x<0)
            x = -1-x;

        if (x==0 && z==0)
            return air;
        if (y==0)
            return stone;

        int zWall = 9;
        int xWall1 = 8;
        int xWall2 = 11;

        int floorHeight = 4;

        int floor = (y-1)/floorHeight;

        if (floor >= nFloors)
            return dirt;

        switch (y%floorHeight) {
            case 1:
                if (x==1+xWall1 && z<2)
                    return ironBars;
                else if (z==0)
                    if (x==xWall1)
                        return water;
                    else if (x<xWall1)
                        return air;

                if (z==zWall)
                    return stone;
                else if (z < zWall)
                    if (x <= xWall1 || x==xWall2 || x==xWall2+1)
                        return stone;
                    else if (x< xWall2)
                        return slab;

                return air;

            case 2:
                if (x == xWall1
                    || x ==xWall2
                    || x == xWall2+1)
                    return stone;
                else if (x > xWall1 && z<zWall)
                    return air;
                else if (z == zWall)
                    if (x>=xWall1)
                        return stone;
                    else
                        return new BlockPlusData(23, z_>cz?2:3);
                else if (z ==zWall+1 && x<=xWall2+1)
                    return stone;
                else
                    return air;

            case 0:
                if(x <= xWall2+1 && z <= zWall+1)
                    return stone;
                else
                    return air;

            case 3:
                if (z ==zWall+1)
                    return redstone;
                // fall through
            default:
                if (x == xWall2 || x==xWall2+1)
                    return stone;
                else if (z == zWall)
                    return stone;
                else if (x > xWall1)
                    return air;
                else if (x == xWall1)
                    return ironBars;
                else
                    return air;

        }

    }

    public WormWorld.Bounds getBounds()
    {
        int dx = 13;
        int dz=11;
        return new WormWorld.Bounds(cx-dx, y0, cz-dz,
            cx+dx+1, y0+nFloors*4+1 +1, cz+dz);
    }


    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));

        int x0 = 0;
        int y0 = 150;
        int z0 = 200;

        MobFarm2Template mobFarm = new MobFarm2Template(x0, y0, z0, 4);
        editor.apply(mobFarm, mobFarm.getBounds());

        editor.relight();
        editor.save();

    }


}
