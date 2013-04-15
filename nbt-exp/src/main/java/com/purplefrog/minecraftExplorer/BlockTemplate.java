package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/12/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockTemplate
{

    public int width, depth, elevation;

    public BlockPlusData[] blocks;

    public BlockTemplate(int width, int depth, int elevation, BlockPlusData... blocks)
    {
        this.width = width;
        this.depth = depth;
        this.elevation = elevation;

        if (blocks.length != width*depth*elevation)
            throw new IllegalArgumentException(blocks.length+" != "+width+"*"+depth+"*"+elevation);

        this.blocks = blocks;
    }

    public BlockTemplate(int width, int depth, int elevation, int... blocks)
    {
        this.width = width;
        this.depth = depth;
        this.elevation = elevation;

        if (blocks.length != width*depth*elevation)
            throw new IllegalArgumentException(blocks.length+" != "+width+"*"+depth+"*"+elevation);

        this.blocks = new BlockPlusData[blocks.length];

        for (int i = 0; i < blocks.length; i++) {
            this.blocks[i] = new BlockPlusData(blocks[i]);
        }
    }

    public BlockTemplate(int blockType)
    {
        this(new BlockPlusData(blockType));
    }

    public BlockTemplate(BlockPlusData pavement)
    {
        this(1,1,1, pavement);
    }

    public BlockPlusData getBlock(int x_, int y_, int z_)
    {
        int x = x_%width;
        int y = y_%elevation;
        int z = z_%depth;
        return blocks[ (y*depth +z)*width +x];
    }

    public BlockTemplate transpose()
    {
        BlockPlusData[] transposed = new BlockPlusData[blocks.length];

        for (int x=0; x<width; x++) {
            for (int y=0; y<elevation; y++) {
                for (int z=0; z<depth; z++) {
                    transposed[ (y*width +x)*depth +z] = blocks[ (y*depth +z)*width +x];
                }
            }
        }
        return new BlockTemplate(depth, width, elevation, transposed);
    }

    public void renderColumn(BlockEditor editor, int x,int y0,int z, int u_, int v_)
        throws IOException
    {
        int u = u_ % width;
        int v = v_ % depth;
        for (int y=0; y<elevation; y++) {
            editor.setBlock(x,y+y0,z, getBlock(u, y, v));
        }
    }

    public ColumnRef referenceColumn(int x, int z)
    {
        return new ColumnRef(this, x,z);
    }
}
