package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.minecraftExplorer.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/16/13
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuantizedDCG
    extends DistortedCellularGauss
{
    public int cellSize;
    Bounds3Di b;

    protected final double[] heightmap;

    public QuantizedDCG(int clusterDiameter, double minSigma, double maxSigma, Bounds3Di bounds, int cellSize)
    {
        super(clusterDiameter, minSigma, maxSigma);
        b = bounds;
        this.cellSize = cellSize;

        this.heightmap = fabricateHeightMap();
    }

    protected double[] fabricateHeightMap()
    {
        int dx = b.xSize();
        int dy = b.ySize();
        int dz = b.zSize();
        double[] heightmap = new double[dx*dz];

        for (int x=b.x0; x<b.x1; x++) {
            for (int z=b.z0; z<b.z1; z++) {
                heightmap[x-b.x0 + dx*(z-b.z0)] = height(x,z);
            }
        }
        System.out.println("height map");
        return heightmap;
    }

    private double height(int x, int z)
    {
        int r = 3;
        int uc = (int) Math.floor(x / (double)cellSize);
        int vc = (int) Math.floor(z / (double)cellSize);

        double x1 = x / (double) cellSize;
        double z1 = z / (double) cellSize;

        double rval =0;
        for (int u= uc - r; u<= uc+r; u++) {
            for (int v=vc-r; v<= vc+r; v++) {
                GaussLumps.HeightMap hm = getElement(u,v);
                rval += hm.blargh(x1, z1);
            }
        }
        return rval;
    }

    protected double heightAt(int x, int z)
    {
        int u = x-b.x0;
        int v = z-b.z0;

        int dx = b.xSize();
        return heightmap[u + v * dx];
    }

    public void addRandomNoise(double scale)
    {
        for (int i = 0; i < heightmap.length; i++) {
            heightmap[i] += rand.nextDouble()*scale;
        }
    }
}
