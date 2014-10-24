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

        if (argv.length != 7) {
            usage(System.err);
            return;
        }

        int x1 = Integer.parseInt(argv[0]);
        int y1 = Integer.parseInt(argv[1]);
        int z1 = Integer.parseInt(argv[2]);
        int x2 = Integer.parseInt(argv[3]);
        int y2 = Integer.parseInt(argv[4]);
        int z2 = Integer.parseInt(argv[5]);

        String ofname = argv[6];

        ExportWebGL exporter = new ExportWebGL(
            x1,y1,z1,x2,y2,z2,
            ofname);
        exporter.exportWebGL(editor);

        System.out.println("writing to "+ofname);
    }

    public static void usage(PrintStream out)
    {
        out.println("Usage:\njava "+ExportWebGL.class.getName()+" x1 y1 z1 x2 y2 z2 ofname.js");
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
        GLStore opaque = new GLStore();
        GLStore translucent = new GLStore();
        GLStore glStore = new GLStore();
        for (BlenderMeshElement poly : polys) {
            glStore.clear();
            maybeFlush(w, opaque);
            poly.accumOpenGL(glStore);

            // copy the freshly created face set into the correct accumulator
            for (GLFace face : glStore.faces) {
                GLStore tgt;
                if (face.bd.isTranslucent()) {
                    tgt = translucent;
                } else {
                    tgt = opaque;
                }
                int[] verts = new int[face.vertices.length];
                for (int i = 0; i < verts.length; i++) {
                    verts[i] = tgt.getVertex(glStore.vertices.get(face.vertices[i]));
                }
                tgt.addFace(face.bd, verts);
            }
        }

        dumpSegment(w, opaque);
        w.write(",\n");
        dumpSegment(w, translucent);
        w.write("] // end webgl_segments\n");

        w.close();
    }

    private void maybeFlush(Writer w, GLStore opaque)
        throws IOException
    {
        if (opaque.vertices.size() > 0xff00) {
            dumpSegment(w, opaque);
            w.write(",\n");
            opaque.clear();
        }
    }

    public void dumpSegment(Writer w, GLStore glStore)
        throws IOException
    {
        Collections.sort(glStore.faces);

        w.write("{\n");

        w.write("//"+glStore.vertices.size()+" vertices; "+glStore.faces.size()+" faces\n");

        //
        w.write("// each triple is an X,Y,Z coordinate of a vertex\n");
        w.write("vertex_xyz: [\n");
        for (XYZUV vertex : glStore.vertices) {
            w.write(vertex.x+","+vertex.y+","+vertex.z+",\n");
        }
        w.write("],\n\n");

        //

        w.write("// each pair is an UV texture coordinate of a vertex\n");
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
            if (true) {
                w.write(join(",",
                    // two triangles for OpenGL
                    v3, v0, v2,
                    v0, v1, v2) + ",\n");
                cursor += 6;
            } else {
                // a single quad
                w.write(join(",", v0, v1, v2, v3) + ",\n");
                cursor += 4;

                // XXX quads are not supported by webgl yet
            }
        }
        w.write("],\n\n");

        //

        w.write("texture_extents : [\n");
        for (int i = 0; i < boundaries.size(); i++) {
            UniformFaceTextureExtent te = boundaries.get(i);
            int after  = (i+1<boundaries.size()) ? boundaries.get(i+1).start : cursor;
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
        public final int blockType;

        public TextureForGL(BlockPlusData bd, BlockSide orientation)
        {
            this.textureName = "tex."+bd.blockType+"."+bd.data+"."+orientation.ordinal();
            this.blockType = bd.blockType;
        }

        public TextureForGL(String textureName, int blockType)
        {
            this.textureName = textureName;
            this.blockType = blockType;
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
            return new TextureForGL(bd, orientation);
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

        public boolean isTranslucent()
        {
            if ( blockType == -1) {

                if (true)
                    return textureName.equals("blocks/water_still")
                        || textureName.equals("blocks/water_flow");

                return textureName .startsWith("blocks/potatoes")
                    || textureName.startsWith("blocks/carrots")
                    || textureName.startsWith("blocks/iron_bars")
                    || textureName.startsWith("blocks/ladder")
                    || textureName.startsWith("blocks/leaves")
                    || textureName.startsWith("blocks/double_plant_")
                    || textureName.startsWith("blocks/flower_")
                    || textureName.startsWith("blocks/torch")
                    || textureName.startsWith("blocks/redstone_torch")
                    || textureName.startsWith("blocks/deadbush")
                    || textureName.startsWith("blocks/glass")
                    || textureName.startsWith("blocks/wheat");
            }
            return blockType == 6
                || blockType == 8
                || blockType == 9
                || blockType == 18
                || blockType == 20
                || blockType == 26
                || blockType == 27
                || blockType == 28
                || blockType == 30
                || blockType == 31
                || blockType == 32
                || blockType == 37
                || blockType == 38
                || blockType == 39
                || blockType == 40
                || blockType == 51
                || blockType == 52
                || blockType == 55
                || blockType == 59
                || blockType == 64
                || blockType == 65
                || blockType == 66
                || blockType == 71
                || blockType == 83
                || blockType == 96
                || blockType == 101
                || blockType == 102
                || blockType == 104
                || blockType == 105
                || blockType == 106
                || blockType == 111
                || blockType == 115
                || blockType == 117
                || blockType == 132
                || blockType == 141
                || blockType == 142
                || blockType == 157
                || blockType == 160
                || blockType == 161
                || blockType == 167
                || blockType == 175
                || blockType ==66;
        }
    }

    public static class GLFace
        implements Comparable<GLFace>
    {

        public static final int NVERTS = 4;
        public final TextureForGL bd;
        public final int[] vertices;

        public GLFace(TextureForGL bd, int[] vertices)
        {
            this.bd = bd;
            this.vertices = vertices;
            if (vertices.length != NVERTS)
                throw new IllegalArgumentException("I only do quads");
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
        /**
         * should we check for duplicate vertices and reuse them?  vertex reuse costs CPU and saves memory.
         */
        public boolean reuseVertices=true;

        public GLStore()
        {
            this(true);
        }

        /**
         *
         * @param reuseVertices should we check for duplicate vertices and reuse them?  vertex reuse costs CPU and saves memory.
         */
        public GLStore(boolean reuseVertices)
        {
            this.reuseVertices = reuseVertices;
        }

        public int getVertex(double x, double y, double z, double u, double v)
        {
            return getVertex(new XYZUV(x,y,z, u,v));
        }

        public int getVertex(XYZUV coords)
        {
            if (reuseVertices) {
                for (int i = 0; i < vertices.size(); i++) {
                    XYZUV point3Di = vertices.get(i);
                    if (point3Di.equals(coords))
                        return i;
                }
            }

            int rval = vertices.size();
            vertices.add(coords);
            return rval;
        }

        public void addFace(TextureForGL bd, int... vertices)
        {
            faces.add(new GLFace(bd, vertices));
        }

        public void clear()
        {
            vertices.clear();
            faces.clear();
        }
    }


}
