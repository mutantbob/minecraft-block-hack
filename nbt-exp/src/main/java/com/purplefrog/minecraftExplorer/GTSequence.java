package com.purplefrog.minecraftExplorer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/2/13
 * Time: 8:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class GTSequence
    implements GeometryTree
{
    public final List<GeometryTree> trees;

    public GTSequence(GeometryTree... trees)
    {
        this(Arrays.asList(trees));
    }

    public GTSequence(List<GeometryTree> trees)
    {
        this.trees = trees;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        for (GeometryTree tree : trees) {
            BlockPlusData rval = tree.pickFor(x, y, z);
            if (null != rval)
                return rval;
        }

        return null;
    }

    public static class WithBounds
        extends GTSequence
        implements GeometryTree.WithBounds
    {

        public Bounds3Di bounds;

        public WithBounds(Bounds3Di bounds, GeometryTree... trees)
        {
            super(trees);
            this.bounds = bounds;
        }

        public WithBounds(Bounds3Di bounds, List<GeometryTree> trees)
        {
            super(trees);
            this.bounds = bounds;
        }

        @Override
        public Bounds3Di getBounds()
        {
            return bounds;
        }
    }
}
