package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/26/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public enum FaceSide
{
    TOP,
    NORTH,
    SOUTH,
    EAST,
    WEST,
    BOTTOM;

    public String capitalized()
    {
        String s = name().toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
