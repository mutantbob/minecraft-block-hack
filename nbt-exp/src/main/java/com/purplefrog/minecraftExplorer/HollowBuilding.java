package com.purplefrog.minecraftExplorer;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/5/13
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class HollowBuilding
    implements GeometryTree
{

    Point3Di p1, p2, p3, p4;
    GeometryTree inner;
    BlockPlusData air = new BlockPlusData(0);

    Materials materials = new Materials();

    int floorSpan;

    public HollowBuilding(GeometryTree inner, Point3Di p1, Point3Di p2, Point3Di p3, Point3Di p4, int floorSpan)
    {
        this.inner = inner;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.floorSpan = floorSpan;
    }

    public HollowBuilding(Point3Di p1, Point3Di p2, Point3Di p3, Point3Di p4, int floorSpan)
    {
        this(new GeometryTree()
        {
            @Override
            public BlockPlusData pickFor(int x, int y, int z)
            {
                return null;
            }
        }, p1, p2, p3, p4, floorSpan);
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        if (y<p1.y || y>p4.y)
            return null;

        switch (ringClass(x,z)) {
            case 0:
                return inner.pickFor(x, y, z);

            case 1:
            case 3:
                if (x==p1.x || x == p4.x)
                    return materials.wallBlock(x-p1.x, y-p1.y);
                else if (x==p2.x || x==p3.x)
                    return materials.wallBlock(x-p2.x, y-p1.y);
                else if (z==p1.z || z==p4.z)
                    return materials.wallBlock(z-p1.z, y-p1.y);
                else
                    return materials.wallBlock(z-p2.z, y-p1.y);

            case 2:
                if (((y-p1.y) % floorSpan)==0)
                    return floorFor(x,z);
                else
                    return air;

            default:
                return null;
        }
    }

    private BlockPlusData floorFor(int x, int z)
    {
        return materials.floor();
    }

    private BlockPlusData wallFor(int x, int y, int z)
    {
        return materials.columns();
    }

    private boolean isWall(int x, int z)
    {
        int ring = ringClass(x, z);
        return ring == 1 || ring==3;
    }

    private int ringClass(int x, int z)
    {
        if (p2.x<x && x<p3.x &&
            p2.z<z && z<p3.z)
            return 0;
        if (p2.x<=x && x<=p3.x &&
            p2.z<=z && z<=p3.z)
            return 1;
        if (p1.x<x && x<p4.x &&
            p1.z<z && z<p4.z)
            return 2;
        if (p1.x<=x && x<=p4.x &&
            p1.z<=z && z<=p4.z)
            return 3;

        return 4;
    }

    public static class Materials
    {
        SkyScraper1.WindowShape  windows  = (SkyScraper1.WindowShape) ClipArt.getDecor(1,1,4,0)[0];

        BlockPlusData columns() {
            return new BlockPlusData(1);
        }

        BlockPlusData windows() {
            return new BlockPlusData(2);
        }

        BlockPlusData wallBlock(int u_, int v_) {
            int u = u_ % (windows.cellWidth-1);
            int v = v_ % windows.height();
            return windows.getBlock(u, v);
        }

        BlockPlusData floor() {
            return new BlockPlusData(4);
        }
    }

    public static class One
    {
        public static void main(String[] argv)
        {

            Point3Di p1 = new Point3Di(200, 80, 300);
            Point3Di p2 = new Point3Di(210, 80, 312);
            Point3Di p3 = new Point3Di(240, 101, 340);
            Point3Di p4 = new Point3Di(250, 101, 350);
            HollowBuilding building = new HollowBuilding(p1, p2, p3, p4, 4);
        }

    }
    public static class Two
    {
        public static void main(String[] argv)
            throws IOException
        {
            File dir = WorldPicker.menger5();
            BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(dir));

            List<GeometryTree> trees = new ArrayList<GeometryTree>();

            Random rand = new Random();
            int y0 = 80;
            int y= y0;
            int floorHeight=4;
            int x0 = 200;
            int x9 = 260;
            int z1 = 300;
            int z4 = 330;
            while (y<200) {
                int nFloors = rand.nextInt(2) + 3;

                int thick = 4;
                int x1 = rand.nextInt(20) + x0;
                int x4 = x9 - rand.nextInt(20) ;

                int y2 = nFloors *floorHeight + y;
                Point3Di p1 = new Point3Di(x1, y, z1);
                Point3Di p2 = new Point3Di(x1+thick, y, z1+thick);
                Point3Di p3 = new Point3Di(x4-thick, y2, z4-thick);
                Point3Di p4 = new Point3Di(x4, y2, z4);
                HollowBuilding building = new HollowBuilding(p1, p2, p3, p4, floorHeight);

                trees.add(building);

                y=y2;
            }
            trees.add(new Solid(0));

            GeometryTree scene = new GTSequence(trees);
            editor.apply(scene, new Bounds3Di(x0, y0, z1, x9+1, y+1, z4+1));

            editor.relight();
            editor.save();
        }
    }

    public Bounds3Di getBounds()
    {
        return new Bounds3Di(p1, new Point3Di(p4.x+1, p4.y+1, p4.z+1));
    }
}
