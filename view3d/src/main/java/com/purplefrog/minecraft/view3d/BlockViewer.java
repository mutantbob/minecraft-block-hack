package com.purplefrog.minecraft.view3d;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.Texture;
import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.blockmodels.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import org.apache.log4j.*;
import org.json.*;

import javax.media.j3d.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by thoth on 10/14/14.
 */
public class BlockViewer
    implements GLEventListener
{
    private static final Logger logger = Logger.getLogger(BlockViewer.class);

    private final ExportWebGL.GLStore glStore;
    private boolean quit=false;
    private int w, h;
    private TextureData td;
    private Texture texture;
    private File textureDir = new File("/home/thoth/src/minecraft-webgl/minecraft-textures");
    private Map<String, TextureData> textureData = new HashMap<String, TextureData>();
    private Map<String, Texture> textureMap = new HashMap<String, Texture>();
    protected ModelViewSetter modelView;


    public BlockViewer()
        throws IOException, JSONException
    {
        File sapling = new File("/home/thoth/src/minecraft-webgl/minecraft-textures/blocks/sapling_birch.png");
        td = TextureIO.newTextureData(GLProfile.getDefault(), sapling, false, "png");

        BlockModels blockModels = BlockModels.getInstance();
        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        if (false) {
            blocks8x8parade(blockModels, accum);
            modelView = new ModelViewSetter(-7.5, -1.5, -7.5, 5, 0, 0, -20, 24);

        } else {
            singleBlock(blockModels, accum, 2,0);
            modelView = new ModelViewSetter(-0.5, -1.5, -0.5, 10, 0, 0, -12, 10);
        }

        glStore = new ExportWebGL.GLStore();
        for (BlenderMeshElement bme : accum) {
            bme.accumOpenGL(glStore);
        }
    }

    public void singleBlock(BlockModels blockModels, List<BlenderMeshElement> accum, int bt, int blockData)
        throws IOException, JSONException
    {
        int x = 0, y = 0, z = 0;
        BlockEnvironment env = new BlockEnvironment(new boolean[6]);
        blockModels.modelFor(bt, blockData).getMeshElements(accum, x, y, z, env);
    }

    public void blocks8x8parade(BlockModels blockModels, List<BlenderMeshElement> accum)
        throws IOException, JSONException
    {
        int cols = 8;
        for (int i=0; i<64; i++) {
            int bt = i;
            int blockData = 0;
            int x = 2*(i % cols), y = 0, z = 2*(i / cols);
            BlockEnvironment env = new BlockEnvironment(new boolean[6]);
            blockModels.modelFor(bt, blockData).getMeshElements(accum, x, y, z, env);
        }
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

        rigMatricesAndStuff(gl);

        texture = new Texture(gl, td);

        for (ExportWebGL.GLFace face : glStore.faces) {
            Texture t = getTextureFor(gl, face.bd.textureName);
        }

    }

    private Texture getTextureFor(GL2 gl, String textureName)
    {
        Texture rval = textureMap.get(textureName);
        if (null==rval) {
            try {
                rval = new Texture(gl, getTextureDataFor(textureName));
                textureMap.put(textureName, rval);
                rval.enable(gl);
            } catch (IOException e) {
                logger.warn("failed to load texture for "+textureName, e);
                return null;
            }
        }
        return rval;
    }

    private TextureData getTextureDataFor(String textureName)
        throws IOException
    {
        TextureData rval;
        rval = textureData.get(textureName);
        if (rval == null) {
            rval = TextureIO.newTextureData(GLProfile.getDefault(), new File(textureDir, textureName+".png"), false, "png");
            textureData.put(textureName, rval);
        }
        return rval;
    }

    public void rigMatricesAndStuff(GL2 gl)
    {
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU glu=new GLU();
        glu.gluPerspective(45.0f, ((float) w /(float)h), 0.1f, 100.0f);

        gl.glMatrixMode(gl.GL_MODELVIEW);

        gl.glShadeModel(gl.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.2f, 0.0f, 0.0f);

        gl.glClearDepth(1000.0f);
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glDepthFunc(gl.GL_LEQUAL);

        gl.glEnable(GL.GL_CULL_FACE);

        gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_NICEST);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable)
    {
        textureMap.clear();

        System.out.println("dispose()");

    }

    public void display(GLAutoDrawable drawable) {
//        System.out.println("display()");
        GL2 gl2 = (GL2) drawable.getGL();
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, gl2.GL_NEAREST);
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, gl2.GL_NEAREST);

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();
        gl2.glTranslated(0, 0, -24);

        gl2.glRotated(5, 1,0,0);
        gl2.glRotated(rot.getDegrees(), 0,1,0);

        gl2.glTranslated(-7.5, -1.5, -7.5);


//        texture.bind(gl2);

        if (true) {
            for (ExportWebGL.GLFace face : glStore.faces) {
                Texture t = getTextureFor(gl2, face.bd.textureName);
                if (t==null) {
                    continue;
                }
                t.bind(gl2);

                gl2.glBegin(GL2.GL_QUADS);
                for (int idx : face.vertices) {
                    XYZUV vi = glStore.vertices.get(idx);
                    gl2.glVertex3d(vi.x, vi.y, vi.z);
                    gl2.glTexCoord2d(vi.u, vi.v);
                }
                gl2.glEnd();

            }
        } else {
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

        gl2.glFlush();
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


    public static class SphereDemo {
        public SphereDemo()
        {
            SimpleUniverse universe = new SimpleUniverse();

            BranchGroup group = new BranchGroup();

            Sphere sphere = new Sphere(0.3f);
            group.addChild(sphere);

            Color3f light1Color = new Color3f(1.8f, 0.1f, 0.1f);

            BoundingSphere bounds =

                new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

            Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);

            DirectionalLight light1

                = new DirectionalLight(light1Color, light1Direction);

            light1.setInfluencingBounds(bounds);

            group.addChild(light1);        universe.getViewingPlatform().setNominalViewingTransform();

            universe.addBranchGraph(group);
        }

        public static void main(String[] argv)
        {
            new SphereDemo();

        }
    }

    public static class ModelViewSetter
    {
        protected final double pitch;
        protected final double cx;
        protected final double cy;
        protected final double cz;
        protected final double camx;
        protected final double camy;
        protected final double camz;
        private Rotatron rot;

        public ModelViewSetter(double cx, double cy, double cz, double pitch, int camx, int camy, int camz, int periodSeconds)
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
    }
}
