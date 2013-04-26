package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/19/13
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GeometryTree
{

    public BlockPlusData pickFor(int x, int y, int z);


    public class Solid
        implements GeometryTree
    {
        public final BlockPlusData blockType;

        public Solid(int blockType)
        {
            if (blockType < 0) {
                this.blockType = null;
            } else {
                this.blockType = new BlockPlusData(blockType);
            }
        }

        public Solid(BlockPlusData bt)
        {
            this.blockType = bt;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            return blockType;
        }
    }
}
