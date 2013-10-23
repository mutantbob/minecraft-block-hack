package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/19/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class GTEllipse
    implements GeometryTree
{
    public final double x;
    public final double y;
    public final double z;
    public final double radius;
    public GeometryTree inside;
    public GeometryTree outside;

    public GTEllipse(double x, double y, double z, double radius, int btInside, int btOutside)
    {
        this(x,y,z,radius, new GeometryTree.Solid(btInside), new GeometryTree.Solid(btOutside));
    }

    public GTEllipse(double x, double y, double z, double radius, GeometryTree inside, GeometryTree outside)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.inside = inside;
        this.outside = outside;
    }

    public GTEllipse(Point3D c, double radius, int btInside, int btOutside)
    {
        this(c.x, c.y, c.z, radius, btInside, btOutside);
    }

    public GTEllipse(Point3D c, double radius, GeometryTree inside, GeometryTree outside)
    {
        this(c.x, c.y, c.z, radius, inside, outside);
    }

    public GTEllipse(Point3D c, double radius, int btInside)
    {
        this(c.x, c.y, c.z, radius, btInside, -1);
    }

    public GTEllipse(Point3D c, double radius, GeometryTree inside)
    {
        this(c.x, c.y, c.z, radius, inside, new Solid(null));
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        double d2 = Math2.L22(x - this.x, y - this.y, z - this.z);


        GeometryTree delegate;
        if (d2<= radius*radius) {
            delegate = inside;
        } else {
            delegate = outside;
        }
        return delegate==null ? null : delegate .pickFor(x, y, z);
    }

}
