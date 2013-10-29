package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/24/13
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ZooDome

{

    public static final BlockPlusData GLOWSTONE = new BlockPlusData(89);
    public static final BlockPlusData FENCE = new BlockPlusData(113);
    private static final GeometryTree AIR = new GeometryTree.Solid(0);
    public static BlockPlusData IRON = new BlockPlusData(42);
    public static BlockPlusData GLASS = new BlockPlusData(0);

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.menger5()));


        int x0 = 105;
        int y0 = 100;
        int z0 = 650;
        int r1 = 100;
        GeometryTree stacks;
        if (false) {
            stacks = new GTPattern(x0, y0, z0, 1, 12, 1, new int[] {1,3,2, 0, 0,0,0,0, 0,0,0,89});
        } else {
            List<GeometryTree> layers = new ArrayList<GeometryTree>();

            int h1 = 18;
            for (int i=0; (i+1)*h1<r1; i++) {

                {
                    int y1 = y0+i*h1;
                    int y2 = y1+h1;
                    final Point3Di p1 = new Point3Di(x0-r1, y1, z0-r1);
                    final Point3Di p2 = new Point3Di(x0+r1, y2, z0+r1);

                    GaussLumps lumps = new GaussLumps(new Bounds3Di(p1, p2), 8, 3, 0.3, 1, 3, 0.7, 1.2 );
                    ParkLamps parkLamps = new ParkLamps(lumps, AIR);
//                    parkLamps.lamp = BlockPlusData.convert(91);
                    lumps.overlayer = parkLamps;
                    layers.add(lumps);
                }
            }
            stacks = new GTSequence(layers);
        }


        GeometryTree layer3 = new GTCone(new LineMath(new Point3D(x0, y0+r1+8, z0), new Point3D(0,1,0)),
            4, new GeometryTree.Solid(0), stacks);
        GeometryTree layer2 = new SpiralRamp(x0, y0, z0, 96, 6, layer3);
        editor.apply(new Hemisphere1(x0, y0, z0, r1, 98, layer2), new Bounds3Di(x0-r1, y0, z0-r1, x0+r1+1, y0+r1+1, z0+r1+1));

        editor.relight();
        editor.save();
    }

    public static class SpiralRamp
        implements GeometryTree
    {

        private final int x0;
        private final int y0;
        private final int z0;
        private final int r1;
        private final int width;
        private final GeometryTree inside;
        private final double slope;
        private final double[] thetas;

        public SpiralRamp(int x0, int y0, int z0, int r1, int width, GeometryTree inside)
        {
            this.x0 = x0;
            this.y0 = y0;
            this.z0 = z0;
            this.r1 = r1;
            this.width = width;
            this.inside = inside;

            slope = 0.3;

            double[] rval = fabThetaArray();
            thetas = rval;
        }

        public double[] fabThetaArray()
        {
            double[] rval = new double[256 * 2];

            for (int y_=y0*2; y_<rval.length; y_++) {

                double y = y_/2.0;
                double b2 = r1 * r1 - (y - y0) * (y - y0);
                if (b2<=0)
                    break;

                double r2 = Math.sqrt(b2) - width;

                if (r2<=0)
                    break;

                double theta1 = rval[y_ - 1] + 0.5 / slope / r2;
                while (theta1 >Math.PI) {
                    theta1 -= Math.PI*2;
                }
                rval[y_] = theta1;
            }
            return rval;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            double b2 = r1*r1 - (y-y0)*(y-y0);
            if (b2<0)
                return null;
            double b = Math.sqrt(b2)-width;
            if ( b*b> Math2.L22(x-x0,z-z0))
                return inside.pickFor(x,y,z);

            double theta = Math.atan2(x-x0, z-z0);
            int ptr = y * 2;
            double theta1 = thetas[ptr];
            double theta2 = thetas[ptr +1];
            double theta3 = thetas[ptr +2];

            if (angleBetween(theta1, theta, theta2)) {
                return new BlockPlusData(44, 0);
            } else if (angleBetween(theta2, theta, theta3)){
                return new BlockPlusData(44, 8);
            } else {
                return new BlockPlusData(0);
            }
        }

        public static boolean angleBetween(double before, double theta, double after)
        {
            if (after<before)
                after = after + 2*Math.PI;
            return angleBetween_(before, theta, after) || angleBetween_(before, theta+2*Math.PI, after);
        }

        public static boolean angleBetween_(double before, double theta, double after)
        {
            return before <theta && theta<= after;
        }
    }

    public static class SteelAndGlass
    implements GeometryTree
    {
        private final int x0;
        private final int z0;
        private int period;

        public SteelAndGlass(int x0, int z0, int period)
        {
            this.x0 = x0;
            this.z0 = z0;
            this.period = period;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            if ( (x-x0) % period == 0)
                return IRON;
            if ( (z-z0) %period ==0)
                return IRON;

            return GLASS;
        }
    }

    public static class Hemisphere1
        implements GeometryTree
    {
        private final GTEllipse in;

        public Hemisphere1(double x0, double y0, double z0, double r1, double r2, GeometryTree core)
        {
            GTEllipse out = new GTEllipse(x0, y0, z0, r1, new SteelAndGlass((int) x0, (int) z0, 16), null);

            in = new GTEllipse(x0, y0, z0, r2, core, out);
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            return in.pickFor(x,y,z);
        }
    }

    public static class ParkLamps
        implements GeometryTree
    {
        public static final BlockPlusData[] LAMP_ON_POST = new BlockPlusData[]{
            FENCE, FENCE, GLOWSTONE
        };
        public int x0 = 0;
        public int z0 = 0;
        public int spacing = 16;
        public GaussLumps lumps;
        private GeometryTree chain;

        public BlockPlusData[] lamp = LAMP_ON_POST;

        public ParkLamps(GaussLumps lumps, GeometryTree chain)
        {
            this.lumps = lumps;
            this.chain = chain;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            int tallness = 3;

            if (0==(x-x0)%spacing && 0==(z-z0)%spacing) {
                int y0 = 1+(int) lumps.yFor(x,z);

                int y1 = y-y0;
                if (y >= 0 && y1 < lamp.length)
                    return lamp[y1];
            }
            return chain.pickFor(x,y,z);
        }
    }
}
