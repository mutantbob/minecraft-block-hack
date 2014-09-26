package com.purplefrog.minecraftExplorer.blockmodels;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;

import java.util.*;

/**
 * Created by thoth on 9/26/14.
 */
public class BlockElement
{
    public final Point3Di from;
    public final Point3Di to;
    public final Map<String, FaceSpec> faces;
    private RotationSpec rot;

    public BlockElement(Point3Di from, Point3Di to, Map<String, FaceSpec> faces, RotationSpec rot)
    {
        this.from = from;
        this.to = to;
        this.faces = faces;
        this.rot = rot;
    }

    public void getPolys(List<GLPoly> polys, OneBlockModel textureBrain, BlockEnvironment env)
    {
        double[] uvSquare = {
            0, 0,
            0, 1,
            1, 1,
            1, 0,
        };

        double resolution=16;
        double minx = from.x/resolution;
        double maxx = to.x/resolution;
        double miny = from.y/resolution;
        double maxy = to.y/resolution;
        double minz = from.z/resolution;
        double maxz = to.z/resolution;

        {
            FaceSpec spec = faces.get("down");
            if (spec!=null  && env.notCulled(spec.cullface)) {
                double x1 = maxx;
                double x2 = minx;
                double z1 = maxz;
                double z2 = minz;
                polys.add(rotated(new GLPoly(giraffe(0,2, miny, x1, z1, x1, z2, x2, z2, x2, z1),
                    spec.rotated(uvSquare), textureBrain.resolveTexture(spec.textureName))));
            }
        }

        {
            FaceSpec spec = faces.get("up");
            if (spec!=null  && env.notCulled(spec.cullface)) {
                double x2 = maxx;
                double x1 = minx;
                double z2 = maxz;
                double z1 = minz;
                polys.add(rotated(new GLPoly(giraffe(0,2, maxy, x1, z1, x1, z2, x2, z2, x2, z1),
                    spec.rotated(uvSquare), textureBrain.resolveTexture(spec.textureName))));
            }
        }

        {
            FaceSpec spec = faces.get("north");
            if (spec!=null  && env.notCulled(spec.cullface)) {
                double x1 = minx;
                double x2 = maxx;
                double y1 = miny;
                double y2 = maxy;
                polys.add(rotated(new GLPoly(giraffe(0,1, minz, x1,y1,x1,y2,x2,y2,x2,y1),
                    spec.rotated(uvSquare), textureBrain.resolveTexture(spec.textureName))));
            }
        }
        {
            FaceSpec spec = faces.get("south");
            if (spec!=null  && env.notCulled(spec.cullface)) {
                double x1 = minx;
                double x2 = maxx;
                double y1 = miny;
                double y2 = maxy;
                polys.add(rotated(new GLPoly(giraffe(0,1, maxz,x2,y1,x2,y2,x1,y2, x1,y1),
                    spec.rotated(uvSquare), textureBrain.resolveTexture(spec.textureName))));
            }
        }


        {
            FaceSpec spec = faces.get("west");
            if (spec!=null  && env.notCulled(spec.cullface)) {
                double x1 = minz;
                double x2 = maxz;
                double y1 = miny;
                double y2 = maxy;

                polys.add(rotated(new GLPoly(giraffe(2,1, minx,x2,y1,x2,y2,x1,y2, x1,y1),
                    spec.rotated(uvSquare), textureBrain.resolveTexture(spec.textureName))));
            }
        }


        {
            FaceSpec spec = faces.get("east");
            if (spec!=null  && env.notCulled(spec.cullface)) {
                double x1 = minz;
                double x2 = maxz;
                double y1 = miny;
                double y2 = maxy;
                polys.add(rotated(new GLPoly(giraffe(2,1, maxx, x1,y1,x1,y2,x2,y2,x2,y1),
                    spec.rotated(uvSquare), textureBrain.resolveTexture(spec.textureName))));
            }
        }

    }

    private GLPoly rotated(GLPoly glPoly)
    {
        if (rot==null)
            return glPoly;

        Point3D[] v2 = new Point3D[glPoly.verts.length];
        for (int i = 0; i < v2.length; i++) {
            v2[i] = rot.transform(glPoly.verts[i]);
        }
        return new GLPoly(v2, glPoly.uvs, glPoly.texture);
    }

    private Point3D[] giraffe(int uAxis, int vAxis, double otherAxis, double... uvCoords)
    {
        Point3D[] rval = new Point3D[uvCoords.length/2];
        for (int i=0; i*2<uvCoords.length; i++) {

            double[] coords = new double[]{otherAxis, otherAxis, otherAxis};
            if (uAxis<0) {
                coords[-1-uAxis] = 1-uvCoords[i*2];
            } else {
                coords[uAxis] = uvCoords[i*2];
            }
            if (vAxis<0) {
                coords[-1-vAxis] = 1-uvCoords[i*2+1];
            } else {
                coords[vAxis] = uvCoords[i*2+1];
            }
            rval[i] = new Point3D(coords[0], coords[1], coords[2]);
        }

        return rval;
    }
}
