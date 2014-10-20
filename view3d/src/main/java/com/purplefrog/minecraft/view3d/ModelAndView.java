package com.purplefrog.minecraft.view3d;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.Texture;
import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.blockmodels.*;
import org.apache.log4j.*;
import org.json.*;

import javax.media.opengl.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

/**
 * Created by thoth on 10/16/14.
 */
public class ModelAndView
{
    private static final Logger logger = Logger.getLogger(ModelAndView.class);
    public static final BlockEnvironment ISOLATION = new BlockEnvironment(new boolean[6], new boolean[6]);

    private File textureDir = new File("/home/thoth/src/minecraft-webgl/minecraft-textures");
    private Map<String, TextureData> textureData = new HashMap<String, TextureData>();
    private Map<String, Texture> textureMap = new HashMap<String, Texture>();

    private final List<GLBufferSet> bufferSets = new ArrayList<GLBufferSet>();
    protected ModelViewSetter modelView;

    public ModelAndView()
        throws IOException, JSONException
    {
        BlockModels blockModels = BlockModels.getInstance();
        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();

        switch (6) {
            case 2:
                int bt = BlockDatabase.BLOCK_TYPE_QUARTZ_STAIRS;
                blocksPerData(blockModels, accum, bt);
                modelView = new ModelViewSetter(-3.5, -1.5, -3.5, 5, 0, 0, -12, 30);

                break;
            case 1:
                blocks8x8parade(blockModels, accum, 64);
                modelView = new ModelViewSetter(-7.5, -1.5, -7.5, 5, 0, 0, -20, 24);

                break;
            case 3:

                farm(accum);
                break;
            case 4:
            {
                cullTest(blockModels,  accum, 1, 0);
                modelView = new ModelViewSetter(-5.5, -1.5, -0.5, 10, 0, 0, -12, 10);
            }
            break;
            case 5:

                house1(accum);
                break;
            case 6:
            {
                int x =0;
                double y=0;
                int z=0;

                GLPoly poly = new GLPoly(new Point3D[]{new Point3D(x, y, z + 0.5), new Point3D( 1, 0, 0.5), new Point3D( 1, 1, 0.5), new Point3D(0, 1,  0.5)},
                    FaceSpec.DEFAULT_UVSQUARE, "blocks/dirt");
                for (x=-10; x<10; x++) {
                    for (z=-10; z<10; z++) {
                        accum.add(new OneBlockModel.BaconMeshElement(poly, x, (int) y, z));
                    }
                }
                modelView = new ModelViewSetter(-0.5, -1.5, -0.5, 10, 0, 0, -6, 10);
            }
            break;

            default:
                singleBlock(blockModels, accum, BlockDatabase.BLOCK_TYPE_TRAPDOOR, 0);
                modelView = new ModelViewSetter(-0.5, -1.5, -0.5, 10, 0, 0, -6, 120);
                break;
        }

        {

            ExportWebGL.GLStore glStore = new ExportWebGL.GLStore();
            for (BlenderMeshElement bme : accum) {
                if (glStore.vertices.size() >= 64000) {
                    bufferSets.add(new GLBufferSet(glStore));
                    glStore.clear();
                }

                bme.accumOpenGL(glStore);
            }

            bufferSets.add(new GLBufferSet(glStore));
        }

    }

