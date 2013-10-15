package com.purplefrog.minecraftExplorer;

/**
* Created with IntelliJ IDEA.
* User: thoth
* Date: 10/15/13
* Time: 11:17 AM
* To change this template use File | Settings | File Templates.
*/
public class WindowShape
{
    public final int cellWidth;

    public final BlockPlusData[] blocks;

    public WindowShape(int cellWidth, Object[] blocks)
    {
        this.blocks = new BlockPlusData[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            Object block_ = blocks[i];
            if (block_ instanceof BlockPlusData) {
                this.blocks[i] =  (BlockPlusData) block_;
            } else {
                this.blocks[i] = new BlockPlusData((Integer)block_, 0);
            }
        }
        this.cellWidth = cellWidth;

        if (blocks.length != (cellWidth -1)* height())
            throw new IllegalArgumentException("blocks.length=" +blocks.length+ " is not an even multiple of "+(cellWidth-1));
    }

    public WindowShape(int cellWidth, int... blocks)
    {
        this.cellWidth = cellWidth;

        this.blocks = new BlockPlusData[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            this.blocks[i] = new BlockPlusData(blocks[i], 0);

        }
    }

    public int height()
    {
        return blocks.length / (cellWidth-1);
    }

    public BlockPlusData getBlock(int u, int v)
    {
        return blocks[u + v*(cellWidth-1)];
    }
}
