package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/19/13
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SuperSphere
{
    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));

        int cx = 66;
        int cy = 128;
        int cz = 406;
        Point3D c = new Point3D(cx, cy, cz);

        GeometryTree tree = doomGlowMatroska(c);
        tree = doomGlow(c);
        tree = doomLatticeGlow(c);
//        tree = cylinderLayers(c);

        editor.apply(tree, new Bounds3Di(cx-126, cy-126, cz-126, cx+126, cy+126, cz+126));

        editor.relight();
        editor.save();
    }

    public static GeometryTree cylinderLayers(Point3D c)
    {
        GeometryTree icos = icosaSpike(c, 10, new DoomChipCookie(), null);

        GeometryTree[] nest = new GeometryTree[12];

        GeometryTree air = new GeometryTree.Solid(0);

        for (int i=0; i<nest.length; i++) {
            nest[i] = new GTEllipse(c, i*10+5, (0==(i&1)) ? air: icos);
        }

        return new GTFirstPick(
            new GTFirstPick(nest),
            new GTEllipse(c, 125, 0)
        );
    }

    public static GeometryTree doomGlowMatroska(Point3D c)
    {
        GeometryTree dough = halfDoom();

        GeometryTree air = new GeometryTree.Solid(0);

        GeometryTree holy = new GTFirstPick(icosaSpike(c, 10, new GeometryTree.Solid(0), null), dough);

        return matroska(c, air, holy);
    }

    public static GeometryTree doomGlow(Point3D c)
    {
        GeometryTree dough = halfDoom();

        return new GTFirstPick(
            new GTEllipse(c, 115, dough),
            new GTEllipse(c, 125, 0)
        );
    }

    public static GeometryTree doomLatticeGlow(Point3D c)
    {
        GeometryTree dough = latticeHalfDoom();

        return new GTFirstPick(
            new GTEllipse(c, 115, dough),
            new GTEllipse(c, 125, 0)
        );
    }

    public static GeometryTree halfDoom()
    {
        GeometryTree doomChipCookie = new DoomChipCookie();

        GeometryTree glowChipCookie = new GlowChipCookie();

        return new PlanarSplit(0,128,0, 0,1,0, doomChipCookie, glowChipCookie);
    }

    public static GeometryTree latticeHalfDoom()
    {
        final GeometryTree doomChipCookie = new DoomChipCookie(15);

        GeometryTree glowChipCookie = new GlowChipCookie();

        GeometryTree lattice = new GTLattice(doomChipCookie, new GeometryTree.Solid(1), 10, 30);

        return new PlanarSplit(0,128,0, 0,1,0, new GTShifted(lattice, 0, 128, 0), glowChipCookie);
    }

    private static GeometryTree matroska(Point3D c, GeometryTree air, GeometryTree solid)
    {
        GeometryTree[] nest = new GeometryTree[12];
        for (int i=0; i<nest.length; i++) {
            nest[i] = new GTEllipse(c, i*10+5, (0==(i&1)) ? air: solid);
        }
        return new GTFirstPick(
            new GTFirstPick(nest),
            new GTEllipse(c, 125, 0)
        );
    }

    private static GeometryTree icosaSpike(Point3D c, int radius, GeometryTree inside, GeometryTree outside)
    {
        GeometryTree[] arr = new GeometryTree[6];
        double gr = 1.6;
        icosaNode(arr, 0, gr, 1, c, inside, outside, radius);
        icosaNode(arr, 3, gr, -1, c, inside, null, radius);
        return new GTFirstPick(arr);
    }

    public static void icosaNode(GeometryTree[] arr, int base, double gr, double one, Point3D c, GeometryTree inside, GeometryTree outside, double radius)
    {
        arr[base] = new GTCylinder(c, new Point3D(0,gr, one), radius, inside, outside);
        arr[base+1] = new GTCylinder(c, new Point3D(gr, one, 0), radius, inside, outside);
        arr[base+2] = new GTCylinder(c, new Point3D(one, 0, gr), radius, inside, outside);
    }

    public static class DoomChipCookie
        implements GeometryTree
    {
        Random rand = new Random();
        protected int denominator;

        public DoomChipCookie()
        {
            this(20);
        }

        public DoomChipCookie(int denom)
        {
            denominator = denom;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            if (rand.nextInt(denominator) == 0) {
                int dynamite = 46;
                return new BlockPlusData(dynamite);
            } else {
                return new BlockPlusData(1);
            }
        }
    }

    private static class GlowChipCookie
        implements GeometryTree
    {
        Random rand = new Random();

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            if (rand.nextInt(20) == 0) {
                int glowstone = 89;
                return new BlockPlusData(glowstone);
            } else {
                return new BlockPlusData(1);
            }
        }
    }

    public static class GTLattice
        implements GeometryTree
    {
        private final GeometryTree inside;
        protected final Solid outside;
        protected final int denom;
        protected final int num;

        public GTLattice(GeometryTree inside, Solid outside, int insideNumerator, int denominator)
        {
            this.inside = inside;
            this.outside = outside;
            num = insideNumerator;
            denom = denominator;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {

            int x2 = BlockTemplate.mod_(x, denom);
            int y2 = BlockTemplate.mod_(y, denom);
            int z2 = BlockTemplate.mod_(z, denom);
            int count = (x2 < num ? 1 : 0)
                + (y2 < num ? 1 : 0)
                + (z2 < num ? 1 : 0);
            return (2 <= count ? inside : outside).pickFor(x,y,z);
        }
    }

    public static class GTShifted
        implements GeometryTree
    {
        private final GeometryTree base;
        protected final int dx;
        protected final int dy;
        protected final int dz;

        public GTShifted(GeometryTree base, int dx, int dy, int dz)
        {
            this.base = base;
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            return base.pickFor(x- dx, y- dy, z- dz);
        }
    }
}
