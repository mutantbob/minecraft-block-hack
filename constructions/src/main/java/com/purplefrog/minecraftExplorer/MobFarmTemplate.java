package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/3/13
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MobFarmTemplate
    implements GeometryTree
{

    private final int cx2,cz2;
    public Bounds3Di b;
    private BlockPlusData wall = new BlockPlusData(1);
    private BlockPlusData air = new BlockPlusData(0);

    public MobFarmTemplate(Bounds3Di b)
    {
        this.b = b;

        cx2 = b.x0 + b.x1-1;
        cz2 = b.z0 + b.z1-1;
    }

    @Override
    public BlockPlusData pickFor(int x, int y_, int z)
    {
        int y = y_-b.y0;


        if (!b.contains(x,y_,z))
            return null;

        int floor = y/4;

        int x2 = x * 2 - cx2;
        int z2 = z*2 - cz2;
        int r = Math.max(Math.abs(x2), Math.abs(z2));

        if (Math.abs(r) > floor*4+7)
            return air;

        if (x==b.x0 || x+1 == b.x1
            || z==b.z0 || z+1==b.z1
            || Math.abs(r) == floor*4+7) {
            return wall;
        }

        if (y%4==0) {
            if (y_+4 >= b.y1)
                return wall;// roof
            if (r%8 == (floor%2 *4)+1)
                return new BlockPlusData(68, signDetailFor(x2,z2));
            else
                return wall;
        }

        return air;
    }

    public static int signDetailFor(int x2, int z2)
    {
        if (Math.abs(x2) > Math.abs(z2)) {
            if (x2<0)
                return 4;
            else
                return 5;
        } else {
            if (z2<0)
                return 3;
            else
                return 2;
        }
    }

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));

        int x0 = 0;
        int y0 = 150;
        int y1 = y0 + 9*4;
        int z0 = 100;

        int w = 30;
        Bounds3Di b = new Bounds3Di(x0, y0, z0, x0+w, y1, z0+w);

        editor.apply(new MobFarmTemplate(b), b);

        editor.relight();
        editor.save();

    }
}
