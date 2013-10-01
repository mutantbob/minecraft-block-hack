package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/2/13
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class GTRectangle
    implements GeometryTree
{
    public final Point3Di min;
    public final Point3Di max;
    public final GeometryTree inside;
    public final GeometryTree outside;

    /**
     * the inside ranges from min..max, INCLUSIVE
     * @param min
     * @param max
     * @param inside
     * @param outside
     */
    public GTRectangle(Point3Di min, Point3Di max, GeometryTree inside, GeometryTree outside)
    {
        this.min = min;
        this.max = max;
        this.inside = inside;
        this.outside = outside;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        GeometryTree gt = which(x, y, z);
        return gt==null ? null : gt.pickFor(x, y, z);
    }

    GeometryTree which(int x,int y,int z)
    {
        if (x<min.x || x>max.x
            || y<min.y || y>max.y
            || z<min.z || z>max.z)
            return outside;
        else
            return inside;
    }
}
