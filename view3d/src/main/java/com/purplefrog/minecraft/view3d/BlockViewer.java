package com.purplefrog.minecraft.view3d;

import com.jogamp.opengl.util.*;
import org.json.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.*;

/**
 * Created by thoth on 10/14/14.
 */
public class BlockViewer
    implements GLEventListener
{
    private boolean quit=false;
    private int w, h;
    ModelAndView mv;
    protected String vertexShaderSrc;
    protected String fragmentShaderSrc;
    private int shaderProgram;

    public BlockViewer()
        throws IOException, JSONException
    {
        if (true) {
            vertexShaderSrc = slurp(BlockViewer.class.getResourceAsStream("vertex-shader.glsl"));
            fragmentShaderSrc = slurp(BlockViewer.class.getResourceAsStream("fragment-shader.glsl"));
        } else {
            vertexShaderSrc = slurp(BlockViewer.class.getResourceAsStream("demo-vertex-1.glsl"));
            fragmentShaderSrc = slurp(BlockViewer.class.getResourceAsStream("demo-fragment-1.glsl"));

        }
        mv = new ModelAndView();
    }

    public static void main(String[] argv)
        throws IOException, JSONException
    {
        BlockViewer blockViewer = new BlockViewer();

        JFrame fr = new JFrame("JOGL");

        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(blockViewer);
        canvas.setFocusable(true);
        canvas.requestFocus();

        Animator animator = new Animator(canvas);
        animator.setRunAsFastAsPossible(true);
        animator.start();

        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fr.getContentPane().add(canvas, BorderLayout.CENTER);
        fr.setSize(640,480);
        fr.setVisible(true);

    }

    public void init(GLAutoDrawable drawable) {
        System.out.println("init()");
        GL2 gl = (GL2) drawable.getGL();
        if(!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object")){
            System.out.println("Error: VBO support is missing");
            quit=true;
        }

        compileAndLoadShaderProgram(gl);

        rigMatricesAndStuff(gl);

        mv.loadTextures(gl);

    }

    public void compileAndLoadShaderProgram(GL2 gl)
    {
        int vertexSP = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        gl.glShaderSource(vertexSP, 1, new String[] {vertexShaderSrc}, null);
        gl.glCompileShader(vertexSP);

        int fragmentSP = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fragmentSP, 1, new String[] {fragmentShaderSrc}, null);
        gl.glCompileShader(fragmentSP);

        shaderProgram = gl.glCreateProgram();
        gl.glAttachShader(shaderProgram, vertexSP);
        gl.glAttachShader(shaderProgram, fragmentSP);
        gl.glLinkProgramARB(shaderProgram);
        gl.glValidateProgram(shaderProgram);

        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl.glGetProgramiv(shaderProgram, GL2.GL_LINK_STATUS,intBuffer);
        if (intBuffer.get(0)!=1){
            gl.glGetProgramiv(shaderProgram, GL2.GL_INFO_LOG_LENGTH,intBuffer);
            int size = intBuffer.get(0);
            System.err.println("Program link error: ");
            if (size>0){
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl.glGetProgramInfoLog(shaderProgram, size, intBuffer, byteBuffer);
                for (byte b:byteBuffer.array()){
                    System.err.print((char)b);
                }
            } else {
                System.out.println("Unknown");
            }
            throw new IllegalStateException("failed to link shaders");
        }
        gl.glUseProgram(shaderProgram);
    }

    public static String slurp(InputStream istr)
        throws IOException
    {
        return slurp(new InputStreamReader(istr));
    }

    public static String slurp(Reader r)
        throws IOException
    {
        StringBuilder rval = new StringBuilder();

        char[] buf = new char[4<<10];
        while (true) {
            int n = r.read(buf);
            if (n<1)
                break;
            rval.append(buf, 0, n);
        }

        return rval.toString();
    }

    public void rigMatricesAndStuff(GL2 gl)
    {
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU glu=new GLU();
        glu.gluPerspective(45.0f, ((float) w /(float)h), 0.1f, 1000.0f);

        gl.glMatrixMode(gl.GL_MODELVIEW);

        gl.glShadeModel(gl.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.2f, 0.0f, 0.0f);

        gl.glClearDepth(1000.0f);
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glDepthFunc(gl.GL_LEQUAL);

        gl.glEnable(GL.GL_CULL_FACE);

        gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_NICEST);

        gl.glAlphaFunc(GL.GL_GREATER, 0.5f);
        gl.glEnable(GL2.GL_ALPHA_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable)
    {
        mv.disposeTextures(glAutoDrawable.getGL());

        System.out.println("dispose()");

    }

    public void display(GLAutoDrawable drawable) {
//        System.out.println("display()");
        GL2 gl2 = (GL2) drawable.getGL();
        if (true && ! (gl2 instanceof DebugGL2)) {
            gl2 = new DebugGL2(gl2);
//            drawable.setGL(new DebugGL2(gl2));
        }
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();

//        mv.display2(gl2);
        mv.display3(gl2,shaderProgram);

        drawAxes(gl2, 0, 0, 0);

        gl2.glFlush();
    }

    public static void drawAxes(GL2 gl2, double x, double y, double z)
    {
        gl2.glLineWidth(2.5f);
        double q=-0.5;
        gl2.glBegin(GL2.GL_LINES);
        gl2.glColor3f(1, 0, 0);
        gl2.glVertex3d(x +q+1, y +q, z +q);
        gl2.glColor3f(1, 0, 0);
        gl2.glVertex3d(x +q, y +q, 0+q);
        gl2.glEnd();

        gl2.glBegin(GL2.GL_LINES);
        gl2.glColor3f(0, 1, 0);
        gl2.glVertex3d(x + q, y +q+1, z +q);
        gl2.glColor3f(0, 1, 0);
        gl2.glVertex3d(x +q, y +q, z +q);
        gl2.glEnd();

        gl2.glBegin(GL2.GL_LINES);
        gl2.glColor3f(0, 0, 1);
        gl2.glVertex3d(x +q, y +q, z +q+1);
        gl2.glColor3f(0, 0, 1);
        gl2.glVertex3d(x +q, y +q, z +q);
        gl2.glEnd();

        gl2.glColor3f(1,1,1);
    }

    public void rainbowSquare(GL2 gl2)
    {
        gl2.glBegin(GL2.GL_QUADS);
        gl2.glColor3f(1, 0, 0);
        gl2.glVertex2f(0, 0);

        gl2.glColor3f(1, 1, 0);
        gl2.glVertex2f(1, 0);

        gl2.glColor3f(0, 1, 0);
        gl2.glVertex2f(1, 1);

        gl2.glColor3f(0, 0, 1);
        gl2.glVertex2f(0, 1);

        gl2.glEnd();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("reshape()");
        GL2 gl2 = (GL2) drawable.getGL();

        w = width;
        h = height;
        rigMatricesAndStuff(gl2);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean
        deviceChanged) {}

}
