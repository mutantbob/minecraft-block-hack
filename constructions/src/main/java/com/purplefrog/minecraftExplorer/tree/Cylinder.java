package com.purplefrog.minecraftExplorer.tree;

import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.landscape.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/17/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Cylinder
    implements GeometryTree
{

    public final static BlockPlusData AIR = new BlockPlusData(0);
    public final static BlockPlusData WOOD = new BlockPlusData(17);

    GaussMultiNode nodes;
    protected final int lumpiness = 2;
    private Bounds3Di bounds;

    double x0;
    int y1, y2;
    double z0;
    double radius;
    double cellSize = 8;

    public Cylinder(int x0, int y1, int y2, int z0, double radius, int nLumps, double minSigma, double maxSigma)
    {
        this.x0 = x0;
        this.y1 = y1;
        this.y2 = y2;
        this.z0 = z0;

        this.radius = radius;

        double rPlus = this.radius +5;

        bounds = computeBounds(rPlus);

        Random rand = new Random();
        final GaussNode[] nodes_ = new GaussNode[nLumps];
        for (int i = 0; i < nodes_.length; i++) {
            double x1 =  this.radius * (rand.nextDouble()-0.5)*2;
            double z1 =  this.radius * (rand.nextDouble()-0.5)*2;
            if (GaussLumps.L2(x1, z1) > this.radius) {
                i--;
                continue;
            }
            double y3 = rand.nextInt(this.y2 - this.y1) + this.y1;
            double sigma = rand.nextDouble()*(maxSigma -minSigma) + minSigma;
            nodes_[i] = new GaussNode((x1+ this.x0)/cellSize, y3/cellSize, (z1+ this.z0)/cellSize, sigma);
        }
        nodes = new GaussMultiNode(nodes_);
    }

    public Bounds3Di computeBounds(double rPlus)
    {
        return new Bounds3Di((int) (x0-rPlus), (int) y1, (int)(z0-rPlus),
            (int)(x0+rPlus), (int)y2, (int)(z0+rPlus));
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        return pickFor(x, y, z);
    }

    private BlockPlusData pickFor(double x, double y, double z)
    {
        double val = GaussLumps.L2(x - x0, z - z0) + nodes.eval(x, y, z);

        if ( val < radius ) {
            return WOOD;
        } else {
            return AIR;
        }
    }

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.menger5()));

        Cylinder tree = new Cylinder(250, 80, 240, 450, 10.5, 40, 0.3, 1.0);
        Tapered tree2 = tree.new Tapered(250, 4, 60, 12);
        editor.apply(tree2, tree2.getBounds());

        editor.save();
        editor.relight();
    }

    public Bounds3Di getBounds()
    {
        return bounds;
    }

    public class Tapered
        implements GeometryTree
    {
        double y1, r1;
        double y2, r2;

        public Tapered(double y1, double r1, double y2, double r2)
        {
            this.y1 = y1;
            this.r1 = r1;
            this.y2 = y2;
            this.r2 = r2;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            double f = factorFor(y) ;
            return Cylinder.this.pickFor( (x - x0)/f + x0,
                y,
                 (z - z0)/f + z0);
        }

        private double factorFor(double y)
        {
            double r3 = (y-y1) * (r2-r1) / (y2-y1) +r1;

            return r3 /radius;
        }

        public Bounds3Di getBounds()
        {
            Bounds3Di b = Cylinder.this.getBounds();
            double r3 = (radius+5) * Math.max(factorFor(b.y0), factorFor(b.y1-1));

            return computeBounds(r3);
        }
    }
}
