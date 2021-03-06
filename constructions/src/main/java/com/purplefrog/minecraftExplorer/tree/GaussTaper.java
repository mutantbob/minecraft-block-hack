package com.purplefrog.minecraftExplorer.tree;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.landscape.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/21/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class GaussTaper
    implements F3
{
    private final double cellSize=16;
    public final double sigma = 0.8;
    public int x0, y1, y2, z0;
    public double r1, r2;
    public double inflation;

    public Bounds3Di bounds;

    public GaussTaper(int x0, int y1, int y2, int z0, double inflation, double r1, double r2)
    {
        this.x0 = x0;
        this.y1 = y1;
        this.y2 = y2;
        this.z0 = z0;

        this.r1 = r1;
        this.r2 = r2;
        this.inflation = inflation;

        bounds = computeBounds(this.r1 +5);
    }


    private Bounds3Di computeBounds(double rPlus)
    {
        return new Bounds3Di((int) (x0-rPlus), y1, (int)(z0-rPlus),
            (int)(x0+rPlus), y2, (int)(z0+rPlus));
    }

    private double radiusFor(double y)
    {
        double r3 = (y-y1) * (r2-r1) / (y2-y1) +r1;

        return r3;
    }

    @Override
    public double eval(double x, double y, double z)
    {
        double r = radiusFor(y);
        double q = Math2.L22(x - x0, z - z0);
        double pre = GaussLumps.gauss(q / (r * r), sigma);
        return inflation * pre;
    }

    public static double PI = Math.PI;
    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.menger5()));

        Random rand = new Random();

        if (false) {
            GeometryTree.WithBounds gt2 = GaussLumps.GaussIslands.cliche1(editor);
            editor.apply(gt2, gt2.getBounds());

            GeometryTree.WithBounds tree2 = cliche1(rand);
            GeometryTree gt3 = new GTSequence(tree2,gt2);
            Bounds3Di b2 = gt2.getBounds();
            editor.apply(gt3, tree2.getBounds().max(b2));

        } else {
            GeometryTree.WithBounds tree2 = cliche1(rand);
            editor.apply(tree2, tree2.getBounds());
        }
        editor.save();
        editor.relight();

    }

    public static GeometryTree.WithBounds cliche1(Random rand)
    {
        int maxr = 70;
        int x0 = 225+5+maxr;
        int y1 = 80;
        int y2 = 240;
        int z0 = 400+5+maxr;
        GaussTaper t1 = new GaussTaper(x0, y1, y2, z0, 10, 12, 2.2);

        List<LimbWithBranches> limbs = fabLimbs(rand, maxr, x0, y1, y2, z0);

//        SimpleLimb limb = new SimpleLimb(new Point3D(x0, 120, z0), new Point3D(x0, 140, z0+50),
//            8, 4, 0, 50, 10);
        F3 combo = new GaussMultiNode(limbs, t1);
        GeometryTree tree = new GT(combo, new GeometryTree.Solid(new BlockPlusData(17, 1)),
            1, new GeometryTree.Solid(Clouds.SPARSE),
            0.2, new GeometryTree.Solid(Cylinder.AIR)
//            1, new GeometryTree.Solid(Cylinder.AIR),
//            0.2, null
        );

        Bounds3Di bounds1 = t1.computeBounds(maxr);

        return new GTSequence.WithBounds(bounds1, tree);
    }

    public static GeometryTree.WithBounds cliche2(Random rand)
    {
        int maxr = 70;
        int x0 = 225+5+maxr;
        int y1 = 80;
        int y2 = 240;
        int z0 = 400+5+maxr;
        GaussTaper t1 = new GaussTaper(x0, y1, y2, z0, 10, 12, 2.2);

        List<LimbWithBranches> limbs = fabLimbs(rand, maxr, x0, y1, y2, z0);

//        SimpleLimb limb = new SimpleLimb(new Point3D(x0, 120, z0), new Point3D(x0, 140, z0+50),
//            8, 4, 0, 50, 10);
        F3 combo = new GaussMultiNode(limbs, t1);
        GeometryTree tree = new GT(combo, new GeometryTree.Solid(new BlockPlusData(17, 1)),
            0.2, null
        );

        List<F3> leaves_ = new ArrayList<F3>();
        for (LimbWithBranches limb : limbs) {
            leaves_.addAll(limb.lumpCloud(rand));
        }
        F3 leafCloud = new GaussMultiNode(leaves_);
        GeometryTree leaves = new GT(leafCloud, new SuperSphere.DoomChipCookie(), 0.1, null);

        Bounds3Di bounds1 = t1.computeBounds(maxr);

        return new GTSequence.WithBounds(bounds1, tree, leaves);
    }

    private static List<LimbWithBranches> fabLimbs(Random rand, int maxr, int x0, int y1, int y2, int z0)
    {
        List<LimbWithBranches> limbs = new ArrayList<LimbWithBranches>();

        double theta = 0;
        for (int i=0; i<10; i++) {
            double y5 = BLI(0, i, 10, y1, y2);
            double y6 = BLI(0, i, 10-1, 10, 4);
            double a9 = BLI(y1, y5, y2, maxr, 10);
            double x6 = Math.cos(theta)* a9;
            double z6 = Math.sin(theta)* a9;
            Point3D origin = new Point3D(x0, y5, z0);
            double r1 = BLI(y1, y5, y2, 4 , 2);
            double r2 = BLI(y1, y5, y2, 2 , 0.5);
            LimbWithBranches limb =
                new LimbWithBranches
//                    new SimpleLimb
                    (origin, new Point3D(x6,y6,z6), r1, r2, 0, a9, 10
                        , rand
                    );
            limbs.add(limb);

            theta = theta + rand.nextDouble()*0.4 -0.2+ PI;
            double x7 = Math.cos(theta)*a9;
            double z7 = Math.sin(theta)*a9;
            limb =
//                new SimpleLimb
                new LimbWithBranches
                (origin, new Point3D(x7,y6,z7), r1, r2, 0, a9, 10
                    , rand
                );
            limbs.add(limb);

            theta = theta + rand.nextDouble()*0.2 -0.1 + PI/2;
        }
        return limbs;
    }

    private static double BLI(int y1, double y, int y2, double q1, double q2)
    {
        return SimpleLimb.BLI(y1, y, y2, q1, q2);
    }

    public Bounds3Di getBounds()
    {
        return bounds;
    }
}
