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
    TOP(1,0,0, 0,0,-1),
    NORTH(-1,0,0, 0,1,0),
    SOUTH(1,0,0, 0,1,0),
    EAST(0,0,-1, 0,1,0),
    WEST(0,0,1, 0,1,0),
    BOTTOM(1,0,0, 0,0,1);

    public Point3Di uVector, vVector;

    private FaceSide(int ux, int uy, int uz, int vx, int vy, int vz)
    {
        this.uVector = new Point3Di(ux, uy, uz);
        this.vVector = new Point3Di(vx,vy,vz);
    }

    public String capitalized()
    {
        String s = name().toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
