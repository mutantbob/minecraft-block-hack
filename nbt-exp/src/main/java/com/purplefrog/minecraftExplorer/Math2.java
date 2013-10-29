package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/23/13
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Math2
{
    public static double L2(double dx, double dy)
    {
        return Math.sqrt(L22(dx, dy));
    }

    public static double L2(double dx, double dy, double dz)
    {
        return Math.sqrt(L22(dx, dy, dz));
    }

    public static double L22(double dx, double dy)
    {
        return dx * dx + dy * dy;
    }

    public static double L22(double dx, double dy, double dz)
    {
        return dx*dx + dy*dy + dz*dz;
    }

    public static Point3D scaledSum(Point3D A, double aF, Point3D B, double bF)
    {
        return new Point3D(aF *A.x+B.x* bF,
            aF *A.y+B.y* bF,
            aF *A.z+B.z* bF);
    }

    public static Point3D mix(Point3D p1, Point3D p2, double t)
    {
        return scaledSum(p1, 1- t, p2, t);
    }
}
