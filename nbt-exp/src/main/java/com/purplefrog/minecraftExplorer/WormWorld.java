package com.purplefrog.minecraftExplorer;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/16/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class WormWorld
{

    protected double x,y,z;
    protected double dx, dy, dz;
    protected double radius, dr;

    public final Bounds bounds;
    public int blockType = 3;
    protected final Random rand;

    public WormWorld(Bounds b, Random rand_)
    {
        bounds = b;

        rand = rand_;

        x = rand.nextDouble() * b.xSize() + b.x0;
        y = rand.nextDouble() * b.ySize() + b.y0;
        z = rand.nextDouble() * b.zSize() + b.z0;

        dx = rand.nextDouble()-0.5;
        dy = rand.nextDouble()-0.5;
        dz = rand.nextDouble()-0.5;

        radius = rand.nextDouble()*4+1;
        dr = (rand.nextDouble()-0.5)*0.1;
    }

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));

        Bounds b = new Bounds(90,81,150,180,200,250);

        editor.fillCube(0, b.x0, b.y0, b.z0, b.xSize(), b.ySize(), b.zSize());

        Random rand = new Random();

        for (int i=0; i<5; i++) {
            WormWorld ww = new WormWorld(b, rand);

            ww.render(editor);
        }

        RectangleEdges frame = new RectangleEdges(b);
        frame.render(editor, 1);

        editor.relight();

        editor.save();
    }

    public void render(BlockEditor editor)
        throws IOException
    {
        for (int i=0; i<100; i++) {
            iterate(editor);
        }
    }

    public void iterate(BlockEditor editor)
        throws IOException
    {
        for (int a = Math.max(bounds.x0, floor(x - radius)); a<=ceil(x+radius) && a < bounds.x1; a++) {
            for (int b= Math.max(bounds.y0, floor(y - radius)); b<=ceil(y+radius) && b < bounds.y1; b++) {
                for (int c= Math.max(bounds.z0, floor(z-radius)); c<=ceil(z+radius) && c < bounds.z1; c++) {
                    if (radius*radius >= l2(a-x, b-y, c-z)) {
                        editor.setBlock(a,b,c, blockType);
                    }
                }
            }
        }

        x = clamp(x+dx, bounds.x0, bounds.x1);
        y = clamp(y+dy, bounds.y0, bounds.y1);
        z = clamp(z+dz, bounds.z0, bounds.z1);

        dx = clamp(dx + (rand.nextDouble()-0.5) * 0.1
            + pusher(x, bounds.x0, bounds.x1, 0.1), -1, 1);
        dy = clamp(dy + (rand.nextDouble()-0.5) * 0.1
            + pusher(y, bounds.y0, bounds.y1, 0.1), -1, 1);
        dz = clamp(dz + (rand.nextDouble()-0.5) * 0.1
            + pusher(z, bounds.z0, bounds.z1, 0.1), -1, 1);

        radius = clamp(radius+dr, 0.6, 5);
        dr = clamp(dr +(rand.nextDouble()-0.5)*0.1
            +pusher(radius, 0.5, 5, 1), -0.4, 0.4);
    }

    public double pusher(double d, double min, double max, double scale)
    {
        return Math.max(0, 1 - scale * (d - min))
            - Math.max(0, 1 - scale * (max - d));
    }

    public static double clamp(double x, double min, double max)
    {
        if (x<min) {
            x = min;
        }
        if (x>max) {
            x = max;
        }
        return x;
    }

    public static double l2(double x, double y, double z)
    {
        return x*x+y*y+z*z;
    }

    public static int floor(double x)
    {
        return (int) Math.floor(x);
    }

    public static int ceil(double x)
    {
        return (int) Math.ceil(x);
    }

    public static class Bounds
    {
        public int x0, y0, z0;
        public int x1, y1, z1;

        public Bounds(int x0, int y0, int z0, int x1, int y1, int z1)
        {
            this.x0 = x0;
            this.y0 = y0;
            this.z0 = z0;
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
        }

        public Bounds(Point3Di p0, Point3Di p1)
        {
            x0 = p0.x;
            y0 = p0.y;
            z0 = p0.z;
            x1 = p1.x;
            y1 = p1.y;
            z1 = p1.z;
        }

        public int xSize()
        {
            return x1-x0;
        }

        public int ySize()
        {
            return y1-y0;
        }

        public int zSize()
        {
            return z1-z0;
        }

        public boolean contains(int x, int y, int z)
        {
            return x>=x0 && x<x1
                && y>=y0 && y<y1
                && z>=z0 && z<z1;
        }

        public Bounds outset(int dx, int dy, int dz)
        {
            return new Bounds(x0-dx, y0-dy, z0-dz, x1+dx, y1+dy, z1+dz);
        }
    }
}
