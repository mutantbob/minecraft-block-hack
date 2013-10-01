package com.purplefrog.minecraftExplorer;

import java.io.*;
import java.util.*;

/**
 * This is a failed experiment.
 *
 * <p>
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/29/13
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class Valleyscape1
{


    protected int width;
    protected int length;
    protected final int[] terrain;
    protected final int terrainMax;

    public Valleyscape1(int dx, int dz, int terrainMax1)
    {
        width = dx;
        length = dz;
        terrain = new int[width * length];
        terrainMax = terrainMax1;
        Arrays.fill(terrain, terrainMax);

    }

    public static void main(String[] argv)
        throws IOException
    {

        Callback cb = new GUI();
        Random rand = new Random();

        Valleyscape1 vs = new Valleyscape1(70, 50, 80);
        vs.generate(cb, rand);

        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker .pickSaveDir()));

        int x0 = 350;
        int y0 = 100;
        int z0 = 5;

        WormWorld.Bounds bounds = new WormWorld.Bounds(x0, y0, z0, x0+vs.width, y0+vs.terrainMax+1, z0+vs.length);

        GeometryTree.Solid sky = new GeometryTree.Solid(0);
        GeometryTree.Solid ground = new GeometryTree.Solid(2);

        editor.apply(vs.new GT(x0, y0, z0, ground, sky), bounds);

        editor.relight();
        editor.save();
    }

    public class ErosionPicker
    {

        float score = 0;

        int pickedX=-1;
        int pickedZ=-1;

        public void evaluate(int x, int z)
        {
            int e0 = elevation(x, z);

            if (e0<1)
                return;

            double newScore = computeScore(e0, elevation(x - 1, z), elevation(x, z - 1),
                elevation(x + 1, z), elevation(x, z + 1));

            if (newScore > score) {
                score = (float) newScore;
                set(x,z);
            }
        }

        public void evaluate2(int e0, int x, int z)
        {
            if (e0< elevation(x, z)) {
                evaluate(x,z);
            }
        }

        public void set(int x, int z)
        {
            pickedX = x;
            pickedZ = z;
        }
    }
    public void generate(Callback cb, Random rand)
    {

        int passes = 0;

        int delay=width*length;

        ErosionPicker oldPicker = null;

        while (true) {
            cb.callback(width, length, terrain);

            ErosionPicker picker = new ErosionPicker();

            if (null != oldPicker) {
                int x=oldPicker.pickedX;
                int z =oldPicker.pickedZ;
                int e0 = elevation(x,z);

                picker.evaluate2(e0, x - 1, z);
                picker.evaluate2(e0, x, z - 1);
                picker.evaluate2(e0, x + 1, z);
                picker.evaluate2(e0, x, z + 1);
            }

            for (int i=0; i<20 || picker.score<=0; i++) {
                int x = rand.nextInt(width);
                int z = rand.nextInt(length);

                if (elevation(x,z) < terrainMax) {
                    picker.evaluate(x, z);
                } else {
                    int dir = rand.nextInt(8);
                    int dx= (dir+5)%3 -1;
                    int dz = ((dir+5)/3 %3) -1;

                    while (elevation(x,z) >= terrainMax) {
                        picker.set(x,z);
                        x += dx;
                        z += dz;
                    }
                    System.out.print(".");
                    break;
                }
            }

            if (picker.pickedX>=0) {
                terrain[pos_(picker.pickedX,picker.pickedZ)] --;
            }

            oldPicker = picker;

            passes++;

            delay--;
            if (delay<=0) {
                int peaks = countUneroded();
                if (peaks==0)
                    break;

                delay = peaks*2;
            }

            if (passes > width*length*20)
                break;// XXX kludge
        }

        System.out.println(passes+" erosions");
    }

    public int countUneroded()
    {
        int rval =0;

        for (int e : terrain) {
            if (e>=terrainMax)
                rval++;
        }
        return rval;
    }

    public static double computeScore(int e0, int ew, int es, int ee, int en)
    {
        Score1 x = new Score1(e0);

        x.add(ew);
        x.add(es);
        x.add(ee);
        x.add(en);

        return x.score();
    }


    public int elevation(int x,int z)
    {
        if (x<0 || x >=width ||
            z<0 || z >= length)
            return 0;

        return terrain[pos_(x, z)];
    }

    private int pos_(int x, int z)
    {
        return z*width + x;
    }

    public class GT
        implements GeometryTree
    {
        int x0, y0, z0;
        GeometryTree ground, sky;

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
                || z<0 || z >=length ) {
                return null;
            }

            GeometryTree delegate;
            if (y<=elevation(x,z))
                delegate= ground;
            else
                delegate= sky;

            if (null==delegate)
                return null;

            return delegate.pickFor(x_, y_, z_);
        }
    }

    public static class Score1
    {
        protected int nHigher=0;
        protected int e0;
        private int nLower =0;

        protected int sumHigher = 0;
        protected int maxHigher=0;
        protected int sumLower = 0;
        protected int maxLower = 0;

        public Score1(int e0)
        {
            this.e0 = e0;
        }

        public void add(int otherE)
        {
            if (otherE > e0) {
                nHigher++;
                int de = otherE - e0;
                if (de > maxHigher)
                    maxHigher = de;
                sumHigher += de;
            } else if (otherE < e0) {
                nLower++;
                int de = e0-otherE;
                if (de > maxLower)
                    maxLower  =de;
                sumLower += de;
            }
        }

        public double score()
        {
            double raw = (nLower + 1.0) / (nHigher + 1.0) *
                (maxLower + averageLower()) / (1 + maxHigher + averageHigher());

            if (nHigher <= 0) {
                return 0.1*raw;
            } else if (nLower==1) {
                return raw*3;
            } else if (nLower == 2) {
                return raw*2;
            } else {
                return raw;
            }

        }

        public double averageHigher()
        {
            if (nHigher >0)
                return sumHigher / (double) nHigher;
            else
                return 0;
        }

        public double averageLower()
        {
            if (nLower >0)
                return sumLower / (double) nLower;
            else
                return 0;
        }
    }

    public static class GUI
        implements Callback
    {
        @Override
        public void callback(int width, int length, int[] terrain)
        {
            // XXX
        }
    }

    public interface Callback
    {
        void callback(int width, int length, int[] terrain);
    }
}
