package com.purplefrog.minecraftExplorer;

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
}
