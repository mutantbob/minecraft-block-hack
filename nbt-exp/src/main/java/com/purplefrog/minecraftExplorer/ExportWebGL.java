package com.purplefrog.minecraftExplorer;

import java.io.*;
import java.util.*;

/**
 * Created by thoth on 9/19/14.
 */
public class ExportWebGL
{

    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;
    private String ofname;

    public ExportWebGL(int x1, int y1, int z1, int x2, int y2, int z2, String ofname)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.ofname = ofname;
    }

    public static void main(String[] argv)
        throws IOException
    {
        MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        ExportWebGL exporter = new ExportWebGL(
//            0,0,0, 64, 256, 64,
//            -485, 60, 130, -426, 80, 190, // farm
            -485, 60, 159, -448, 80, 190, // farm house
//            64,0,-64, 128, 256, -1,
//            -470, 50, 0, 64, 80, 64,
            "/tmp/minecraft.gl.js");
        exporter.exportWebGL(editor);
    }

    public void exportWebGL(BasicBlockEditor editor)
        throws IOException
    {
        List<BlenderMeshElement> polys = new ArrayList<BlenderMeshElement>();

        if (true) {
            for (int y=y1; y<=y2; y++) {
                for (int x = x1; x<=x2; x++) {
                    for (int z=z1; z<=z2; z++) {
                        editor.getBlenderMeshElements(polys, x,y,z);
                    }
                }
            }
        } else {
            polys.add(new BlenderMeshElement.Widget(x1,y1,z1, 156, 0));
            polys.add(new BlenderMeshElement.Face(x1+1, y1, z1, 1, 0, FaceSide.TOP));
        }

        Writer w = new FileWriter(ofname);

        w.write("webgl_segments=[\n");
        GLStore glStore = new GLStore();
        for (BlenderMeshElement poly : polys) {
            if (glStore.vertices.size() > 0xff00) {
                dumpSegment(w, glStore);
                w.write(",\n");
                glStore = new GLStore();
            }
            poly.accumOpenGL(glStore);
        }

        dumpSegment(w, glStore);
        w.write("] // end webgl_segments\n");

        w.close();
    }

    private void dumpSegment(Writer w, GLStore glStore)
        throws IOException
    {
        Collections.sort(glStore.faces);

        w.write("{\n");

        w.write("//"+glStore.vertices.size()+" vertices; "+glStore.faces.size()+" faces\n");

        //

        w.write("vertex_xyz: [\n");
        for (XYZUV vertex : glStore.vertices) {
            w.write(vertex.x+","+vertex.y+","+vertex.z+",\n");
        }
        w.write("],\n\n");

        //

        w.write("vertex_uv : [\n");
        for (XYZUV vertex : glStore.vertices) {
            w.write(vertex.u+","+vertex.v+",\n");
        }
        w.write("],\n\n");

        //

        List<UniformFaceTextureExtent> boundaries = new ArrayList<UniformFaceTextureExtent>();
        TextureForGL color = null;
        w.write("faces : [\n");
        int cursor = 0;
        for (GLFace face : glStore.faces) {

            if (color == null || !face.bd.equals(color)) {
                w.write(" // " + face.bd.textureName + "\n");
                boundaries.add(new UniformFaceTextureExtent(face.bd, cursor));
            }
            color = face.bd;

            int v0 = face.vertices[0];
            int v1 = face.vertices[1];
            int v2 = face.vertices[2];
            int v3 = face.vertices[3];
            w.write(join(",",
                // two triangles for OpenGL
                v3,v0,v2,
                v0,v1,v2)+",\n");
            cursor += 6;
        }
        w.write("],\n\n");

        //

        w.write("texture_extents : [\n");
        for (int i = 0; i < boundaries.size(); i++) {
            UniformFaceTextureExtent te = boundaries.get(i);
            int after  = (i+1<boundaries.size()) ? boundaries.get(i+1).start : glStore.faces.size()*6;
            te.count = after - te.start;
            w.write(te.json()+",\n");
        }
        w.write("],\n\n");

        //

        w.write("}");
    }

    public static String join(String separator, int... elements)
    {
        StringBuilder rval = new StringBuilder();

        for (int i = 0; i < elements.length; i++) {
            if (i>0)
                rval.append(separator);
            rval.append(elements[i]);
        }
        return rval.toString();
    }

    public static class UniformFaceTextureExtent
    {
        public final TextureForGL tex;
        public final int start;
        int count;

        public UniformFaceTextureExtent(TextureForGL tex, int start)
        {
            this.tex = tex;
            this.start = start;
        }

        public String json()
        {
            return "{ \"name\" : \""+tex.textureName+"\", \"start\": "+start+", \"count\":"+count+" }";
        }
    }

    public static class TextureForGL
        implements Comparable<TextureForGL>
    {
        public final String textureName;

        public TextureForGL(String textureName)
        {
            this.textureName = textureName;
        }

        @Override
        public int compareTo(TextureForGL arg)
        {
            return textureName.compareTo(arg.textureName);
        }

        public static TextureForGL from(BlockPlusData bd, FaceSide orientation)
        {
            BlockSide o2;
            switch (orientation) {
                case TOP:
                    o2 = BlockSide.TOP;
                    break;
                case BOTTOM:
                    o2 = BlockSide.BOTTOM;
                    break;
                default:
                    o2 = BlockSide.SIDE;
            }
            return from(bd, o2);
        }

        public static TextureForGL from(BlockPlusData bd, BlockSide orientation)
        {
            return new TextureForGL("tex."+bd.blockType+"."+bd.data+"."+orientation.ordinal());
        }

        @Override
        public int hashCode()
        {
            return 804+textureName.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof TextureForGL) {
                TextureForGL arg = (TextureForGL) obj;
                return textureName.equals(arg.textureName);
            } else {
                return false;
            }
        }
    }

    public static class GLFace
        implements Comparable<GLFace>
    {

        public final TextureForGL bd;
        public final int[] vertices;

        public GLFace(TextureForGL bd, int[] vertices)
        {
            this.bd = bd;
            this.vertices = vertices;
        }

        @Override
        public int compareTo(GLFace arg)
        {
            return bd.compareTo(arg.bd);
        }
    }

    public static class GLStore
    {
        public List<XYZUV> vertices = new ArrayList<XYZUV>();
        public List<GLFace> faces = new ArrayList<GLFace>();

        public int getVertex(double x, double y, double z, double u, double v)
        {
            return getVertex(new XYZUV(x,y,z, u,v));
        }

        public int getVertex(XYZUV coords)
        {
            for (int i = 0; i < vertices.size(); i++) {
                XYZUV point3Di = vertices.get(i);
                if (point3Di.equals(coords))
                    return i;
            }

            int rval = vertices.size();
            vertices.add(coords);
            return rval;
        }

        public void addFace(TextureForGL bd, int... vertices)
        {
            faces.add(new GLFace(bd, vertices));
        }
    }


}
