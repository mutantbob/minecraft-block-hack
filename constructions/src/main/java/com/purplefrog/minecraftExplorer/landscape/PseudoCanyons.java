package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.minecraftExplorer.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/31/13
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PseudoCanyons
{
    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.menger5()));

        int x0=375, y0=130, z0= 250;

        DistortedCellularGauss.D3 dcg = new DistortedCellularGauss.D3(0.3, 1.0,
            0.2, 2, 2, new int[] { 0,1,0}, new int[] { 0,0,1 });


        DistortedCellularGauss.D3 dcg2 = new DistortedCellularGauss.D3(0.3, 1.0,
            0.2, 2, 2, new int[] { 0,1,0}, new int[] { 0,0,1 });


        F3 base = new Bacon(new GaussMultiNode(new TranslatedF3(dcg, 0.6, 0, 0),
            new TranslatedF3(dcg2, -0.6,0,0)), x0, y0, z0);
        GeometryTree gt = new F3.GT(base, new GeometryTree.Solid(0), 3.5, new GeometryTree.Solid(2));

        editor.apply(gt, plusminus(new Point3Di(x0,y0,z0), 20, 50, 60));

        editor.relight();
        editor.save();
    }

    private static Bounds3Di plusminus(Point3Di center, int dx, int dy, int dz)
    {
        return new Bounds3Di(center.x - dx, center.y -dy, center.z - dz,
            center.x+dx, center.y+dy, center.z+dz);
    }

    private static class Bacon
        implements F3
    {
        private final F3 dcg;
        private final int x0;
        private final int y0;
        private final int z0;

        public Bacon(F3 dcg, int x0, int y0, int z0)
        {
            this.dcg = dcg;
            this.x0 = x0;
            this.y0 = y0;
            this.z0 = z0;
        }

        @Override
        public double eval(double x, double y, double z)
        {

            double x_ = x - x0;
            double y_ = y - y0;
            double f1 = -Math.abs(x_ / 32);
            f1 = -Math2.L2(x_/16, y_/32);
            double f2 = 2.5*dcg.eval(x_ / 16.0, y_ / 8.0, (z - z0) / 8.0);
            return f1 + f2;
        }
    }
}
