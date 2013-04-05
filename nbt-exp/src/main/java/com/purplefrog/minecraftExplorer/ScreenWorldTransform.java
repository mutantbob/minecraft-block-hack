package com.purplefrog.minecraftExplorer;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/3/13
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScreenWorldTransform
{
    private final Dimension screenSize;
    private final double wcx;
    private final double wcz;
    private double magnification;

    /**
     * An overhead map of the minecraft world includes the X and Z axes.  The Y axis of the minecraft world is height which isn't relevant for overhead maps.
     */
    public ScreenWorldTransform(Dimension screenSize, double wcx, double wcy, double magnification)
    {

        this.screenSize = screenSize;
        this.wcx = wcx;
        this.wcz = wcy;
        this.magnification = magnification;
    }

    public double screenXToWorld(int screenX)
    {
        return wcx +( screenX - screenSize.getWidth() / 2.0 )/ magnification;
    }

    public double screenYToWorld(int screenY)
    {
        return wcz +( screenY - screenSize.getHeight() / 2.0 )/ magnification;
    }

    public double worldXToScreen(int worldX)
    {
        return screenSize.getWidth() / 2 + (worldX - wcx) * magnification;
    }
    public double worldZToScreen(int worldZ)
    {
        return screenSize.getHeight() / 2 + (worldZ - wcz) * magnification;
    }
}
