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
    private final Joint[] joints;

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
        Point3D p1 = Math2.scaledSum(origin, 1.0, axis, a1 / axisL);
        Point3D midpoint = Math2.scaledSum(origin, 1.0, axis, mix(a1, a9, giraffe) / axisL);
        Point3D p3 = Math2.scaledSum(origin, 1.0, axis, a9 / axisL);

        double d = 0.1* axisL;

        Point3D peturbed =
            new Point3D(midpoint.x+rand.nextDouble()*d,
            midpoint.y+rand.nextDouble()*d,
            midpoint.z+rand.nextDouble()*d);

        joints = new Joint[3];

        joints[0] = new Joint(p1, r1);
        joints[1] = new Joint(peturbed, r5);
        joints[2] = new Joint(p3, r9);

        segments = new LineMath[joints.length-1];
        for (int i=0; i<segments.length; i++) {
            segments[i] = LineMath.fromEndpoints(joints[i].p, joints[i+1].p);
        }
    }


    public List<F3> lumpCloud(double radius, Random rand)
    {
        List<F3> nodes = new ArrayList<F3>();
        for (int j = 0; j < segments.length; j++) {
            LineMath segment = segments[j];

            for (int i = 0; i < segment.axisL2; i++) {
                Point3D p1 = segment.interpolate(rand.nextDouble());
                Point3D p2 = new Point3D(p1.x + randPlusMinus(rand, joints[j].r),
                    p1.y + randPlusMinus(rand, joints[j].r),
                    p1.z + randPlusMinus(rand, joints[j].r));
                nodes.add(new GaussNode(p2, 1));
            }

        }
        return nodes;
    }

    private double randPlusMinus(Random rand, double magnitude)
    {
        return (rand.nextDouble() - 0.5) * 2 * magnitude;
    }

    public double mix(double zero, double one, double t)
    {
        return ((1- t)*zero+ t *one);
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
        double minXsq = 999;
        for (int i=0; i<joints.length; i++) {

            Joint j = joints[i];
            {
                double xsq = Math2.L22(x- j.p.x, y - j.p.y, z - j.p.z) / (j.r*j.r);
                if (xsq < minXsq)
                    minXsq = xsq;
            }

            if (i+1<joints.length) {
                double xsq = partial(x,y,z, segments[i], j.r, joints[i+1].r);
                if (xsq<minXsq)
                    minXsq  = xsq;
            }
        }

        return inflation * GaussLumps.gauss(minXsq, sigma);
    }

    public static double BLI(double a1, double a, double a9, double r1, double r9)
    {
        return SimpleLimb.BLI(a1, a, a9, r1, r9);
    }

    public Point3D interpolatePoint(double a)
    {
        double a_ = (a-a1)/(a9-a1);

        if (a_<giraffe) {
            return Math2.mix(joints[0].p, joints[1].p, a_ / giraffe);
        } else {
            double b = a_-giraffe;
            double g2 = 1-giraffe;
            return Math2.mix(joints[1].p, joints[2].p, b / g2);
        }
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
