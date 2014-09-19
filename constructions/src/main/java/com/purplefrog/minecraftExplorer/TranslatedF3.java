package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/31/13
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class TranslatedF3
    implements F3
{
    public final F3 base;
    public final double x0, y0, z0;

    public TranslatedF3(F3 base, double x0, double y0, double z0)
    {
        this.base = base;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
    }

    @Override
    public double eval(double x, double y, double z)
    {
        return base.eval(x-x0, y-y0, z-z0);
    }
}
