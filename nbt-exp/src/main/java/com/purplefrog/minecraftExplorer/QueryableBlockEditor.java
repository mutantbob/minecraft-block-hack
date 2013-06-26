package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/3/13
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class QueryableBlockEditor
    extends BasicBlockEditor
{
    public abstract BlenderBlockEditor.Combo getOrCreateSection(Point3Di cooked);

    public abstract Set<Map.Entry<Point3Di, BlenderBlockEditor.Combo>> getCachedSections();

    public abstract int getBlockType(int x, int y, int z);

    public abstract BlockPlusData getBlockData(int x, int y, int z);

    public abstract Iterable<Map.Entry<Point3Di, BlockPlusData>> allVoxels();

    public int getBlockType(Point3Di key)
    {
        return getBlockType(key.x, key.y, key.z);
    }

}
