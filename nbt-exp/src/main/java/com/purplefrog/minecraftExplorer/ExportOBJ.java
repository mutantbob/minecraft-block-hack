package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;
import javafx.geometry.*;

import java.io.*;

/**
 * Export minecraft geometry as a <a href="http://en.wikipedia.org/wiki/Wavefront_.obj_file">Wavefront .OBJ</a> file.
 *

 *
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/4/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExportOBJ
{
    public static CharSequence exportOBJ(BlockVoxels bv, Point3D min, Point3D max, TextureBrain textureBrain, MaterialLib mLib)
    {
        int x0 = (int) Math.floor(min.getX());
        int x9 = (int) Math.ceil(max.getX());
        int y0 = (int) Math.floor(min.getY());
        int y9 = (int) Math.ceil(max.getY());
        int z0 = (int) Math.floor(min.getZ());
        int z9 = (int) Math.ceil(max.getZ());

        WavefrontOBJWriter w = new   WavefrontOBJWriter(mLib);

        for (int x= x0; x<= x9; x++) {
            for (int y=y0; y<=y9; y++) {
                for (int z=z0; z<=z9; z++) {

                    WavefrontOBJWriter.LazyIndex[] verts = {
                        w.lazyIndexForVertex(x, y, z),
                        w.lazyIndexForVertex(x + 1, y, z),
                        w.lazyIndexForVertex(x, y + 1, z),
                        w.lazyIndexForVertex(x + 1, y + 1, z),
                        w.lazyIndexForVertex(x, y, z + 1),
                        w.lazyIndexForVertex(x + 1, y, z + 1),
                        w.lazyIndexForVertex(x, y + 1, z + 1),
                        w.lazyIndexForVertex(x + 1, y + 1, z + 1),
                    };

                    int bt0 = bv.getBlockType(x, y, z);
                    int bts = bv.getBlockType(x, y, z + 1);

                    if (BlockDatabase.tClass(bt0) == BlockDatabase.TransparencyClass.Widget) {
                        String mtlName = textureBrain.getMaterial(bt0, mLib);
                        if (null != mtlName) {
                            addSquare(w, mtlName, verts[0].get(), verts[2].get(), verts[3].get(), verts[1].get());
                            addSquare(w, mtlName, verts[1].get(), verts[3].get(), verts[7].get(), verts[5].get());
                            addSquare(w, mtlName, verts[5].get(), verts[7].get(), verts[6].get(), verts[4].get());
                            addSquare(w, mtlName, verts[4].get(), verts[6].get(), verts[2].get(), verts[0].get());
                            addSquare(w, mtlName, verts[3].get(), verts[2].get(), verts[6].get(), verts[7].get());
                            addSquare(w, mtlName, verts[0].get(), verts[1].get(), verts[5].get(), verts[4].get());
                        }
                    }

                    if (bt0<0)
                        continue;
                    boolean trans0 = BlockDatabase.transparent(bt0);

                    if (bts>=0) {
                        boolean transS = BlockDatabase.transparent(bts);
                        if (trans0 != transS) {
                            addSquarePolarized(textureBrain, w, bt0, trans0, bts,
                                verts[5].get(), verts[7].get(), verts[6].get(), verts[4].get(), false);
                        }
                    }
                    int btu = bv.getBlockType(x, y + 1, z);
                    if (btu>=0) {
                        boolean transU = BlockDatabase.transparent(btu);
                        if (trans0 != transU) {
                            addSquarePolarized(textureBrain, w, bt0, trans0, btu,
                                verts[2].get(), verts[6].get(), verts[7].get(), verts[3].get(), true);
                        }
                    }
                    int bte = bv.getBlockType(x + 1, y, z);
                    if (bte >=0) {
                        boolean transE = BlockDatabase.transparent(bte);
                        if (trans0 != transE) {
                            addSquarePolarized(textureBrain, w, bt0, trans0, bte,
                                verts[1].get(), verts[3].get(), verts[7].get(), verts[5].get(), false);
                        }
                    }
                }
            }
        }
        return w.toString();
    }

    /**
     * we have a square that represents the boundary between block type 1 and block type 2.
     * If we use texture 1 then the vertices will be used in order.
     * If we use texture 2 then the vertices will be reversed.
     */
    public static void addSquarePolarized(TextureBrain textureBrain, WavefrontOBJWriter w, int blockType1, boolean useTexture2, int blockType2, int v1, int v2, int v3, int v4, boolean top)
    {
        if (useTexture2) {
            String mtlName = textureBrain.getMaterial(blockType2, w.mLib, top);
            addSquare(w, mtlName, v4, v3, v2, v1);
        } else {
            String mtlName = textureBrain.getMaterial(blockType1, w.mLib, top);
            addSquare(w, mtlName, v1, v2, v3, v4);
        }
    }

    private static void addSquare(WavefrontOBJWriter w, String mtlName, int v0, int v1, int v2, int v3)
    {
        if (null != mtlName) {
            w.addFaceTexture(
                mtlName,
                new IndexTexcoord(v0, 0, 0),
                new IndexTexcoord(v1, 0, 1),
                new IndexTexcoord(v2, 1, 1),
                new IndexTexcoord(v3, 1, 0)
            );
        } else {
            w.addFace(v0, v1, v2, v3);
        }
    }

    public static void main(String[] argv)
        throws IOException
    {
        File saveDir = new File("/home/thoth/.minecraft/saves/twelve-reloaded");
        MinecraftWorld world = new MinecraftWorld(saveDir);
        File outDir = new File("/var/tmp/twelve");
        File mtlFile = new File(outDir, "blockTextures.mtl");
        CharSequence objPayload = exportOBJ(new BlockVoxels(world),
//            new Point3D(-1100, 0, -100), new Point3D(-900, 256, 100),
            new Point3D(-1010, 0, -100), new Point3D(-990, 256, 100),
//            new Point3D(-20,0,-20), new Point3D(20,256,20),
            new TextureBrain(outDir), new MaterialLib(mtlFile));

        FileWriter w = new FileWriter(new File(outDir, "minecraft.obj"));
        w.write("mtllib "+mtlFile.getName()+"\n\n"
            + objPayload.toString());
        w.close();
    }
}
