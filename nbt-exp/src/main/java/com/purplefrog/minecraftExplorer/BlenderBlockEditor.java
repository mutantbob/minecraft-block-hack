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
    extends QueryableBlockEditor
{
    Map<Point3Di, Combo> sections = new HashMap<Point3Di, Combo>();

    private String ofname;

    public LowRes lowRes;

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


    public synchronized void setBlock(Point3Di loc, BlockPlusData block)
    {
        setBlock((int)loc.x, (int)loc.y, (int)loc.z, block);
    }

    @Override
    public synchronized void setBlock(int x, int y, int z, BlockPlusData block)
    {

        Point3Di cooked = cook(x, y, z);

        Combo section;

        if (block.blockType==0) {
            section = getOrCreateSection(cooked);
            if (null==section)
                return; // don't create for air

        } else {
            section = getOrCreateSection(cooked);

        }

        section.put(cooked, x, y, z, block);
    }

    @Override
    public void fillCubeByCorners(BlockTemplate template, int x1, int y1, int z1, int x2, int y2, int z2)
        throws IOException
    {
        super.fillCubeByCorners(template, x1, y1, z1, x2, y2, z2);

        if (null != lowRes)
            lowRes.logFillCube(template, x1, y1, z1, x2,y2,z2);
    }

    @Override
    public void drawBorderedRectangle(int x1, int y0, int z1, int x2, int z2, BlockTemplate north, BlockTemplate south, BlockTemplate east, BlockTemplate west, BlockTemplate meadow)
        throws IOException
    {
        super.drawBorderedRectangle(x1, y0, z1, x2, z2, north, south, east, west, meadow);

        lowRes.logFillCube(meadow, x1, y0, z1, x2, y0+meadow.elevation, z2);
    }

    //

    @Override
    public Combo getOrCreateSection(Point3Di cooked)
    {
        Combo rval = getSection(cooked);
        if (rval==null) {
            rval = new Combo();
            sections.put(cooked, rval);
        }

        return rval;
    }

    public Combo getSection(Point3Di cooked)
    {
        return sections.get(cooked);
    }

    public static Point3Di cook(int x, int y, int z)
    {
        return new Point3Di(x>>4, y>>4, z>>4);
    }

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

    @Override
    public Set<Map.Entry<Point3Di, Combo>> getCachedSections()
    {
        return sections.entrySet();
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
        List<BlenderMeshElement> polys = new ArrayList<BlenderMeshElement>();
        for (int a=0; a<16; a++) {
            for (int b=0; b<16; b++) {
                for (int c=0; c<16; c++) {

                    int x = ((int)sectionPos.x <<4)  +a;
                    int y = ((int)sectionPos.y <<4)  +b;
                    int z = ((int)sectionPos.z <<4)  +c;

                    int bt = section.getBlockType(a, b, c);
                    if (0 == bt)
                        continue; // skip air

                    int blockData = section.getData(a, b, c);

                    String locString = x + "," + y + "," + z;
                    BlockDatabase.TransparencyClass transparencyClass = BlockDatabase.tClass(bt);
                    if (transparencyClass == BlockDatabase.TransparencyClass.Widget ||
                        transparencyClass == BlockDatabase.TransparencyClass.OpaqueFunky) {
                        BlenderMeshElement emitter = blenderCommandForFunky(x, y, z, bt, blockData);
                        polys.add(emitter);
                    } else {
                        if (BlockDatabase.tClass(getBlockType(x,y+1,z))!= transparencyClass) {
                            polys.add(new BlenderMeshElement.Face(x,y,z, bt, blockData, FaceSide.TOP));
                        }
                        if (BlockDatabase.tClass(getBlockType(x,y-1,z))!= transparencyClass) {
                            polys.add(new BlenderMeshElement.Face(x,y,z, bt, blockData, FaceSide.BOTTOM));
                        }
                        if (BlockDatabase.tClass(getBlockType(x+1,y,z))!= transparencyClass) {
                            polys.add(new BlenderMeshElement.Face(x,y,z, bt, blockData, FaceSide.EAST));
                        }
                        if (BlockDatabase.tClass(getBlockType(x-1,y,z))!= transparencyClass) {
                            polys.add(new BlenderMeshElement.Face(x,y,z, bt, blockData, FaceSide.WEST));
                        }
                        if (BlockDatabase.tClass(getBlockType(x,y,z-1))!= transparencyClass) {
                            polys.add(new BlenderMeshElement.Face(x,y,z, bt, blockData, FaceSide.NORTH));
                        }
                        if (BlockDatabase.tClass(getBlockType(x,y,z+1))!= transparencyClass) {
                            polys.add(new BlenderMeshElement.Face(x,y,z, bt, blockData, FaceSide.SOUTH));
                        }
                    }
                }
            }
        }
        if (polys.isEmpty())
            return null;

        StringBuilder rval = new StringBuilder();

        rval.append("sectionFaces = (\n");
        appendJoin(rval, ",\n", polys);
        rval.append(")\n" +
            "buildMinecraftMesh(sectionFaces, " + sectionPos.x + ", " + sectionPos.y + ", " + sectionPos.z + ")\n");
        return rval.toString();
    }

    public BlenderMeshElement blenderCommandForFunky(int x, int y, int z, int bt, int blockData)
    {
        if (bt== BlockDatabase.BLOCK_TYPE_GLASS_PANE
            || bt == BlockDatabase.BLOCK_TYPE_IRON_BARS) {
            int orientation = paneOrientation(x,y,z);
            return new BlenderMeshElement.Pane(x,y,z,bt,blockData, orientation);
        } else {
            return new BlenderMeshElement.Widget(x,y,z,bt,blockData);
        }
    }

    /**
     * we have something like a glass pane or iron bars at x,y,z, what orientation should we draw it in
     * @return bit mask for quadrants 1=north, 2=south, 4=east, 8=west
     */
    public int paneOrientation(int x, int y, int z)
    {
        boolean north = canAnchorPane(getBlockType(x, y, z - 1));
        boolean south = canAnchorPane(getBlockType(x, y, z + 1));
        boolean east = canAnchorPane(getBlockType(x + 1, y, z));
        boolean west = canAnchorPane(getBlockType(x - 1, y, z));


        return (north ? 1:0)
            | (south ? 2:0)
            | (east ? 4:0)
            | (west ? 8:0);
    }

    public static boolean canAnchorPane(int bt)
    {
        BlockDatabase.TransparencyClass transparencyClass = BlockDatabase.tClass(bt);
        return transparencyClass!= BlockDatabase.TransparencyClass.Transparent &&
            transparencyClass != BlockDatabase.TransparencyClass.Water;
    }

    public static void appendJoin(StringBuilder accum, String separator, Object... strings)
    {
        appendJoin(accum, separator, Arrays.asList(strings));
    }

    public static void appendJoin(StringBuilder accum, String separator, Iterable<?> strings)
    {
        boolean first = true;
        for (Object o : strings) {
            if (first) {
                first = false;
            } else {
                accum.append(separator);
            }
            accum.append(o);
        }
    }

    @Override
    public int getBlockType(int x, int y, int z)
    {
        Point3Di cooked = cook(x, y, z);
        Combo c = getSection(cooked);

        if (c ==null)
            return -1;

        return c.getBlockType(cooked, x,y,z);
    }

    @Override
    public BlockPlusData getBlockData(int x, int y, int z)
    {
        Point3Di cooked = cook(x, y, z);
        Combo c = getSection(cooked);

        if (c ==null)
            return null;

        return c.get(x, y, z);
    }

    @Override
    public void relight()
    {
        // don't care
    }

    @Override
    public Iterable<Map.Entry<Point3Di, BlockPlusData>> allVoxels()
    {
        return new Iterable<Map.Entry<Point3Di, BlockPlusData>>()
        {
            @Override
            public Iterator<Map.Entry<Point3Di, BlockPlusData>> iterator()
            {
                return new MyIterator();
            }
        };
    }

    public static class Combo
    {
        ByteCube blockType = new ByteCube();
        ByteCube data = new ByteCube();

        public void put(Point3Di cooked, int x_, int y_, int z_, BlockPlusData block)
        {
            int x = x_-((int)cooked.x<<4);
            int y = y_-((int)cooked.y<<4);
            int z = z_-((int)cooked.z<<4);

            blockType.set(x,y,z, block.blockType);
            data.set(x,y,z, block.data);
        }

        public int getBlockType(Point3Di cooked, int x_, int y_, int z_)
        {
            int x = x_-((int)cooked.x<<4);
            int y = y_-((int)cooked.y<<4);
            int z = z_-((int)cooked.z<<4);

            return blockType.get(x,y,z);
        }

        public int getBlockType(int x, int y, int z)
        {
            return blockType.get(x,y,z);
        }

        public int getData(int x, int y, int z)
        {
            return data.get(x, y, z);
        }

        public Iterator<Map.Entry<Point3Di, BlockPlusData>> allBlocksIter(Point3Di base)
        {
            return new SectionVoxelIterator(base, blockType,  data);
        }

        public Iterable<Map.Entry<Point3Di, BlockPlusData>> allBlocks(final Point3Di base)
        {
            return new Iterable<Map.Entry<Point3Di, BlockPlusData>>()
            {
                @Override
                public Iterator<Map.Entry<Point3Di, BlockPlusData>> iterator()
                {
                    return new SectionVoxelIterator(base, blockType,  data);
                }
            };
        }

        public BlockPlusData get(int x, int y, int z)
        {
            return new BlockPlusData(blockType.get(x,y,z), data.get(x,y,z));
        }

    }

    public interface LowRes
    {
        void logFillCube(BlockTemplate template, int x1, int y1, int z1, int x2, int y2, int z2);
    }

    public class MyIterator
        implements Iterator<Map.Entry<Point3Di, BlockPlusData>>
    {

        private Iterator<Map.Entry<Point3Di, Combo>> alpha = sections.entrySet().iterator();
        private Iterator<Map.Entry<Point3Di, BlockPlusData>> beta=null;

        @Override
        public boolean hasNext()
        {
            while (beta==null || !beta.hasNext()) {
                if (alpha.hasNext()) {
                    Map.Entry<Point3Di, Combo> q = alpha.next();
                    beta = q.getValue().allBlocks(q.getKey()).iterator();
                } else {
                    return false;
                }
            }

            return true;
        }

        @Override
        public Map.Entry<Point3Di, BlockPlusData> next()
        {
            if (hasNext())
                return beta.next();

            throw new NoSuchElementException();
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
