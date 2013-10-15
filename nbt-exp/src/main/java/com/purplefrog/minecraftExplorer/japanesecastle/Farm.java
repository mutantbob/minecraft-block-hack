package com.purplefrog.minecraftExplorer.japanesecastle;

import com.purplefrog.minecraftExplorer.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/1/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Farm
    implements GeometryTree
{
    private final int cx;
    private final int y;
    private final int cz;

    public Farm(int cx, int y, int cz)
    {
        this.cx = cx;
        this.y = y;
        this.cz = cz;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        if (y==this.y) {
            if (x==cx && z==cz)
                return new BlockPlusData(9);

            if (Math.abs(x-cx)<=4 && Math.abs(z-cz)<=4)
                return new BlockPlusData(60);
        }
        return null;
    }
}
