package com.purplefrog.minecraftExplorer.blockmodels;

import org.json.*;

/**
 * Created by thoth on 9/26/14.
 */
public class FaceSpec
{
    public final static double[] DEFAULT_UVSQUARE = {
        0, 0,
        0, 1,
        1, 1,
        1, 0
    };
    public final String textureName;
    public final String cullface;
    public final double rotate;
    private double[] uv;
    private final double sine;
    private final double cosine;

    public FaceSpec(String textureName, String cullface, double rotate, double[] uvMinMax)
    {

        this.textureName = textureName;
        this.cullface = cullface;
        this.rotate = rotate;
        this.uv = expandUV(uvMinMax);

        if (rotate==0) {
            sine=0;
            cosine=1;
        } else if (rotate==90) {
            sine=1;
            cosine=0;
        } else if (rotate==180) {
            sine=0;
            cosine=-1;
        } else if (rotate==270) {
            sine=-1;
            cosine=0;
        } else {
            sine = Math.sin(rotate * 3.14159 / 180);
            cosine = Math.cos(rotate * 3.14159 / 180);
        }
    }

    private double[] expandUV(double[] uvMinMax)
    {
        if (uvMinMax ==null)
            return null;

        final double minX = uvMinMax[0];
        final double minY = uvMinMax[1];
        final double maxX = uvMinMax[2];
        final double maxY = uvMinMax[3];
        double[] rval = new double[] {
            minX, minY,
            minX, maxY,
            maxX, maxY,
            maxX, minY,
        };

        return rval;
    }

    public static FaceSpec parse(JSONObject spec)
        throws JSONException
    {
        String textureName = spec.getString("texture");
        String cullface = spec.optString("cullface", null);
        double rotation = spec.optDouble("rotation", 0);
        JSONArray uv16 = spec.optJSONArray("uv");
        double[] uv = minecraftToOpenGL(uv16);
        return new FaceSpec(textureName, cullface,rotation, uv);
    }

    public static double[] minecraftToOpenGL(JSONArray uv16)
        throws JSONException
    {
        double[] uv;
        if (uv16 == null) {
            uv=null;
        } else {
            uv = new double[uv16.length()];
            for (int i=0; i<uv.length; i++) {
                uv[i] = uv16.getDouble(i)/16;
            }

            // I can't think of a better place to "fix" this
            double miny = uv[1];
            double maxy = uv[3];
            uv[1] = 1-maxy;
            uv[3] = 1-miny;
        }
        return uv;
    }

    public double[] rotated(double[] uvSquare)
    {
        if (rotate==0)
            return uvSquare;

        double[] rval = new double[uvSquare.length];
        for (int i=0; i<uvSquare.length; i+=2)
        {
            double x0 = uvSquare[i]-0.5;
            double y0 = uvSquare[i+1]-0.5;

            double x2 = x0*cosine - y0*sine;
            double y2 = y0*cosine + x0*sine;

            rval[i] = x2+0.5;
            rval[i+1] = y2+0.5;
        }

        return rval;
    }

    public double[] getUV()
    {
        return rotated(uv==null ? DEFAULT_UVSQUARE : uv);
    }
}
