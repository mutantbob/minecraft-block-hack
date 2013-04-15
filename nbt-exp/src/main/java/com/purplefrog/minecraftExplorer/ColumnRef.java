package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/12/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColumnRef
{
    public final BlockTemplate blockTemplate;
    public final int x;
    public final int z;

    public ColumnRef(BlockTemplate blockTemplate, int x, int z)
    {

        this.blockTemplate = blockTemplate;
        this.x = x;
        this.z = z;
    }

    public void renderColumn(BlockEditor editor, int x, int y, int z)
        throws IOException
    {
        blockTemplate.renderColumn(editor, x,y,z, this.x, this.z);
    }
}
