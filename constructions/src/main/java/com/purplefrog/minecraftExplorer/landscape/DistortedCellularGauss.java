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
public abstract class DistortedCellularGauss<T>
{
    protected final double minSigma;
    protected final Random rand = new Random();
    protected double sigmaSpan;
    Map<Point, T> elements = new HashMap<Point, T>();

    public DistortedCellularGauss(double minSigma, double maxSigma)
    {
        sigmaSpan = maxSigma - minSigma;
        this.minSigma = minSigma;
    }

    protected T getElement(int u, int v)
    {
        Point key = new Point(u, v);
        T rval = elements.get(key);
        if (rval==null) {
            rval = createElement(u, v);
            elements.put(key, rval);
        }
        return rval;
    }

    protected abstract T createElement(int u, int v);

    public static class SingleGaussMap
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

    public static class D2
        extends DistortedCellularGauss<GaussLumps.HeightMap>
    {
        /**
         * Generated nodes get a random position in the box bounded by u,v ? clusterDiameter/2
         */
        protected final double clusterDiameter;

        public D2(int clusterDiameter, double minSigma, double maxSigma)
        {
            super(minSigma, maxSigma);
            this.clusterDiameter = clusterDiameter;
        }

        protected GaussLumps.HeightMap createElement(int u, int v)
        {
            GaussLumps.HeightMap rval;
            double cx = u + clusterDiameter * (rand.nextDouble() - 0.5);
            double cz = v + clusterDiameter * (rand.nextDouble() - 0.5);
            double sigma = rand.nextDouble() * sigmaSpan + minSigma;
            rval = new SingleGaussMap(cx, cz, sigma);
            return rval;
        }
    }

    public static class D3
        extends DistortedCellularGauss<GaussNode>
        implements F3
    {
        double dx, dy, dz;
        int[] uaxis;
        int[] vaxis;
        protected double diameter;

        /**
         *
         * @param minSigma
         * @param maxSigma
         * @param dx
         * @param dy
         * @param dz
         * @param uaxis This should be something like { 1,0,0}
         * @param vaxis This should be something like {0,1,0} or maybe {0,0,1}
         */
        public D3(double minSigma, double maxSigma,
                  double dx, double dy, double dz,
                  int[] uaxis, int[] vaxis)
        {
            super(minSigma, maxSigma);

            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.uaxis = uaxis;
            this.vaxis = vaxis;

            diameter = Math.max(Math.max(dx, dy), dz) + 3*(maxSigma);
        }

        @Override
        protected GaussNode createElement(int u, int v)
        {
            double x = (rand.nextDouble()-0.5)*dx + u*uaxis[0] + v*vaxis[0];
            double y = (rand.nextDouble()-0.5)*dy + u*uaxis[1] + v*vaxis[1];
            double z = (rand.nextDouble()-0.5)*dz + u*uaxis[2] + v*vaxis[2];

            double sigma = rand.nextDouble() * sigmaSpan + minSigma;

            return new GaussNode(x, y, z, sigma);
        }

        @Override
        public double eval(double x, double y, double z)
        {
            int u5 = (int) hippo(x, y, z, uaxis);
            int v5 = (int) hippo(x, y, z, vaxis);

            double u0 = Math.floor(u5 - diameter);
            double u9 = Math.ceil(u5 + diameter);
            double v0 = Math.floor(v5 - diameter);
            double v9 = Math.ceil(v5 + diameter);

            double rval=0;

            for (int u=(int) u0; u<= u9; u++) {
                for (int v=(int) v0; v<= v9; v++) {
                    rval += getElement(u,v).eval(x,y,z);
                }
            }
            return rval;
        }

        private double hippo(double x, double y, double z, int[] axis)
        {
            return (axis[0]==0 ? 0 : x)
                +(axis[1]==0 ? 0 : y)
                +(axis[2]==0 ? 0 : z);
        }
    }
}
