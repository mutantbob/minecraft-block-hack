package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/21/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineMath
{
    protected final Point3D origin;
    protected final Point3D axis;
    protected final double axisL22;
    public final double axisL2;

    public LineMath(Point3D origin, Point3D axis)
    {
        axisL22 = Math2.L22(axis.x, axis.y, axis.z);
        axisL2 = Math.sqrt(axisL22);
        this.origin = origin;
        this.axis = axis;
    }

    public static double l2(double x, double y, double z)
    {
        return Math2.L22(x, y, z);
    }

    public Squares getSquares(double x, double y, double z)
    {
        double x1 = x - origin.x;
        double y1 = y - origin.y;
        double z1 = z - origin.z;
        double dot = x1 *axis.x
            + y1 *axis.y
            + z1 *axis.z;

        double a = dot / axisL2;

        double c2 = l2(x1, y1, z1);
        double a2 = dot*dot/ axisL22;

        double b2 = c2-a2;

        return new Squares(a2, b2, c2, a);
    }

    public static LineMath fromEndpoints(Point3D origin, Point3D endpoint)
    {
        Point3D axis = new Point3D(endpoint.x - origin.x,
            endpoint.y - origin.y,
            endpoint.z - origin.z);
        return new LineMath(origin, axis);
    }

    public Point3D interpolate(double v)
    {
        return new Point3D(origin.x + v*axis.x,
            origin.y + v*axis.y,
            origin.z + v*axis.z);
    }

    /**
     * This stores the squares of the lengths of the sides of a right triangle formed by the origin, axis,
     * and some point of interest.
     */
    public static class Squares
    {
        /**
         *The square of the length of the side of the triangle aligned with the axis.
         */
        public double a2;
        /**
         * The square of the distance from the point of interest to the line segment.
         */
        public double b2;
        /**
         * The square of distance from the point of interest to the origin.
         */
        public double c2;
        /**
         * the length of the side of the triangle aligned with the axis.
         * This can be negative if it's on the other side of the origin point.
         */
        public double a;

        public Squares(double a2, double b2, double c2, double a)
        {
            this.a2 = a2;
            this.b2 = b2;
            this.c2 = c2;
            this.a = a;
        }
    }
}
