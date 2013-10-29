package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/24/13
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class GTPattern
    implements GeometryTree
{

    private final int x0;
    private final int y0;
    private final int z0;
    private final int dx;
    private final int dy;
    private final int dz;
    /**
     * the indices are (lsb first) x,z,y, yeah, that's screwy, but it is how minecraft does things.
     */
    public final BlockPlusData[] blockTypes;

    public GTPattern(int x0, int y0, int z0, int dx, int dy, int dz, int... blockTypes)
    {
        this(x0, y0, z0, dx, dy, dz, BlockPlusData.convert(blockTypes));
    }

    public GTPattern(int x0, int y0, int z0, int dx, int dy, int dz, BlockPlusData... blockTypes)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.dx = dx;
        this.dy = dy;

        this.dz = dz;
        this.blockTypes = blockTypes;

        if (blockTypes.length != dx*dy*dz)
            new IllegalArgumentException("blocktypes.length " +blockTypes.length+
                " != "+dx+"*"+dy+"*"+dz).printStackTrace();
    }

    @Override
    public BlockPlusData pickFor(int x_, int y_, int z_)
    {
        int x = mod((x_ - x0), dx);
        int y = mod((y_-y0), dy);
        int z = mod((z_-z0), dz);

        int i = (y * dz + z) * dx + x;
        if (i<blockTypes.length)
            return blockTypes[i];
        else
            return null;
    }

    private int mod(int num, int den)
    {
        int rval = num % den;
        return rval<0 ? rval +den : rval;
    }
}