    public void farm(List<BlenderMeshElement> accum)
    {
        MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = -485, y1=60, z1=130, x2=-426, y2=80, z2=190;

        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);
    }

    public void farmHouse(List<BlenderMeshElement> accum)
    {
        MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = -485, y1=60, z1=159, x2=-448, y2=80, z2=190;

        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);
    }

    public void melonPatch(List<BlenderMeshElement> accum)
    {
        MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = -448, y1=60, z1=159, x2=-439, y2=80, z2=180;

        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);
    }

    public void house1(List<BlenderMeshElement> accum)
    {
        MinecraftWorld world = new MinecraftWorld(new File(WorldPicker.savesDir(), "2014-Sep-28 v1_8"));
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = 160, y1=50, z1=190, x2=210, y2=90, z2=240;

        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);
    }

    public void convertWorld(List<BlenderMeshElement> accum, BasicBlockEditor editor, int x1, int y1, int z1, int x2, int y2, int z2)
    {
        for (int y=y1; y<=y2; y++) {
            for (int x = x1; x<=x2; x++) {
                for (int z=z1; z<=z2; z++) {
                    editor.getBlenderMeshElements(accum, x,y,z);
                }
            }
        }
        modelView = new ModelViewSetter(-(x1+x2)/2.0, -(y1+y2)/2.0, -(z1+z2)/2.0,
            10,
            0,0, -Math.max(x2-x1, z2-z1)*1.2,
            30);
    }

    public static void blocksPerData(BlockModels blockModels, List<BlenderMeshElement> accum, int bt)
        throws IOException, JSONException
    {
        int cols = 4;
        for (int blockData=0; blockData<16; blockData++) {
            int x = 2*(blockData % cols);
            int y = 0;
            int z = 2*(blockData / cols);
            blockModels.modelFor(bt, blockData, ISOLATION).getMeshElements(accum, x, y, z, ISOLATION);
        }
    }

    public static void singleBlock(BlockModels blockModels, List<BlenderMeshElement> accum, int bt, int blockData)
        throws IOException, JSONException
    {
        int x = 0, y = 0, z = 0;
        blockModels.modelFor(bt, blockData, ISOLATION).getMeshElements(accum, x, y, z, ISOLATION);
    }

    public static void cullTest(BlockModels blockModels, List<BlenderMeshElement> accum, int bt, int blockData)
        throws IOException, JSONException
    {
        for (int i=0; i<6; i++) {
            boolean[] culling = new boolean[6];
            culling[i] = true;
            BlockEnvironment env = new BlockEnvironment(culling, culling);
            blockModels.modelFor(bt, blockData, env).getMeshElements(accum, i*2, 0, 0, env);
        }
    }

    public static void blocks8x8parade(BlockModels blockModels, List<BlenderMeshElement> accum, int baseBT)
        throws IOException, JSONException
    {
        int cols = 8;
        for (int i=0; i<64; i++) {
            int blockData = 0;
            int x = 2*(i % cols);
            int y = 0;
            int z = 2*(i / cols);
            blockModels.modelFor(i+ baseBT, blockData, ISOLATION).getMeshElements(accum, x, y, z, ISOLATION);
        }
    }

    //
    //
    //


    public void loadTextures(GL2 gl)
    {
        for (GLBufferSet bs : bufferSets) {
            for (String tname : bs.perTexture.keySet()) {
                getTextureFor(gl, tname);
            }
        }
    }

    public Texture getTextureFor(GL2 gl, String textureName)
    {
        Texture rval = textureMap.get(textureName);
        if (null==rval) {
            try {
                rval = new Texture(gl, getTextureDataFor(textureName));

                gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
                gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);

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

    public void disposeTextures(GL gl)
    {
        for (Texture texture : textureMap.values()) {
            texture.destroy(gl);
        }
        textureMap.clear();
    }


    /**
     * use a GLSL shader program
     */
    public void display3(GL2 gl2, int shaderProgram)
    {
        gl2.glEnable(GL2.GL_VERTEX_ARRAY);
        gl2.glEnable(GL2.GL_TEXTURE_COORD_ARRAY);

        int tint_idx = gl2.glGetUniformLocation(shaderProgram, "u_tint");

        int TEXTURE_NUMBER = 0;

        int tex_idx = gl2.glGetUniformLocation(shaderProgram, "tex0");
        if (tex_idx>=0) {
            gl2.glUniform1i(tex_idx, TEXTURE_NUMBER);
        }

        if (false) {
            try {
                int mvpm_idx = gl2.glGetUniformLocation(shaderProgram, "u_modelViewProjMatrix");
                gl2.glUniformMatrix4fv(mvpm_idx, 1, false, modelView.getMatrix(), 0);
            } catch (GLException e) {
                // ignore it
            }
        } else {
            modelView.invoke(gl2);
        }

        //

        gl2.glActiveTexture(GL2.GL_TEXTURE0+ TEXTURE_NUMBER);

        //

        int uv_idx = gl2.glGetAttribLocation(shaderProgram, "vTexCoord");

        for (GLBufferSet bufferSet : bufferSets) {

            gl2.glVertexPointer(3, GL.GL_FLOAT, 0, bufferSet.vertices);
            gl2.glEnableVertexAttribArray(uv_idx);
            gl2.glVertexAttribPointer(uv_idx, 2, GL.GL_FLOAT, false, 0, bufferSet.uvs);

            for (Map.Entry<String, ShortBuffer> en : bufferSet.perTexture.entrySet()) {
                String tname = en.getKey();
                Texture t = getTextureFor(gl2, tname);
                if (t==null) {
                    logger.error("no texture for "+tname);
                    continue;
                }
                t.bind(gl2);

                gl2.glUniform4fv(tint_idx, 1, getTint(tname), 0);

                ShortBuffer indices = en.getValue();
                gl2.glDrawElements(GL2.GL_QUADS, indices.limit(), GL2.GL_UNSIGNED_SHORT, indices);
            }
        }

        gl2.glDisable(GL2.GL_VERTEX_ARRAY);
        gl2.glDisable(GL2.GL_TEXTURE_COORD_ARRAY);
    }

    public static float[] GRASS_TINT ={ 0,1,0,1};
    public static float[] NO_TINT = {1,1,1,1};

    public static float[] getTint(String tname)
    {
        float[] tint;
        if ("blocks/grass_top".equals(tname)
            || "blocks/grass_side_overlay".equals(tname)) {
            tint = GRASS_TINT;
        } else {
            tint = NO_TINT;
        }
        return tint;
    }

    public void renderFace2(GL2 gl2, GLPoly bacon)
    {
        Texture t = getTextureFor(gl2, bacon.texture);
        t.bind(gl2);
        gl2.glBegin(GL2.GL_QUADS);

        for (int i = 0; i < bacon.verts.length; i++) {
            Point3D vert = bacon.verts[i];
            gl2.glTexCoord2d(bacon.uvs[i * 2], 1 - bacon.uvs[i * 2 + 1]);
            gl2.glVertex3d(vert.x, vert.y, vert.z);
        }

        gl2.glEnd();
    }



    public void testFaces(GL2 gl2)
    {
        OneBlockModel bm = new OneBlockModel(0);
        String textureName = "blocks/grass_side";
        renderFace2(gl2, BlockElement.polyDown(bm, 0, 1, 0, 0, 1, new FaceSpec("blocks/dirt", "down", 0, null)));
        FaceSpec side = new FaceSpec(textureName, null, 0, null);
        renderFace2(gl2, BlockElement.polyNorth(bm, 0, 1, 0, 1, 0, side));
        renderFace2(gl2, BlockElement.polySouth(bm, 0, 1, 0, 1, 1, side));
        renderFace2(gl2, BlockElement.polyEast(bm, 1, 0, 1, 0, 1, side));
        renderFace2(gl2, BlockElement.polyWest(bm, 0, 0, 1, 0, 1, side));
        renderFace2(gl2, BlockElement.polyUp(bm, 0, 1, 1, 0, 1, new FaceSpec("blocks/grass_top", "up", 0, null)));
    }

    //

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
}
