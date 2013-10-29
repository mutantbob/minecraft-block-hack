package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.jwavefrontobj.*;
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

    public GaussNode(Point3D center, double sigma)
    {
        this(center.x, center.y, center.z, sigma);
    }

    @Override
    public double eval(double x, double y, double z)
    {
        return GaussLumps.gauss(Math2.L22(x - x0, y - y0, z - z0), sigma);
    }

}
