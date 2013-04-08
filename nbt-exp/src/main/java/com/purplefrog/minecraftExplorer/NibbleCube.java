package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/5/13
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class NibbleCube
{
    private byte[] data;

    public NibbleCube(byte[] data)
    {

        this.data = data;
    }

    public static int nibbleClamp(int val)
    {
        if (val<0)
            return 0;
        if (val>15)
            return 15;
        return val;
    }

    public int get(int x,int y, int z)
    {
        int pos = BlockVoxels.encodePos(x,y,z);

        return get_(pos);
    }

    public int get_(int pos)
    {
        int idx = pos>>1;
        if (0 == (pos&1)) {
            return data[idx] & 0xf;
        } else {
            return 0xf & (data[idx] >>4);
        }
    }

    public void set(int x,int y,int z, int val)
    {
        int pos = BlockVoxels.encodePos(x,y,z);

        set_(pos, val);
    }

    public void set_(int pos, int val)
    {
        int idx = pos>>1;
        if (0 == (pos&1)) {
            data[idx] = (byte) ((data[idx] & 0xf0) | (val&0xf));
        } else {
            data[idx] = (byte) ( (val<<4) | (data[idx] & 0xf) );
        }
    }

    public void zero()
    {
        for (int i = 0; i < data.length; i++) {
            data[i]=0;
        }
    }
}
