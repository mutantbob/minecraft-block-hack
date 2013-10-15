package com.purplefrog.minecraftExplorer.spleef;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/2/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpleefSiloWall
    implements GeometryTree
{
    private final GeometryTree rule;

    public SpleefSiloWall(Point3Di origin, int y0, int y1, double innerRadius, double outerRadius, BlockPlusData wall)
    {

        Point3D axis = new Point3D(0, 1, 0);
        GTCylinder inside = new GTCylinder(origin.asD(), axis, innerRadius, new Solid(0), new Solid(wall));
        GTCylinder outer = new GTCylinder(origin.asD(), axis, outerRadius, inside, null);

        int or = (int) Math.ceil(outerRadius);

        rule = new GTRectangle(new Point3Di(origin.x- or, y0, origin.z - or),
            new Point3Di(origin.x+ or+1, y1, origin.z + or + 1),
            outer, null);
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        return rule.pickFor(x, y, z);
    }
}
