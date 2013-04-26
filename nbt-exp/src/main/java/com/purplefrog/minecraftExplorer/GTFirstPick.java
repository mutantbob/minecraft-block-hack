package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/19/13
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class GTFirstPick
    implements GeometryTree
{
    private GeometryTree[] constructs;

    public GTFirstPick(GeometryTree... constructs)
    {
        this.constructs = constructs;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        for (GeometryTree construct : constructs) {
            BlockPlusData rval = construct.pickFor(x, y, z);
            if (rval != null)
                return rval;
        }

        return null;
    }
}
