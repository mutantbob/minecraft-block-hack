package com.purplefrog.minecraftExplorer;

import org.apache.log4j.*;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/12/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class CityScape
{
    private static final Logger logger = Logger.getLogger(CityScape.class);

    public static void main(String[] argv)
        throws IOException
    {
        QueryableBlockEditor editor;
//        editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));

        String outDir = "/tmp/blender-city";
        new File(outDir).mkdir();

        SimpleLowRes lowResLog = new SimpleLowRes(new FileWriter(new File(outDir,
            "/city.lores.py")));
        editor = new BlenderBlockEditor(new File(outDir,"blender.city2.py").getPath(), lowResLog);

        Random rand = new Random(4262);

        streetscape1(editor, lowResLog, rand);

        editor.relight();

        editor.save();


        ExplosionSim.simulateExplosions(editor,rand, outDir);

    }

    public static void streetscape1(BasicBlockEditor editor, SimpleLowRes lowResLog, Random rand)
        throws IOException
    {
        Streetscape ss = new Streetscape(4, 6);


        int x0=200;
        int y0=79;
        int z0 =100;

        int excavationLimit = 8;
        int y1 = y0+excavationLimit+2;

        {
            int y9 = y0 - 5;
            editor.fillCube(0, x0, y9, z0, 200, 250- y9, 200);
        }


        for (int u=0; u+1<ss.nStreets; u++) {
            for (int v=0; v+1<ss.nAvenues; v++) {
                Rectangle r = ss.meadowSouthEastOf(u,v);
                r.x += x0;
                r.y += z0;

                randomBuilding(editor, r, y1+ss.meadow.elevation, rand, lowResLog);

                if (false ) {
                    // lapis rectangle for debugging
                    for (int a=0; a<r.width; a++) {
                        for (int b=0; b<r.height; b++) {
                            editor.setBlock(r.x+a, y0+10, r.y+b, 22);
                        }
                    }
                }
            }
        }

        ss.render(editor, x0, y1, z0);


        {
            FloatingIsland island = new FloatingIsland(ss.xSize(), ss.zSize(), excavationLimit, 4, rand);
            island.render(editor, x0, y0, z0, y1, new FloatingIsland.DoomChipCookie(rand));
        }

        System.out.println(ss.xSize()+"x"+ss.zSize()+"+"+x0+"+"+z0);
    }

    public static void randomBuilding(BlockEditor editor, Rectangle emptyLot, int y0, Random rand, LowResLog lowResLog)
        throws IOException
    {


        Object [] windows = ClipArt.randomBuildingDecor(rand);

        int[] column = (int[]) windows[2];

        int nFloors = (rand.nextInt(250-y0-12)+12) / column.length;

        WindowShape window1 = (WindowShape) windows[0];
        WindowShape window2 = (WindowShape) windows[1];

        int uCells = (emptyLot.width - 2 - 1) / window1.cellWidth;
        int vCells = (emptyLot.height - 2 - 1) / window2.cellWidth;

        SkyScraper1 scraper = new SkyScraper1(uCells, vCells, nFloors, column, window1, window2);

        int dx = (emptyLot.width - scraper.xDimension())/2;
        int dz = (emptyLot.height - scraper.zDimension())/2;

        int x1 = emptyLot.x + dx;
        int z1 = emptyLot.y + dz;
        scraper.render(editor, x1, y0, z1);

        lowResLog.log(x1,y0,z1, uCells, vCells, nFloors, column, window1, window2);
    }

    public interface LowResLog
    {

        void log(int x, int y, int z,
                 int uCells, int vCells, int nFloors,
                 int[] column, WindowShape window1, WindowShape window2);

    }

    public static class SimpleLowRes
        implements LowResLog, BlenderBlockEditor.LowRes
    {
        Writer w;

        public SimpleLowRes(Writer w)
        {
            this.w = w;
        }

        @Override
        public void log(int x, int y, int z, int uCells, int vCells, int nFloors, int[] column, WindowShape window1, WindowShape window2)
        {
            String msg = "buildingPretender("+x+","+y+","+z+", "
                +uCells+","+vCells+","+nFloors+", "+column.length+","+window1.cellWidth+","+window2.cellWidth+")\n";
            System.out.print(msg);
            try {
                w.write(msg);
                w.flush();
            } catch (IOException e) {
                logger.warn("IO error ", e);
            }
        }

        @Override
        public void logFillCube(BlockTemplate template, int x1, int y1, int z1, int x2, int y2, int z2)
        {
            StringBuilder msg = new StringBuilder( "fillCube(");
            BlockPlusData block = template.getBlock(0, (y2-y1-1)%template.elevation, 0);
            BlenderBlockEditor.appendJoin(msg, ", ", block.blockType, x1, y1, z1, x2, y2, z2);
            msg.append(")\n");

            try {
                w.write(msg.toString());
                w.flush();
            } catch (IOException e) {
                logger.warn("IO error", e);
            }
        }
    }
}
