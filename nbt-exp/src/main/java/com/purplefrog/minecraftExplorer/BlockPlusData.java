package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/11/13
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockPlusData
{
    public final int blockType;
    public final int data;

    public BlockPlusData(int blockType, int data)
    {
        this.blockType = blockType;
        this.data = data;
    }

    public BlockPlusData(int blockType)
    {
        this(blockType,0);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BlockPlusData) {
            BlockPlusData arg = (BlockPlusData) obj;

            return arg.blockType == blockType
                && arg.data == data;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return blockType *71 + data;
    }
}
