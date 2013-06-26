package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/3/13
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class SectionVoxelIterator
    implements Iterator<Map.Entry<Point3Di, BlockPlusData>>
{
    private final int baseX, baseY, baseZ;
    private final ByteCube blockTypes;
    private final ByteCube data;

    int pos=0;

    /**
     * @param base  This is section coordinates and must be &lt;&tl;4 to return it to proper world coordinates.
     * @param blockTypes
     * @param data
     */
    public SectionVoxelIterator(Point3Di base, ByteCube blockTypes, ByteCube data)
    {
        baseX = base.x << 4;
        baseY = (int)base.y<< 4;
        baseZ = (int)base.z << 4;
        this.blockTypes = blockTypes;
        this.data = data;
    }

    @Override
    public boolean hasNext()
    {
        return pos < (1<<12);
    }

    @Override
    public Map.Entry<Point3Di, BlockPlusData> next()
    {
        if (!hasNext())
            throw new NoSuchElementException();

        int x_ = pos &0xf;
        int p = pos>>4;
        int z_ = p & 0xf;
        int y_ = p>>4;

        Point3Di loc = new Point3Di(x_+baseX, y_+baseY, z_+baseZ);

        AbstractMap.SimpleEntry<Point3Di, BlockPlusData> rval
            = new AbstractMap.SimpleEntry<Point3Di, BlockPlusData>(loc, new BlockPlusData(blockTypes.get_(pos), data.get_(pos)));

        pos++;

        return rval;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
