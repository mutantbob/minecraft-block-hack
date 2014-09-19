package com.purplefrog.minecraftExplorer.buildings;

import com.purplefrog.minecraftExplorer.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/29/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeBalconies
    implements GeometryTree.WithBounds
{
    public Materials materials = new Materials();

    Point3Di p0, p1, p8, p9;
    int balcony;
    int floorHeight = 6;
    List<Balcony> balconies;

    Random rand = new Random(4262);
    protected final int balconiesPerFace;

    public TreeBalconies()
    {
        p0 = new Point3Di(10, 84, 800);
        p9 = new Point3Di(60, 200, 850);
        balcony=8;

        p1 = new Point3Di(p0.x+balcony, p0.y, p0.z+balcony);
        p8 = new Point3Di(p9.x-balcony, p9.y, p9.z-balcony);

        balconiesPerFace = 3;

        Balcony[] balconies = fabRawBalconies();

        this.balconies = new ArrayList<Balcony>();
        for (Balcony balcony1 : balconies) {
            if (balcony1 != null)
                this.balconies.add(balcony1);
        }
    }

    private Balcony[] fabRawBalconies()
    {
        int nFloors = (p9.y-p0.y)/floorHeight;

        int perFloor = 4 * balconiesPerFace;
        int count = perFloor * nFloors;
        Balcony[] balconies = new Balcony[count];
        for (int i=0; i<count; i++) {
            boolean balconyBelow = i >= perFloor && balconies[i - perFloor] != null;
            boolean balconyBelowBelow = i >= perFloor*2 && balconies[i - 2*perFloor] != null;
            if (balconyBelow)
                continue; // skip this one.

            if (!balconyBelowBelow || 0==rand.nextInt(2)) {
                int floor = i/(perFloor);
                int q_ = i%perFloor;

                balconies[i] = makeBalcony(floor, q_/ balconiesPerFace, q_% balconiesPerFace, balconiesPerFace);
            }
        }
        return balconies;
    }

    public Balcony makeBalcony(int floor, int face, int q, int per)
    {
        Point3Di p3, p4;
        int y1 = p0.y + floor * floorHeight;
        int y2 = y1 + 2*floorHeight - 1;
        switch (face) {
            case 0: {
                int z1 = interp(q, per, p1.z, p8.z);
                int z2 = interp((q+1), per, p1.z, p8.z) -1;
                p3 = new Point3Di(p0.x, y1, z1);
                p4 = new Point3Di(p1.x+1, y2, z2);
            }
            break;
            case 2: {
                int z1 = interp(q, per, p1.z, p8.z);
                int z2 = interp((q+1), per, p1.z, p8.z) -1;
                p3 = new Point3Di(p8.x-1, y1, z1);
                p4 = new Point3Di(p9.x-1, y2, z2);
            }
            break;
            case 1: {
                int x1 = interp(q, per, p1.x, p8.x );
                int x2 = interp((q+1), per, p1.x, p8.x ) -1;
                p3 = new Point3Di(x1, y1, p0.z);
                p4 = new Point3Di(x2, y2, p1.z+1);
            }
            break;
            default: {
                int x1 = interp(q, per, p1.x, p8.x );
                int x2 = interp((q+1), per, p1.x, p8.x ) -1;
                p3 = new Point3Di(x1, y1, p8.z-1);
                p4 = new Point3Di(x2, y2, p9.z-1);
            }
            break;
        }
        return new Balcony(p3, p4, rand);
    }

    public static int interp(int q, int per, int a, int b)
    {
        return a + q * (b - a)/per;
    }

    @Override
    public BlockPlusData pickFor(int x, int y_, int z)
    {
        int y = y_ - p0.y;

        if (x>=p1.x && x<p8.x
            && (z==p1.z || z+1 == p8.z)) {
            return materials.northWall.pickFor(x-p1.x, y, z-p1.z);
        }
        if (z>=p1.z && z<p8.z
            && ( x==p1.x || x+1==p8.x)) {
            return materials.westWall.pickFor(x-p1.x, y, z-p1.z);
        }

        if (1 == y%floorHeight && isCore(x,z))
            return materials.interiorFloor;

        for (Balcony balcony1 : balconies) {
            BlockPlusData rval = balcony1.pickFor(x, y_, z);
            if (rval != null)
                return rval;
        }

        return materials.air;
    }

    private BlockPlusData obsoleteTail(int x, int y, int z)
    {
        switch (y % floorHeight) {
            case 0:
                return level0(x,y,z);
            case 1:
                return level1(x, y, z);
            case 2:
                return level2(x, y, z);
        }


        return materials.air;
    }

    public BlockPlusData level0(int x,int y, int z)
    {
        int f = y/floorHeight;
        if (isBalcony(x, z, f) && !isCore(x,z)) {
            return materials.balconySubFloor;
        } else {
            return materials.air;
        }
    }

    public BlockPlusData level1(int x, int y, int z)
    {
        int f = y/floorHeight;

        if (isCore(x, z)) {
            return materials.interiorFloor;
        }

        if (isBalcony(x, z, f)) {
            return materials.balconyFloor;
        }

        return materials.air;
    }

    public BlockPlusData level2(int x, int y, int z)
    {
        int f = y/floorHeight;
        if (isBalcony(x,z,f) && !isCore(x,z)) {

            if (0==x%8 && 0==z%8)
                return new BlockPlusData(50);

            if (0== rand.nextInt(12)) {
                return new BlockPlusData(6, rand.nextInt(6));
            }
        }

        return materials.air;
    }

    private boolean isCore(int x, int z)
    {
        return x>=p1.x && x<p8.x
            && z>=p1.z && z<p8.z;
    }

    private boolean isBalcony(int x, int z, int f)
    {
        int x4,z4, x5, z5;

        switch(f%4) {
            case 0:
                x4 = p0.x;
                x5 = p8.x;
                z4 = p0.z;
                z5 = p8.z;
                break;
            case 1:
                x4 = p0.x;
                x5 = p8.x;
                z4 = p1.z;
                z5 = p9.z;
            break;
            case 2:
                x4 = p1.x;
                x5 = p9.x;
                z4 = p1.z;
                z5 = p9.z;
                break;
            default:
                x4 = p1.x;
                x5 = p9.x;
                z4 = p0.z;
                z5 = p8.z;
                break;

        }

        return x >= x4 && x < x5
            && z >= z4 && z < z5;
    }

    @Override
    public Bounds3Di getBounds()
    {
        return new Bounds3Di(p0, p9);
    }

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.menger5()));

        TreeBalconies building = new TreeBalconies();

        editor.apply(building, new Bounds3Di(building.p0, new Point3Di(100, 200, 900)));

        editor.relight();
        editor.save();
    }

    public static class Materials
    {

        public BlockPlusData interiorFloor = new BlockPlusData(1);
        public BlockPlusData balconyFloor = new BlockPlusData(2);
        public BlockPlusData balconySubFloor= new BlockPlusData(44, 8);
        public BlockPlusData air = new BlockPlusData(0);
        public GeometryTree northWall = new GTPattern(0,0,0, 1, 6, 1,
            98, 98,98,102, 102, 102);
        public GeometryTree westWall = northWall;
    }

    public static class Balcony
        implements GeometryTree
    {
        private final GeometryTree tree;
        public Point3Di p0, p9;
        public final Bounds3Di bounds;

        public GeometryTree wall = new GTPattern(0,0,0, 1, 99, 1,
            new BlockPlusData(44, 8),
            new BlockPlusData(98),
            new BlockPlusData(101)
        );

        public GeometryTree interior = new GTPattern(0,0,0, 1, 99, 1,
            new BlockPlusData(44, 8),
            new BlockPlusData(2)
        );

        public Balcony(Point3Di p0, Point3Di p9, Random rand)
        {
            this.p0 = p0;
            this.p9 = p9;
            bounds = new Bounds3Di(p0, p9);

            Tree1 tree2 = new Tree1(rand, treeCore(rand), new Solid(0));
            tree = new Tree1(rand, treeCore(rand), tree2);
        }

        private Point3Di treeCore(Random rand)
        {
            return new Point3Di((int) interp(p0.x+1, p9.x-1, rand.nextDouble()),
                        p0.y+(int) interp(5,8, rand.nextDouble()),
                        (int) interp(p0.z+1, p9.z-1, rand.nextDouble()));
        }

        private double interp(double a, double b, double t)
        {
            return a + (b-a)*t;
        }

        @Override
        public BlockPlusData pickFor(int x, int y_, int z)
        {
            if (!bounds.contains(x,y_,z))
                return null;

            BlockPlusData rval = phase1(x, y_, z);
            if (rval==null || rval.blockType==0)
                return tree.pickFor(x, y_, z);
            return rval;
        }

        private BlockPlusData phase1(int x, int y_, int z)
        {
            int y = y_ - p0.y;

            if (x== p0.x || x+1==p9.x ||
                z==p0.z || z+1==p9.z) {
                return wall.pickFor(0, y, 0);
            }

            return interior.pickFor(0,y,0);
        }
    }

    /**
     * Trunk starts at {@link #p1} and goes down forever.  leaves are within the sphere of {@link #leafRadius} around p1.
     */
    public static class Tree1
        implements GeometryTree
    {
        public TreeMaterials mats;

        public Point3Di p1;
        public double leafRadius;
        private GeometryTree chain;

        public Tree1(TreeMaterials mats, Point3Di p1, double leafRadius, GeometryTree chain)
        {
            this.mats = mats;
            this.p1 = p1;
            this.leafRadius = leafRadius;
            this.chain = chain;
        }

        public Tree1(Random rand, Point3Di p1, GeometryTree chain)
        {
            this(TreeMaterials.randomTree16(rand), p1, 2+1.5*rand.nextDouble(), chain);
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            if (x==p1.x && z==p1.z && y<=p1.y)
                return mats.logVertical();

            if (Math2.L22(x-p1.x, y-p1.y, z-p1.z) <= leafRadius*leafRadius)
                return mats.leaves;

            return chain==null ? null : chain.pickFor(x, y, z);
        }
    }


}
