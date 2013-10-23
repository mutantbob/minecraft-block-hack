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
    extends LineMath
    implements GeometryTree
{
    private final double radius;
    private final GeometryTree inside;
    private final GeometryTree outside;

    public GTCylinder(Point3D origin, Point3D axis, double radius, GeometryTree inside, GeometryTree outside)
    {
        super(origin, axis);

        this.radius = radius;
        this.inside = notNull(inside);
        this.outside = notNull(outside);

    }

    public static GeometryTree notNull(GeometryTree tree)
    {
        return tree ==null ? new Solid(-1) : tree;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        Squares sq = getSquares(x, y, z);

        boolean out = sq.b2 > radius*radius;

        return (out ? outside : inside).pickFor(x, y, z) ;
    }


}
