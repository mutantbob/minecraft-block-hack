package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.minecraftExplorer.*;

/**
* Created with IntelliJ IDEA.
* User: thoth
* Date: 10/21/13
* Time: 3:13 PM
* To change this template use File | Settings | File Templates.
*/
public class GaussNode
    implements F3
{
    public double x0,y0,z0;
    public double sigma;

    public GaussNode(double x0, double y0, double z0, double sigma)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.sigma = sigma;
    }

    @Override
    public double eval(double x, double y, double z)
    {
        return GaussLumps.gauss(L22(x-x0, y-y0, z-z0), sigma);
    }

    private double L22(double x, double y, double z)
    {

        return x*x + y*y + z*z;
    }
}
