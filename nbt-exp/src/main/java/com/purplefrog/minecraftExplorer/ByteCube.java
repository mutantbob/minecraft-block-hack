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

    public ByteCube()
    {
        data = new byte[16*16*16];
    }

    public int get_(int pos)
    {
        return 0xff & data[pos];
    }

    public int get(int x, int y, int z)
    {
        return get_(BlockVoxels.encodePos(x,y,z));
    }

    public void set(int x, int y, int z, int bt)
    {
        set_(BlockVoxels.encodePos(x,y,z), bt);
    }

    private void set_(int pos, int bt)
    {
        data[pos] = (byte) bt;
    }
}
