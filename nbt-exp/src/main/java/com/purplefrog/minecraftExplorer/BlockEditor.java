package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/19/13
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockEditor
{
    void setBlock(int x, int y, int z, int bt)
        throws IOException;

    void setBlock(int x, int y, int z, BlockPlusData block)
            throws IOException;

    void save()
                throws IOException;

    void relight();
}
