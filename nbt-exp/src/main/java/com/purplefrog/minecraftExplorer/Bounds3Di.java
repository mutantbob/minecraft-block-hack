package com.purplefrog.minecraftExplorer;

/**
* Created with IntelliJ IDEA.
* User: thoth
* Date: 10/15/13
* Time: 11:07 AM
* To change this template use File | Settings | File Templates.
*/
public class Bounds3Di
{
    public int x0, y0, z0;
    public int x1, y1, z1;

    public Bounds3Di(int x0, int y0, int z0, int x1, int y1, int z1)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }

    public Bounds3Di(Point3Di p0, Point3Di p1)
    {
        x0 = p0.x;
        y0 = p0.y;
        z0 = p0.z;
        x1 = p1.x;
        y1 = p1.y;
        z1 = p1.z;
    }

    public int xSize()
    {
        return x1-x0;
    }

    public int ySize()
    {
        return y1-y0;
    }

    public int zSize()
    {
        return z1-z0;
    }

    public boolean contains(int x, int y, int z)
    {
        return x>=x0 && x<x1
            && y>=y0 && y<y1
            && z>=z0 && z<z1;
    }

    public Bounds3Di outset(int dx, int dy, int dz)
    {
        return new Bounds3Di(x0-dx, y0-dy, z0-dz, x1+dx, y1+dy, z1+dz);
    }

    public int volume()
    {
        return xSize() * ySize() * zSize();
    }

    public Bounds3Di max(Bounds3Di arg)
    {
        return new Bounds3Di(Math.min(x0, arg.x0),
            Math.min(y0, arg.y0),
            Math.min(z0, arg.z0),
            Math.max(x1, arg.x1),
            Math.max(y1, arg.y1),
            Math.max(z1, arg.z1));
    }
}
