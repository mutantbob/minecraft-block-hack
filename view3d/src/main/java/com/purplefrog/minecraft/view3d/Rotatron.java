package com.purplefrog.minecraft.view3d;

/**
 *  This class provides a time-based rotation value in degrees.
 */
public class Rotatron
{
    private double phase;
    public long tmBase;
    public double periodSeconds;

    public Rotatron(double periodSeconds)
    {
        this.periodSeconds = periodSeconds;
        tmBase =  System.currentTimeMillis();
        phase = 0.0;
    }

    public double getDegrees()
    {
        double r2 = adjustedR2();

        return r2*360;
    }

    public double getRadians()
    {
        double r2 = adjustedR2();

        return r2*Math.PI*2;
    }

    public double adjustedR2()
    {
        long now = System.currentTimeMillis();
        double r2 = rawR2(now) + phase;

        if (r2 > 1) {
            r2 -= 1;
            tmBase = (long) (now - r2*periodSeconds*1000);
            phase = r2 - rawR2(now);
        }
        return r2;
    }

    public double rawR2(long now)
    {
        return (now - tmBase) / 1000.0 / periodSeconds;
    }
}
