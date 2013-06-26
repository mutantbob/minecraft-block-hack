package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/6/13
 * Time: 7:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Point3Di
{
    public int x;
    public int y;
    public int z;

    public Point3Di(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString()
    {
        return "<"+x+","+y+","+z+">";
    }

    @Override
    public int hashCode()
    {
        return new Double(x*771+y*31+z).hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Point3Di) {
            Point3Di arg = (Point3Di) obj;
            return arg.x==x
                && arg.y==y
                && arg.z==z;
        } else {
            return false;
        }
    }

    public Point3D asD()
    {
        return new Point3D(x,y,z);
    }
}
