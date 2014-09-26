package com.purplefrog.minecraftExplorer.blockmodels;

import org.json.*;

/**
 * Created by thoth on 9/26/14.
 */
public class FaceSpec
{
    public final String textureName;
    public final String cullface;
    public final double rotate;

    public FaceSpec(String textureName, String cullface, double rotate)
    {

        this.textureName = textureName;
        this.cullface = cullface;
        this.rotate = rotate;
    }

    public static FaceSpec parse(JSONObject spec)
        throws JSONException
    {
        String textureName = spec.getString("texture");
        String cullface = spec.optString("cullface", null);
        double rotation = spec.optDouble("rotation", 0);
        return new FaceSpec(textureName, cullface,rotation);
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

            double sine = Math.sin(rotate * 3.14159 / 180);
            double cosine = Math.cos(rotate * 3.14159 / 180);
            double x2 = x0*cosine - y0*sine;
            double y2 = y0*cosine + x0*sine;

            rval[i] = x2+0.5;
            rval[i+1] = y2+0.5;
        }

        return rval;
    }
}
