package com.purplefrog.minecraftExplorer.blockmodels;

import com.purplefrog.jwavefrontobj.*;
import org.json.*;

/**
 * Created by thoth on 9/26/14.
 */
public class RotationSpec
{
    private final double cosine;
    private final double sine;
    public Point3D origin;
    public String axis;
    public double angle;
    public boolean rescale;

    public RotationSpec(Point3D origin, String axis, double angle, boolean rescale)
    {
        this.origin = origin;
        this.axis = axis;
        this.angle = angle;
        this.rescale = rescale;

        double theta = angle*Math.PI/180;
        cosine = Math.cos(theta);
        sine = Math.sin(theta);
    }

    public static RotationSpec parse(JSONObject spec)
        throws JSONException
    {
        JSONArray ar = spec.getJSONArray("origin");
        Point3D origin = OneBlockModel.parsePoint(ar);

        String axis =spec.getString("axis");

        double angle = spec.getDouble("angle");

        boolean rescale = spec.optBoolean("rescale", false);

        return new RotationSpec(origin, axis, angle, rescale);
    }

    public Point3D transform(Point3D vert)
    {
        double resolution = 16;
        double ox = origin.x / resolution;
        double x0 = vert.x - ox;
        double oy = origin.y / resolution;
        double y0 = vert.y - oy;
        double oz = origin.z / resolution;
        double z0 = vert.z - oz;
        double x2=x0,y2=y0,z2=z0;
        if (axis.equals("y")) {
            x2 =x0 * cosine + z0*sine;
            z2 = z0*cosine - x0*sine;
        } else if (axis.equals("x")) {
            y2 = y0*cosine -z0*sine;
            z2 = y0*sine + z0*cosine;
        } else if (axis.equals("z")) {
            x2 = x0*cosine - y0*sine;
            y2 = x0*sine + y0*cosine;
        } else {
            throw new IllegalArgumentException("unsupported axis "+axis);
        }

        return new Point3D(x2 +ox, y2+oy, z2+oz);
    }
}
