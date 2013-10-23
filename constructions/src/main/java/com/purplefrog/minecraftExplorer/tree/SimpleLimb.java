package com.purplefrog.minecraftExplorer.tree;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.landscape.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/21/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleLimb
    implements F3
{
    protected final LineMath segment;

    public Point3D origin, axis;
    public double r1, r9;
    public double a1, a9;
    public double inflation;
    public double sigma = 0.8;

    public SimpleLimb(Point3D origin, Point3D axis, double r1, double r9, double a1, double a9, double inflation)
    {
        this.origin = origin;
        this.axis = axis;
        this.r1 = r1;
        this.r9 = r9;
        this.a1 = a1;
        this.a9 = a9;
        this.inflation = inflation;

        segment = new LineMath(origin, axis);
    }

    @Override
    public double eval(double x, double y, double z)
    {
        LineMath.Squares sq = segment.getSquares(x, y, z);

        if (sq.a < a1 || sq.a > a9)
            return 0;


        double r = BLI(a1, sq.a, a9, r1, r9);

        return inflation * GaussLumps.gauss(sq.b2/(r*r), sigma);
    }

    public static double BLI(double a1, double a, double a9, double r1, double r9)
    {
        return r1 + (a-a1)*(r9-r1)/(a9-a1);
    }
}
