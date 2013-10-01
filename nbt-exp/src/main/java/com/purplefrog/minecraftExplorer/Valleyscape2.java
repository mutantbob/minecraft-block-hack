package com.purplefrog.minecraftExplorer;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/29/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Valleyscape2
{
    int width;
    int length;
    int[] terrain;
    int maxElevation;

    public Valleyscape2(int width, int length, int maxElevation)
    {

        this.width = width;
        this.length = length;
        this.maxElevation = maxElevation;

        terrain = new int[width*length];

    }

    public void addMountain(double x, double z, Random rand)
    {
        double e = Math.min( Math.max(width, length) *0.3, maxElevation);
        addMountain(x,e,z,rand);
    }

    public void addMountain(double x, double elevation, double z, Random rand)
    {
        int nSpines = rand.nextInt(4)+2;

        drawCone(x,elevation,z);
        for (int g = 0; g<nSpines; g++) {
            double theta = (g+2*rand.nextDouble())/nSpines *Math.PI*2;

            double r1 = (2 + rand.nextDouble()) * elevation;
            double x2 = x + Math.cos(theta) * r1;
            double e2 = rand.nextDouble() * elevation / 6;
            double z2 = z + Math.sin(theta) * r1;
            drawCone(x2, e2, z2);
            drawSpine(x,elevation,z, x2, e2, z2, rand, 2);
        }
    }

    public void drawSpine(double x, double e1, double z, double x2, double e2, double z2, Random rand, int nForks)
    {
        double span = l2(x-x2, 0, z-z2);


        if (span<1)
            return;

        double noise = 0.5;
        double x3 = randInterpolate(x, x2, rand, noise);
        double z3 = randInterpolate(z, z2, rand, noise);
        double e3 = randInterpolate(e1, e2, rand, 0.2);
        drawCone(x3, e3, z3);

        if (rand.nextDouble()*span <= nForks ) {
            double vx = x-x3 + x2-x3;
            double vz = z-z3 + z2-z3;
            forkSpine(rand, nForks, x3, e3, z3, -vx, -vz);
            nForks--;
        }

        int f1 = nForks/2;
        int f2 = nForks - f1;
        drawSpine(x, e1, z, x3, e3, z3, rand, f1);
        drawSpine(x3, e3, z3, x2, e2, z2, rand, f2);
    }

    public void forkSpine(Random rand, int nForks, double x3, double e3, double z3, double vx, double vz)
    {
        double r2 = l2(vx, 0, vz);

        double r3 = (2+rand.nextDouble())*e3;
        double x4 = x3 + vx/r2 * r3;
        double z4 = z3 + vz/r2*r3;
        drawSpine(x3, e3, z3, x4, 0, z4, rand, nForks-1);
    }

    public void drawCone(double x0, double e0, double z0)
    {
        System.out.println(x0+","+z0+"   +"+e0);
        for (int x=0; x<width; x++) {
            for (int z=0; z<length;z++) {
                double r = Math.sqrt(l2(x - x0, 0, z - z0));
                int q = terrain[pos_(x,z)];
                int newE = (int) Math.floor(e0-r);
                if (newE>q) {
                    terrain[pos_(x,z)] = newE;
                }
            }
        }
    }

    public int pos_(int x, int z)
    {
        return z*width +x;
    }

    public static double randInterpolate(double a, double b, Random rand, double noise)
    {
        return (a + b)/2 + ((rand.nextDouble()-0.5)*noise)*(b - a);
    }

    public static double l2(double x, double y, double z)
    {
        return GTEllipse.l2(x,y,z);
    }


    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));

        Valleyscape2 vs = new Valleyscape2(200, 150, 80);

        Random rand = new Random();

        {
            int x = vs.width / 4 + rand.nextInt(vs.width / 2);
            int z = vs.length / 4 + rand.nextInt(vs.length / 2);
            vs.addMountain(x, z, rand);
        }

        int x0 = 350;
        int y0 = 100;
        int z0 = 5;

        WormWorld.Bounds bounds = new WormWorld.Bounds(x0, y0, z0, x0+vs.width, y0+vs.maxElevation+1, z0+vs.length);
        GeometryTree ground = new SuperSphere.DoomChipCookie();
        GeometryTree sky = new GeometryTree.Solid(0);
        editor.apply(vs.new GT(x0, y0, z0, ground, sky), bounds);

        editor.relight();

        editor.save();
    }

    public class GT
        implements GeometryTree
    {
        private final int x0;
        private final int y0;
        private final int z0;
        private final GeometryTree ground;
        private final GeometryTree sky;

        public GT(int x0, int y0, int z0, GeometryTree ground, GeometryTree sky)
        {

            this.x0 = x0;
            this.y0 = y0;
            this.z0 = z0;
            this.ground = ground;
            this.sky = sky;
        }

        @Override
        public BlockPlusData pickFor(int x_, int y_, int z_)
        {
            int x = x_-x0;
            int y = y_-y0;
            int z = z_-z0;

            if (x<0 || x>=width
                || z<0 || z>= length)
                return null;

            int e = terrain[pos_(x,z)];
            GeometryTree gt;
            if (e<y) {
                gt = sky;
            } else {
                gt = ground;
            }

            return gt.pickFor(x_, y_, z_);
        }
    }
}
