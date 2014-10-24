package com.purplefrog.minecraft.view3d;

import com.jogamp.opengl.util.*;

import javax.media.opengl.*;
import java.nio.*;

/**
 * Created by thoth on 10/24/14.
 */
public class ModelViewSetter
{

    protected final double pitch;
    protected final double cx;
    protected final double cy;
    protected final double cz;
    protected final double camx;
    protected final double camy;
    protected final double camz;
    private Rotatron rot;

    public ModelViewSetter(double cx, double cy, double cz, double pitch, double camx, double camy, double camz, double periodSeconds)
    {
        this.pitch = pitch;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.camx = camx;
        this.camy = camy;
        this.camz = camz;
        rot = new Rotatron(periodSeconds);
    }

    /**
     * call this right after
     * <pre>
     gl2.glMatrixMode( GL2.GL_MODELVIEW );
     gl2.glLoadIdentity();
     * </pre>
     */
    public void invoke(GL2 gl2)
    {
        gl2.glTranslated(camx, camy, camz);

        gl2.glRotated(pitch, 1, 0, 0);
        gl2.glRotated(rot.getDegrees(), 0, 1, 0);

        gl2.glTranslated(cx, cy, cz);
    }

    public float[] getMatrix()
    {
        PMVMatrix gl2 = new PMVMatrix();

        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        gl2.gluPerspective(30, 1, 0.01f, 1000f);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);

        gl2.glTranslatef((float) camx, (float) camy, (float) camz);

        gl2.glRotatef((float) pitch, 1, 0, 0);
        gl2.glRotatef((float) rot.getDegrees(), 0, 1, 0);

        gl2.glTranslatef((float) cx, (float) cy, (float) cz);

        gl2.update();

        FloatBuffer tmp = gl2.glGetPMvMatrixf();

        float[] rval = new float[16];
        for (int i=0; i<rval.length; i++) {
            rval[i] = tmp.get(i);
        }

        return rval;
    }

}