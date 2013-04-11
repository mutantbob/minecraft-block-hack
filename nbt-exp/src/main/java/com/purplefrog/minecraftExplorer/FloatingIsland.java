package com.purplefrog.minecraftExplorer;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/8/13
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class FloatingIsland
{
    public static void main(String[] argv)
        throws IOException
    {

        DepthMap bottom = new DepthMap(100, 50);

        Random rand = new Random();

        while (true) {
            RandomCoord rc = new RandomCoord(rand);
            for (int x=0; x<bottom.w; x++) {
                for (int y=0; y<bottom.h; y++) {
                    if (bottom.get(x,y) == 0)
                        rc.addCoord(x,y);
                }
            }

            if (rc.n <= 0) {
                break;
            }

            excavateCone(bottom, rand, rc.x, rc.y);

            if (false)
                break;
        }

        if (false) {
            for (int y=0; y<bottom.h; y++) {
                for (int x=0; x<bottom.w; x++) {
                    System.out.print(bottom.get(x,y)+" ");
                }
                System.out.println();
            }

            return;
        }




        File saveDir = GenerateMaze1.pickSaveDir();

        BlockEditor editor = new BlockEditor(new MinecraftWorld(saveDir));

        int x0= 80;
        int z0= 80;
        int y0 = 100;

        for (int x=0; x<bottom.w; x++) {
            for (int z=0; z<bottom.h; z++) {
                int k = (int) bottom.get(x, z);
                for (int y= 0; y<12; y++) {
                    editor.setBlock(x+x0,y+y0,z+z0, y>k?2:0);
                }
            }
        }

        editor.relight();

        editor.save();
    }

    public static void excavateCone(DepthMap bottom, Random rand, int cx, int cy)
    {
        int wallaby = rand.nextInt(9)+1;
        System.out.println("cone[" +wallaby+"] @" +cx+ "," + cy);
        int hippo = wallaby*4;
        DepthMap cone = new DepthMap(hippo*2+1, hippo*2+1);
        sloppyCone(cone, rand, hippo, hippo, wallaby);

        for (int y=hippo-4; y<=hippo+4; y++) {
            for (int x=hippo-4; x<=hippo+4; x++) {
                System.out.print(cone.get(x,y)+" ");
            }
            System.out.println();
        }

        for (int x=Math.max(0, hippo-cx); x<Math.min(cone.w, bottom.w +hippo-cx) ; x++) {
            for (int y=Math.max(0, hippo-cy); y<Math.min(cone.h, bottom.h +hippo-cy); y++) {
                int x2 = x + cx - hippo;
                int y2 = y + cy - hippo;
                bottom.set(x2, y2,
                    Math.max(bottom.get(x2, y2), cone.get(x, y)));
            }
        }
    }

    private static void sloppyCone(DepthMap map, Random rand, int cx, int cy, int height)
    {
        map.set(cx, cy, height);
        for (int r = 1; ; r++) {

            boolean dirty = false;

            dirty = maybeUpdate(map, rand, cx, cy - r, map.get(cx, cy - r + 1), null) || dirty;
            dirty = maybeUpdate(map, rand, cx - r, cy, map.get(cx - r + 1, cy), null) || dirty;
            dirty = maybeUpdate(map, rand, cx + r, cy, map.get(cx + r - 1, cy), null) || dirty;
            dirty = maybeUpdate(map, rand, cx, cy + r, map.get(cx, cy + r - 1), null) || dirty;
            for (int q=1; q<r; q++) {
                int x = cx-q;
                int y = cy-r+q;
                dirty = maybeUpdate(map, rand, x, y, map.get(x, y + 1), map.get(x + 1, y)) || dirty;
                y = cy+r-q;
                dirty = maybeUpdate(map, rand, x, y, map.get(x, y - 1), map.get(x + 1, y)) || dirty;
                x = cx+q;
                dirty = maybeUpdate(map, rand, x, y, map.get(x, y - 1), map.get(x - 1, y)) || dirty;
                y = cy-r+q;
                dirty = maybeUpdate(map, rand, x, y, map.get(x, y + 1), map.get(x - 1, y)) || dirty;
            }
            if (!dirty)
                break;
        }
    }

    public static boolean maybeUpdate(DepthMap bottom, Random rand, int x, int y, Float d1, Float d2)
    {
        double newD;
        if (d1==null) {
            if (d2==null) {
                return false;
            } else {
                newD = d2 - erosion(rand);
            }
        } else {
            if (d2==null) {
                newD = d1-erosion(rand);
            } else {
                newD = Math.max(d2-erosion(rand), d1-erosion(rand));
            }
        }
        if (newD > bottom.get(x,y)) {
            bottom.set(x,y,newD);
            return true;
        } else {
            return false;
        }
    }

    private static double erosion(Random rand)
    {
        double q = rand.nextDouble();
        return q*q *2 +0.5;


    }

    private static int[][] array2d(int dx, int dy)
    {
        int[][] rval = new int[dx][];
        for (int i = 0; i < rval.length; i++) {
            rval[i] = new int[dy];
        }
        return rval;
    }

    public static class DepthMap
    {
        final public int w, h;

        protected float[] data;

        public DepthMap(int w, int h)
        {
            this.w = w;
            this.h = h;

            data = new float[w*h];
        }

        public float get(int x, int y)
        {
            if (x<0 || x>=w || y<0 || y>=h)
                throw new IllegalArgumentException();
            return data[y*w +x];
        }

        public Float get_(int x, int y)
        {
            if (x<0 || x>=w || y<0 || y >=h)
                return null;
            return get(x,y);
        }

        public void set(int x, int y, double val)
        {
            data[y*w+x] = (float) val;
        }
    }
}
