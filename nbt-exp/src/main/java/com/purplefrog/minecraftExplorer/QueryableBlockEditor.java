package com.purplefrog.minecraftExplorer;

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
    public abstract RAMBlockEditor.Combo getOrCreateSection(Point3Di cooked);

    public abstract Set<Map.Entry<Point3Di, RAMBlockEditor.Combo>> getCachedSections();

    public abstract Iterable<Map.Entry<Point3Di, BlockPlusData>> allVoxels();

    public int getBlockType(Point3Di key)
    {
        return getBlockType(key.x, key.y, key.z);
    }

}
