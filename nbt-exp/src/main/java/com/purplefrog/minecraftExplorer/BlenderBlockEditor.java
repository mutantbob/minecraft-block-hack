package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/2/13
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class BlenderBlockEditor
    extends RAMBlockEditor
{

    private String ofname;

    public BlenderBlockEditor(String ofname)
    {
        this(ofname, (LowRes)null);
    }

    public BlenderBlockEditor(String ofname, LowRes lowres)
    {
        this.ofname = ofname;
        this.lowRes = lowres;
    }

    public BlenderBlockEditor(String ofname, QueryableBlockEditor editor)
    {
        this.ofname = ofname;
        for (Map.Entry<Point3Di, Combo> base : editor.getCachedSections()) {
            for (Map.Entry<Point3Di, BlockPlusData> en : base.getValue().allBlocks(base.getKey())) {
                setBlock(en.getKey(), en.getValue());
            }
        }
    }


    //

    @Override
    public synchronized void save()
        throws IOException
    {
        int i=1;

        for (Map.Entry<Point3Di, Combo> en : getCachedSections()) {
            Point3Di sectionPos = en.getKey();
            Combo section = en.getValue();

            String payload = dumpSectionAsMesh(sectionPos, section);
            if (null != payload) {
                FileWriter fw = new FileWriter(ofname+"."+i);
                fw.write(payload);
                fw.close();

                i++;
            }
        }
    }

    public void saveCubes()
        throws IOException
    {
        FileWriter fw = new FileWriter(ofname);

        for (Map.Entry<Point3Di, Combo> en : getCachedSections()) {
            Point3Di sectionPos = en.getKey();
            Combo section = en.getValue();


            dumpSectionAsCubes(fw, sectionPos, section);
        }

        fw.close();
    }

    public static void dumpSectionAsCubes(FileWriter fw, Point3Di sectionPos, Combo section)
        throws IOException
    {
        for (int a=0; a<16; a++) {
            for (int b=0; b<16; b++) {
                for (int c=0; c<16; c++) {

                    int x = ((int)sectionPos.x <<4)  +a;
                    int y = ((int)sectionPos.y <<4)  +b;
                    int z = ((int)sectionPos.z <<4)  +c;

                    int bt = section.getBlockType(a, b, c);
                    if (0 != bt) {
                        fw.write("minecraftSetBlock("+x+","+y+","+z+", "+ bt +","+section.getData(a,b,c)+")\n");
                    }
                }
            }
        }
    }

    public String dumpSectionAsMesh(Point3Di sectionPos, Combo section)
        throws IOException
    {
        List<BlenderMeshElement> polys = getBlenderMeshElements(sectionPos, section);
        if (polys.isEmpty())
            return null;

        polys = consolidateFaces(polys);

        StringBuilder rval = new StringBuilder();

        rval.append("sectionFaces = (\n");
        appendJoin(rval, ",\n", polys);
        rval.append(")\n" +
            "buildMinecraftMesh(sectionFaces, " + sectionPos.x + ", " + sectionPos.y + ", " + sectionPos.z + ")\n");
        return rval.toString();
    }

    /**
     *
     * @param polys this gets maimed
     * @return
     */
    public static List<BlenderMeshElement> consolidateFaces(List<BlenderMeshElement> polys)
    {
        List<BlenderMeshElement.FatFace> horizontal = consolidateFacesHorizontal(polys);
        List<BlenderMeshElement.FatFace> vertical = consolidateFacesVertical(polys);

        List<BlenderMeshElement.FatFace> rects = consolidateFatFacesVertical(horizontal);

        ArrayList<BlenderMeshElement> rval = new ArrayList<BlenderMeshElement>();
        rval.addAll(rects);
        rval.addAll(horizontal);
        rval.addAll(vertical);
        rval.addAll(polys);
        return rval;
    }

    public static List<BlenderMeshElement.FatFace> consolidateFatFacesVertical(List<BlenderMeshElement.FatFace> polys)
    {
        ArrayList<BlenderMeshElement.FatFace> vertical;
        {
            vertical = new ArrayList<BlenderMeshElement.FatFace>();
            int cursor=0;
            while (cursor<polys.size()) {
                BlenderMeshElement.FatFace f1_ = polys.get(cursor);


                BlenderMeshElement.FatFace ff = new BlenderMeshElement.FatFace(f1_);

                Point3Di vVector = ff.orientation.vVector;
                while (true) {
                    int x2 = ff.x + ff.dv * vVector.x;
                    int y2 = ff.y + ff.dv * vVector.y;
                    int z2 = ff.z + ff.dv * vVector.z;
                    BlenderMeshElement.FatFace f2 = consumeFatFace(x2, y2, z2,
                        ff.orientation, ff.bt, ff.blockData, polys, ff.du, 1);

                    if (f2==null) {
                        break;
                    }
                    ff.dv++;
                }

                while (true) {
                    int x3 = ff.x - vVector.x;
                    int y3 = ff.y - vVector.y;
                    int z3 = ff.z - vVector.z;
                    BlenderMeshElement.FatFace f2 = consumeFatFace(x3, y3, z3,
                        ff.orientation, ff.bt, ff.blockData, polys, ff.du, 1);

                    if (f2==null) {
                        break;
                    }
                    ff.dv++;
                    ff.x = ff.x - vVector.x;
                    ff.y = ff.y - vVector.y;
                    ff.z = ff.z - vVector.z;
                }

                if (ff.dv!=1) {
                    vertical.add(ff);
                    polys.remove(cursor);
                } else {
                    cursor++;
                }
            }
        }
        return vertical;

    }

    private static BlenderMeshElement.FatFace consumeFatFace(int x, int y, int z, FaceSide orientation, int bt, int blockData, List<BlenderMeshElement.FatFace> faces, int du, int dv)
    {
        for (Iterator<BlenderMeshElement.FatFace> iterator = faces.iterator(); iterator.hasNext(); ) {
            BlenderMeshElement.FatFace face = iterator.next();
            if (face.x == x && face.y == y && face.z == z
                && face.bt == bt && face.blockData == blockData
                && face.orientation == orientation
                && face.du == du && face.dv == dv) {
                iterator.remove();
                return face;
            }
        }

        return null;

    }

    public static List<BlenderMeshElement.FatFace> consolidateFacesVertical(List<BlenderMeshElement> polys)
    {
        ArrayList<BlenderMeshElement.FatFace> vertical;
        {
            vertical = new ArrayList<BlenderMeshElement.FatFace>();
            int cursor=0;
            while (cursor<polys.size()) {
                BlenderMeshElement f1_ = polys.get(cursor);

                if (!(f1_ instanceof BlenderMeshElement.Face)) {
                    cursor++;
                    continue;
                }

                BlenderMeshElement.FatFace ff = new BlenderMeshElement.FatFace((BlenderMeshElement.Face) f1_);

                Point3Di vVector = ff.orientation.vVector;
                while (true) {
                    int x2 = ff.x + ff.dv * vVector.x;
                    int y2 = ff.y + ff.dv * vVector.y;
                    int z2 = ff.z + ff.dv * vVector.z;
                    BlenderMeshElement.Face f2 = consumeFace(x2, y2, z2,
                        ff.orientation, ff.bt, ff.blockData, polys);

                    if (f2==null) {
                        break;
                    }
                    ff.dv++;
                }

                while (true) {
                    int x3 = ff.x - vVector.x;
                    int y3 = ff.y - vVector.y;
                    int z3 = ff.z - vVector.z;
                    BlenderMeshElement.Face f2 = consumeFace(x3, y3, z3,
                        ff.orientation, ff.bt, ff.blockData, polys);

                    if (f2==null) {
                        break;
                    }
                    ff.dv++;
                    ff.x = ff.x - vVector.x;
                    ff.y = ff.y - vVector.y;
                    ff.z = ff.z - vVector.z;
                }

                if (ff.dv!=1) {
                    vertical.add(ff);
                    polys.remove(cursor);
                } else {
                    cursor++;
                }
            }
        }
        return vertical;
    }

    public static List<BlenderMeshElement.FatFace> consolidateFacesHorizontal(List<BlenderMeshElement> polys)
    {
        List<BlenderMeshElement.FatFace> horizontal;
        {
            horizontal = new ArrayList<BlenderMeshElement.FatFace>();
            int cursor=0;
            while (cursor<polys.size()) {
                BlenderMeshElement f1_ = polys.get(cursor);

                if (!(f1_ instanceof BlenderMeshElement.Face)) {
                    cursor++;
                    continue;
                }

                BlenderMeshElement.FatFace ff = new BlenderMeshElement.FatFace((BlenderMeshElement.Face) f1_);

                Point3Di uVector = ff.orientation.uVector;
                while (true) {
                    int x2 = ff.x + ff.du * uVector.x;
                    int y2 = ff.y + ff.du * uVector.y;
                    int z2 = ff.z + ff.du * uVector.z;
                    BlenderMeshElement.Face f2 = consumeFace(x2, y2, z2,
                        ff.orientation, ff.bt, ff.blockData, polys);

                    if (f2==null) {
                        break;
                    }
                    ff.du++;
                }

                while (true) {
                    int x3 = ff.x - uVector.x;
                    int y3 = ff.y - uVector.y;
                    int z3 = ff.z - uVector.z;
                    BlenderMeshElement.Face f2 = consumeFace(x3, y3, z3,
                        ff.orientation, ff.bt, ff.blockData, polys);

                    if (f2==null) {
                        break;
                    }
                    ff.du++;
                    ff.x = ff.x- uVector.x;
                    ff.y = ff.y- uVector.y;
                    ff.z = ff.z- uVector.z;
                }

                if (ff.du!=1) {
                    horizontal.add(ff);
                    polys.remove(cursor);
                } else {
                    cursor++;
                }
            }
        }
        return horizontal;
    }

    public static BlenderMeshElement.Face consumeFace(int x, int y, int z, FaceSide orientation, int bt, int blockData, List<BlenderMeshElement> faces)
    {
        for (Iterator<BlenderMeshElement> iterator = faces.iterator(); iterator.hasNext(); ) {
            BlenderMeshElement me = iterator.next();
            if (me instanceof BlenderMeshElement.Face) {
                BlenderMeshElement.Face face = (BlenderMeshElement.Face) me;
                if (face.x == x && face.y == y && face.z == z
                    && face.bt == bt && face.blockData == blockData
                    && face.orientation == orientation) {
                    iterator.remove();
                    return face;
                }
            }
        }

        return null;
    }

}
