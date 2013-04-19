package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/16/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RectangleEdges
{
    private final WormWorld.Bounds bounds;

    public RectangleEdges(WormWorld.Bounds b)
    {
        bounds = b;
    }

    public void render(BlockEditor editor, int blockType)
        throws IOException
    {
        for (int x=bounds.x0; x<bounds.x1; x++) {
            editor.setBlock(x, bounds.y0, bounds.z0, blockType);
            editor.setBlock(x, bounds.y1-1, bounds.z0, blockType);
            editor.setBlock(x, bounds.y0, bounds.z1-1, blockType);
            editor.setBlock(x, bounds.y1-1, bounds.z1-1, blockType);
        }

        for (int y=bounds.y0; y<bounds.y1; y++) {
            editor.setBlock(bounds.x0, y, bounds.z0, blockType);
            editor.setBlock(bounds.x1-1, y, bounds.z0, blockType);
            editor.setBlock(bounds.x0, y, bounds.z1-1, blockType);
            editor.setBlock(bounds.x1-1, y, bounds.z1-1, blockType);
        }

        for (int z=bounds.z0; z<bounds.z1; z++) {
            editor.setBlock(bounds.x0, bounds.y0, z, blockType);
            editor.setBlock(bounds.x1-1, bounds.y0, z, blockType);
            editor.setBlock(bounds.x0, bounds.y1-1, z, blockType);
            editor.setBlock(bounds.x1-1, bounds.y1-1, z, blockType);
        }
    }
}
