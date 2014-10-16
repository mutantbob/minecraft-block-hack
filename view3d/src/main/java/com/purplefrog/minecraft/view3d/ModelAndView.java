package com.purplefrog.minecraft.view3d;

import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.Texture;
import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.blockmodels.*;
import org.apache.log4j.*;
import org.json.*;

import javax.media.opengl.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by thoth on 10/16/14.
 */
public class ModelAndView
{
    private static final Logger logger = Logger.getLogger(ModelAndView.class);

    private File textureDir = new File("/home/thoth/src/minecraft-webgl/minecraft-textures");
    private Map<String, TextureData> textureData = new HashMap<String, TextureData>();
    private Map<String, Texture> textureMap = new HashMap<String, Texture>();

    private final ExportWebGL.GLStore glStore;
    protected ModelViewSetter modelView;

    public ModelAndView()
        throws IOException, JSONException
    {
        BlockModels blockModels = BlockModels.getInstance();
        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();

        switch (2) {
            case 3:

            {
                MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
                BasicBlockEditor editor = new AnvilBlockEditor(world);

                int x1 = -485, y1=60, z1=130, x2=-426, y2=80, z2=190;

                for (int y=y1; y<=y2; y++) {
                    for (int x = x1; x<=x2; x++) {
                        for (int z=z1; z<=z2; z++) {
                            editor.getBlenderMeshElements(accum, x,y,z);
                        }
                    }
                }
                modelView = new ModelViewSetter(-(x1+x2)/2.0, -(y1+y2)/2.0, -(z1+z2)/2.0,
                    5,
                    0,0, -Math.max(x2-x1, z2-z1)*1.2,
                    30);
            }
                break;
            case 2:
                int bt = BlockDatabase.BLOCK_TYPE_QUARTZ_STAIRS;
                blocksPerData(blockModels, accum, bt);
                modelView = new ModelViewSetter(-3.5, -1.5, -3.5, 5, 0, 0, -12, 30);

                break;
            case 1:
                blocks8x8parade(blockModels, accum, 64);
                modelView = new ModelViewSetter(-7.5, -1.5, -7.5, 5, 0, 0, -20, 24);

                break;
            default:
                singleBlock(blockModels, accum, BlockDatabase.BLOCK_TYPE_TRAPDOOR, 0);
                modelView = new ModelViewSetter(-0.5, -1.5, -0.5, 10, 0, 0, -6, 120);
                break;
        }

        glStore = new ExportWebGL.GLStore();
        for (BlenderMeshElement bme : accum) {
            bme.accumOpenGL(glStore);
        }
    }

    public static void blocksPerData(BlockModels blockModels, List<BlenderMeshElement> accum, int bt)
        throws IOException, JSONException
    {
        BlockEnvironment env = new BlockEnvironment(new boolean[6]);
        int cols = 4;
        for (int blockData=0; blockData<16; blockData++) {
            int x = 2*(blockData % cols);
            int y = 0;
            int z = 2*(blockData / cols);
            blockModels.modelFor(bt, blockData).getMeshElements(accum, x, y, z, env);
        }
    }

    public static void singleBlock(BlockModels blockModels, List<BlenderMeshElement> accum, int bt, int blockData)
        throws IOException, JSONException
    {
        int x = 0, y = 0, z = 0;
        BlockEnvironment env = new BlockEnvironment(new boolean[6]);
        blockModels.modelFor(bt, blockData).getMeshElements(accum, x, y, z, env);
    }

    public static void blocks8x8parade(BlockModels blockModels, List<BlenderMeshElement> accum, int baseBT)
        throws IOException, JSONException
    {
        BlockEnvironment env = new BlockEnvironment(new boolean[6]);
        int cols = 8;
        for (int i=0; i<64; i++) {
            int blockData = 0;
            int x = 2*(i % cols);
            int y = 0;
            int z = 2*(i / cols);
            blockModels.modelFor(i+ baseBT, blockData).getMeshElements(accum, x, y, z, env);
        }
    }

    public void loadTextures(GL2 gl)
    {
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

    public void display(GL2 gl2)
    {
        modelView.invoke(gl2);

        for (ExportWebGL.GLFace face : glStore.faces) {
            renderFace1(gl2, face);
        }

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

    public void renderFace1(GL2 gl2, ExportWebGL.GLFace face)
    {
        Texture t = getTextureFor(gl2, face.bd.textureName);
        if (t==null) {
            return;
        }
        t.bind(gl2);

        gl2.glBegin(GL2.GL_QUADS);
        for (int idx : face.vertices) {
            XYZUV vi = glStore.vertices.get(idx);
            gl2.glTexCoord2d(vi.u, vi.v);
            gl2.glVertex3d(vi.x, vi.y, vi.z);
        }
        gl2.glEnd();
    }

    public static void copyXYZUV(GL2 gl2, XYZUV vi)
    {
        gl2.glTexCoord2d(vi.u, vi.v);
        gl2.glVertex3d(vi.x, vi.y, vi.z);
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
    }
}
