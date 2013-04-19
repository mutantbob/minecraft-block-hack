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
        int x = mod_(x_, width);
        int y = mod_(y_, elevation);
        int z = mod_(z_, depth);
        return blocks[ (y*depth +z)*width +x];
    }

    public static int mod_(int a, int b)
    {
        int rval = a % b;
        if (rval<0)
            return b+rval;
        else
            return rval;
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
        int u = mod_(u_, width);
        int v = mod_(v_, depth);
        for (int y=0; y<elevation; y++) {
            editor.setBlock(x,y+y0,z, getBlock(u, y, v));
        }
    }

    public ColumnRef referenceColumn(int x, int z)
    {
        return new ColumnRef(this, x,z);
    }

    public BlockTemplate rot90()
    {
        BlockPlusData[] newTemplate = new BlockPlusData[blocks.length];

        int width_ = depth;
        int depth_ = width;

        for (int x=0; x<width; x++) {
            for (int y=0; y<elevation; y++) {
                for (int z=0; z<depth; z++) {
                    int x_ = depth-1-z;
                    int z_ = x;
                    newTemplate[ (y*depth_ +z_)*width_ +x_] = rot90(blocks[ (y*depth +z)*width +x]);
                }
            }
        }

        return new BlockTemplate(depth, width, elevation, newTemplate);
    }


    public BlockTemplate rot180()
    {
        BlockPlusData[] newTemplate = new BlockPlusData[blocks.length];

        for (int x=0; x<width; x++) {
            for (int y=0; y<elevation; y++) {
                for (int z=0; z<depth; z++) {
                    int x_ = width-1-x;
                    int z_ = depth-1-z;
                    newTemplate[ (y*depth +z_)*width +x_] = rot180(blocks[(y * depth + z) * width + x]);
                }
            }
        }

        return new BlockTemplate(width,depth, elevation,newTemplate);
    }

    public BlockTemplate rot270()
        {
            BlockPlusData[] newTemplate = new BlockPlusData[blocks.length];

            int width_ = depth;
            int depth_ = width;

            for (int x=0; x<width; x++) {
                for (int y=0; y<elevation; y++) {
                    for (int z=0; z<depth; z++) {
                        int x_ = z;
                        int z_ = width-1-x;
                        newTemplate[ (y*depth_ +z_)*width_ +x_] = rot270(blocks[(y * depth + z) * width + x]);
                    }
                }
            }

            return new BlockTemplate(depth, width, elevation, newTemplate);
        }

    public static boolean isStair(int bt)
    {
        return 108==bt ||
            109==bt;
    }

    public static int stairDataRot90(int data)
    {
        // yeah, that's baroque as hell
        return data ^ (2 | (0==(data&2) ? 0:1));
    }

    public static int stairDataRot180(int data)
    {
        return data^1;
    }

    public static int stairDataRot270(int data)
    {
        // yeah, that's baroque as hell
        return data ^ (2 | (0==(data&2) ? 1:0));
    }

    public static BlockPlusData rot90(BlockPlusData block)
    {
        if (isStair(block.blockType)) {
            return new BlockPlusData(block.blockType, stairDataRot90(block.data));
        }
        return block;
    }

    public static BlockPlusData rot180(BlockPlusData block)
    {
        if (isStair(block.blockType)) {
            return new BlockPlusData(block.blockType, stairDataRot180(block.data));
        }
        return block;
    }

    public static BlockPlusData rot270(BlockPlusData block)
    {
        if (isStair(block.blockType)) {
            return new BlockPlusData(block.blockType, stairDataRot270(block.data));
        }
        return block;
    }


    @Override
    public boolean equals(Object obj)
    {

        if (obj instanceof BlockTemplate) {
            BlockTemplate arg = (BlockTemplate) obj;
            return equals_(this, arg);
        } else {
            return false;
        }
    }

    public static boolean equals_(BlockTemplate a, BlockTemplate b)
    {
        if ( a.width == b.width
            && a.depth == b.depth
            && a.elevation == b.elevation) {

            for (int i=0; i<a.blocks.length; i++) {
                if (! a.blocks[i].equals(b.blocks[i]))
                    return false;
            }

            return true;

        } else {
            return false;
        }
    }
}
