package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/15/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntSquare
{
    public int[] values;

    public IntSquare(int[] values)
    {
        if (values.length!= 16*16)
            throw new IllegalArgumentException("the " + IntSquare.class.getSimpleName()+" values must be an int[16*16]");

        this.values = values;
    }

    /**
     * heightMap[x,z] = y
     */
    public void set(int x, int y, int z)
    {
        values[z*16 + x] = y;
    }
}
