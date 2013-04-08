package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/7/13
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class ByteCube
{

    public byte[] data;

    public ByteCube(byte[] data)
    {
        this.data = data;
    }

    public int get_(int pos)
    {
        return 0xff & data[pos];
    }

    public int get(int x, int y, int z)
    {
        return get_(BlockVoxels.encodePos(x,y,z));
    }
}
