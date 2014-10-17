package com.purplefrog.minecraft.view3d;

import com.jogamp.common.nio.*;
import com.purplefrog.minecraftExplorer.*;

import java.nio.*;
import java.util.*;

/**
 * Created by thoth on 10/17/14.
 */
public class GLBufferSet
{
    /**
     * A buffer of xyz triples usable by
     * {@link javax.media.opengl.GL2#glVertexPointer(int, int, int, java.nio.Buffer)}.
     *
     * <pre>
     * gl2.glEnable(GL2.GL_VERTEX_ARRAY);
     * gl.glVertexPointer(3, GL.GL_FLOAT, 0, bs.vertices);
     * </pre>
     */
    public FloatBuffer vertices;
    /**
     * A buffer of uv pairs usable by
     * {@link javax.media.opengl.GL2#glTexCoordPointer(int, int, int, java.nio.Buffer)}.
     *
     * <pre>
     * gl2.glEnable(GL2.GL_TEXTURE_COORD_ARRAY);
     * gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, bs.vertices);
     * </pre>
     */
    public FloatBuffer uvs;
    /**
     * a set of buffers, segregated by texture name, usable by
     * {@link javax.media.opengl.GL2#glDrawElements(int, int, int, java.nio.Buffer)}.
     *
     * <pre>
     * quads = bs.perTexture.get(tname);
     * Texture t = retrieveTexture(tname);
     * t.bind(gl2);
     * gl2.glDrawElements(GL2.GL_QUADS, quads.limit(), GL2.GL_UNSIGNED_SHORT, quads);
     * </pre>
     */
    public Map<String, ShortBuffer> perTexture = new TreeMap<String, ShortBuffer>();


    public GLBufferSet(ExportWebGL.GLStore glStore)
    {
        vertices = Buffers.newDirectFloatBuffer(glStore.vertices.size()*3);
        uvs = Buffers.newDirectFloatBuffer(glStore.vertices.size()*2);

        for (XYZUV vertex : glStore.vertices) {
            vertices.put((float)vertex.x);
            vertices.put((float)vertex.y);
            vertices.put((float)vertex.z);

            uvs.put((float)vertex.u);
            uvs.put((float)vertex.v);
        }

        vertices.flip();
        uvs.flip();

        Map<String, List<ExportWebGL.GLFace>> facesPerTexture =
            Segregator.segregate(glStore.faces, new Segregator.KeyExtractor<ExportWebGL.GLFace, String>() {
                @Override
                public String getKey(ExportWebGL.GLFace obj)
                {
                    return obj.bd.textureName;
                }
            });


        for (Map.Entry<String, List<ExportWebGL.GLFace>> en : facesPerTexture.entrySet()) {
            perTexture.put(en.getKey(), vertexIndicesAsBuffer(en.getValue()));
        }
    }

    public static ShortBuffer vertexIndicesAsBuffer(List<ExportWebGL.GLFace> faces)
    {
        ShortBuffer indices = Buffers.newDirectShortBuffer(faces.size() * ExportWebGL.GLFace.NVERTS);
        for (ExportWebGL.GLFace glFace : faces) {
            for (int vertex : glFace.vertices) {
                indices.put((short) vertex);
            }
        }
        indices.flip();
        return indices;
    }
}
