package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/24/13
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class GTCone
    implements GeometryTree
{

    LineMath core;
    public GeometryTree inner, outer;
    double slope;

    public GTCone(LineMath core, double slope, GeometryTree inner, GeometryTree outer)
    {
        this.core = core;
        this.inner = inner;
        this.outer = outer;
        this.slope = slope;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        LineMath.Squares sq = core.getSquares(x, y, z);

        GeometryTree delegate;
        if (sq.a2 > sq.b2*slope*slope)
            delegate = inner;
        else
            delegate = outer;

        return delegate.pickFor(x, y, z);
    }
}
