package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.minecraftExplorer.*;

import java.awt.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/16/13
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class DistortedCellularGauss
{
    protected final double minSigma;
    protected final double clusterDiameter;
    protected final Random rand = new Random();
    protected double sigmaSpan;
    Map<Point, GaussLumps.HeightMap> elements = new HashMap<Point, GaussLumps.HeightMap>();

    public DistortedCellularGauss(int clusterDiameter, double minSigma, double maxSigma)
    {
        this.clusterDiameter = clusterDiameter;
        sigmaSpan = maxSigma - minSigma;
        this.minSigma = minSigma;
    }

    protected GaussLumps.HeightMap getElement(int u, int v)
    {
        Point key = new Point(u, v);
        GaussLumps.HeightMap rval = elements.get(key);
        if (rval==null) {
            double cx = u + clusterDiameter * (rand.nextDouble() - 0.5);
            double cz = v + clusterDiameter * (rand.nextDouble() - 0.5);
            double sigma = rand.nextDouble() * sigmaSpan + minSigma;
            rval = new SingleGaussMap(cx, cz, sigma);
            elements.put(key, rval);
        }
        return rval;
    }

    private class SingleGaussMap
        implements GaussLumps.HeightMap
    {
        double cx;
        double cy;
        double sigma;

        public SingleGaussMap(double cx, double cy, double sigma)
        {
            this.cx = cx;
            this.cy = cy;
            this.sigma = sigma;
        }

        @Override
        public double blargh(double x, double y)
        {
            return GaussLumps.gauss(Math2.L22(x - cx, y - cy), sigma);
        }
    }
}
