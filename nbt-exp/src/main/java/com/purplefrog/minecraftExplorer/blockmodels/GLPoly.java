package com.purplefrog.minecraftExplorer.blockmodels;

import com.purplefrog.jwavefrontobj.*;

/**
 * Created by thoth on 9/26/14.
 */
public class GLPoly
{
    public Point3D [] verts;
    public double[] uvs;
    public String texture;

    public GLPoly(Point3D[] verts, double[] uvs, String texture)
    {
        this.verts = verts;
        this.uvs = uvs;
        this.texture = texture;
        for (int i=0; i<verts.length; i++) {
            if (verts[i] == null)
                throw new NullPointerException();
        }
    }
}
