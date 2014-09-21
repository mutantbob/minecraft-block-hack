package com.purplefrog.minecraftExplorer;

/**
* Created by thoth on 9/21/14.
*/
public class XYZUV
{
    public final double x,y,z;
    public final double u,v;

    public XYZUV(double x, double y, double z, double u, double v)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof XYZUV) {
            XYZUV arg = (XYZUV) obj;
            return x==arg.x
                && y==arg.y
                && z==arg.z
                && u==arg.u
                && v==arg.v;
        } else {
            return false;
        }
    }
}
