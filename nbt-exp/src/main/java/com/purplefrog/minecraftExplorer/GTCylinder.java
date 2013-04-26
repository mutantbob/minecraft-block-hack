package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/19/13
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class GTCylinder
    implements GeometryTree
{
    private final Point3D origin;
    private final Point3D axis;
    private final double radius;
    private final GeometryTree inside;
    private final GeometryTree outside;
    private final double axisL2;

    public GTCylinder(Point3D origin, Point3D axis, double radius, GeometryTree inside, GeometryTree outside)
    {

        this.origin = origin;
        this.axis = axis;
        this.radius = radius;
        this.inside = notNull(inside);
        this.outside = notNull(outside);

        axisL2 = GTEllipse.l2(axis.x, axis.y, axis.z);
    }

    public static GeometryTree notNull(GeometryTree tree)
    {
        return tree ==null ? new Solid(-1) : tree;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        double x1 = x - origin.x;
        double y1 = y - origin.y;
        double z1 = z - origin.z;
        double dot = x1 *axis.x
            + y1 *axis.y
            + z1 *axis.z;

        double c2 = l2(x1, y1, z1);
        double a2 = dot*dot/axisL2;

        double b2 = c2-a2;

        boolean out = b2 > radius*radius;

        return (out ? outside : inside).pickFor(x, y, z) ;
    }

    public static double l2(double x, double y, double z)
    {
        return GTEllipse.l2(x,y,z);
    }
}
