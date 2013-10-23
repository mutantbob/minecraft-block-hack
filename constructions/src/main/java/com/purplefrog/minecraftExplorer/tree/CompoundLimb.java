package com.purplefrog.minecraftExplorer.tree;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.landscape.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/22/13
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompoundLimb
    implements F3
{

    private final LineMath[] segments;
    private final double r5;

    public Point3D origin, axis;
    public double r1, r9;
    public double a1, a9;
    public double inflation;
    public double sigma = 0.8;
    protected final double giraffe;

    public CompoundLimb(Point3D origin, Point3D axis, double r1, double r9, double a1, double a9, double inflation, Random rand)
    {
        this.origin = origin;
        this.axis = axis;
        this.r1 = r1;
        this.r9 = r9;
        this.a1 = a1;
        this.a9 = a9;
        this.inflation = inflation;

        double axisL = Math2.L2(axis.x, axis.y, axis.z);

        giraffe = 0.6;

        this.r5 = mix(r1, r9, giraffe);
        Point3D p1 = scaledSum(origin, 1.0, axis, a1 /axisL);
        Point3D midpoint = scaledSum(origin, 1.0, axis, mix(a1, a9, giraffe) /axisL);
        Point3D p3 = scaledSum(origin, 1.0, axis, a9/axisL);

        double d = 0.1* axisL;

        Point3D peturbed =
            new Point3D(midpoint.x+rand.nextDouble()*d,
            midpoint.y+rand.nextDouble()*d,
            midpoint.z+rand.nextDouble()*d);

        Joint[] joints = new Joint[3];

        joints[0] = new Joint(p1, r1);
        joints[1] = new Joint(peturbed, r5);
        joints[2] = new Joint(p3, r9);

        segments = new LineMath[2];
        segments[0] = LineMath.fromEndpoints(p1, peturbed);
        segments[1] = LineMath.fromEndpoints(peturbed, p3);
    }

    public double mix(double zero, double one, double t)
    {
        return ((1- t)*zero+ t *one);
    }

    public static Point3D scaledSum(Point3D A, double aF, Point3D B, double bF)
    {
        return new Point3D(aF *A.x+B.x* bF,
            aF *A.y+B.y* bF,
            aF *A.z+B.z* bF);
    }

    public static double partial(double x,double y,double z, LineMath segment, double r1, double r9)
    {
        LineMath.Squares sq = segment.getSquares(x, y, z);

        if (sq.a < 0 || sq.a > segment.axisL2)
            return 999;


        double r = BLI(0, sq.a, segment.axisL2, r1, r9);

        return sq.b2 / (r * r);
    }

    @Override
    public double eval(double x, double y, double z)
    {
        double xsq0 = partial(x,y,z, segments[0], r1, r5);
        double xsq1 = partial(x,y,z, segments[1], r5, r9);

        return inflation * GaussLumps.gauss(Math.min(xsq0,xsq1), sigma);
    }

    public static double BLI(double a1, double a, double a9, double r1, double r9)
    {
        return SimpleLimb.BLI(a1, a, a9, r1, r9);
    }


    public static class Joint
    {
        public Point3D p;
        public double r;

        public Joint(Point3D p, double r)
        {
            this.p = p;
            this.r = r;
        }
    }

}
