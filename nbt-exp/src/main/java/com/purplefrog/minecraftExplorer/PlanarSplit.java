package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/19/13
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlanarSplit
    implements GeometryTree
{
    private final double x;
    private final double y;
    private final double z;
    private final double dx;
    private final double dy;
    private final double dz;
    private final GeometryTree upper;
    private final GeometryTree lower;

    public PlanarSplit(double x, double y, double z, double dx, double dy, double dz, GeometryTree upper, int lowerBT)
    {
        this(x,y,z, dx, dy, dz, upper, new Solid(lowerBT));
    }

    public PlanarSplit(double x, double y, double z, double dx, double dy, double dz, GeometryTree upper, GeometryTree lower)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.upper = upper;
        this.lower = lower;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        double dot = (x-this.x) * dx
            +(y-this.y)*dy
            +(z-this.z)*dz;

        return ((dot>=0) ? upper : lower )
            .pickFor(x, y, z);

    }
}
