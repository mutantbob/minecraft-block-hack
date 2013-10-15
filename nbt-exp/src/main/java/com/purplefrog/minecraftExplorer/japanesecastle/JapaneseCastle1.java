package com.purplefrog.minecraftExplorer.japanesecastle;

import com.purplefrog.minecraftExplorer.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 9/30/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class JapaneseCastle1
{

    public static void main(String[] argv)
        throws IOException
    {

        File f = WorldPicker.pickSaveDir();

        MinecraftWorld world = new MinecraftWorld(f);

        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x0 = -500;
        int y0 = 150;
        int z0 = 240;
        int x9 = x0+100;
        int y9 = y0+100;
        int z9 = z0+100;

        GeometryTree air = new GeometryTree.Solid(0);
        int y2 = y0 + 2;
        GeometryTree ground = new GTRectangle(new Point3Di(x0,y0, z0), new Point3Di(x9, y2, z9), new GeometryTree.Solid(2), null);

        int z1 = z0 + 13;
        int z2 = z0 + 60;

        int x1 = x0 + 13;
        int x3 = x0 + 80;

        int x2 = (x1+x3)/2;

        GeometryTree castle = new GTSequence(
            farms(x1+8, y2, z1+8, x3-8, z2-12),
            ground,
            keep(x2, y2, z2),
            gatehouse(x2, y2, z1),
            tower3(x1, y2, z1),
            tower3(x3, y2, z1),
            tower3(x1, y2, z2),
            tower3(x3, y2, z2),
            walls(x1, y2, z1, x3, y2 + 3, z2),
            air);


        Bounds3Di bounds = new Bounds3Di(x0, y0, z0, x9, y9, z9);
        editor.apply(castle, bounds);

        String report = BasicBlockEditor.billOfMaterials(castle, bounds);
        System.out.println(report);

        editor.relight();
        editor.save();
    }

    public static GeometryTree farms(int x1, int y, int z1, int x2, int z2)
    {
        int padding =2;
        int cols = ((x2-x1)+padding)/(9+padding);
        int rows = ((z2-z1)+padding)/(9+padding);

        List<GeometryTree> rval = new ArrayList<GeometryTree>();
        for (int u=0; u<cols; u++) {
            for (int v=0; v<rows; v++) {
                int x = x1 + (u*2+1)*(x2-x1+padding)/(cols*2)  - padding/2;
                int z = z1 + (v*2+1)*(z2-z1+padding)/(rows*2)  - padding/2;
                rval.add(new Farm(x,y,z));
            }
        }

        return new GTSequence(
            rval
//            new Farm(x1+4,y,z1+4)
        );
    }

    public static GeometryTree walls(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return new GTSequence(
            frontWall(x1-2, y1, z1, x1+2, y2, z2),
            frontWall(x2-2, y1, z1, x2+2, y2, z2),
            frontWall(x1, y1, z1-2, x2, y2, z1+2),
            frontWall(x1, y1, z2-2, x2, y2, z2+2)

        );
    }

    public static GeometryTree frontWall(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        int thick = 1;
        GeometryTree inner = new GTRectangle(new Point3Di(x1+thick, y1, z1+thick),
            new Point3Di(x2-thick, y2-thick, z2-thick),
            new GeometryTree.Solid(0),
            new GeometryTree.Solid(1));
        return new GTRectangle(new Point3Di(x1, y1, z1), new Point3Di(x2, y2, z2), inner, null);
    }

    public static GeometryTree tower1(int cx, int y0, int cz)
    {
        int y1 = y0 + 8;

        GeometryTree lower = towerPart(cx, y0, cz, y1, 11, 2, null);
        GeometryTree upper = towerPart(cx, y0, cz, y1+8, 9, 2, null);
        return new GTSequence(upper, lower);
    }

    public static GeometryTree tower2(int cx, int y0, int cz)
    {
        int y1 = y0 + 8;

        GeometryTree lower = towerPart(cx, y0, cz, y1, 9, 2, null);
        GeometryTree upper = towerPart(cx, y0, cz, y1+8, 7, 2, null);
        return new GTSequence(upper, lower);
    }

    public static GeometryTree tower3(int cx, int y0, int cz)
    {
        int y1 = y0 + 8;
        int y2 = y1 + 8;
        int y3 = y2 + 6;

        GeometryTree lower = towerPart(cx, y0, cz, y1, 8, 2, null);
        GeometryTree middle = towerPart(cx, y1, cz, y2, 6, 2, null);
        GeometryTree upper = towerPart(cx, y2, cz, y3, 4, 1, null);
        return new GTSequence(upper, middle, lower);
    }

    public static GeometryTree keep(int cx, int y0, int cz)
    {
        int y1 = y0 + 6;
        int y2 = y1 + 6;
        int y3 = y2 + 6;

        int y4 = y3 + 4;
        int y5 = y4 + 6;
        int y6 = y5 + 5;
        int y7 = y6 + 6;

        int dog=8;

        int w1 = 15;

        return new GTSequence(
            towerPart(cx-dog, y6, cz, y7, 3, 1, null),
            towerPart(cx+dog, y6, cz, y7, 3, 1, null),
            towerPart(cx-dog, y5, cz, y6, 4, 1, null),
            towerPart(cx+dog, y5, cz, y6, 4, 1, null),
            towerPart(cx-dog, y4, cz, y5, 6, 2, null),
            towerPart(cx+dog, y4, cz, y5, 6, 2, null),
            towerPart2(cx, y3, cz, y4, w1, 8, 2, null),
            towerPart2(cx, y2, cz, y3, w1+1, 9, 2, null),
            towerPart2(cx, y1, cz, y2, w1+2, 10, 2, null),
            towerPart2(cx, y0, cz, y1, w1+3, 11, 2, null));
    }

    public static GeometryTree gatehouse(int cx, int y0, int cz)
    {
        int y1 = y0 + 6;

        int w1 = 13;
        int w2 = 7;

        GeometryTree roof1a = new JapaneseRoof(w1, w2, w1-3, w2-1, true, false, w1-3, w2)
            .new GT(cx, y1, cz, (GeometryTree) null);

        GeometryTree wall1a = hollowV2(cx, y0, cz, y1, 2, roof1a, w1 - 2, w2 - 2);

        return new GTSequence(roof1a, wall1a);
    }

    public static GeometryTree towerPart(int cx, int y0, int cz, int y1, int r1, int inset, GeometryTree chain)
    {
        GeometryTree roof1a = new JapaneseRoof(r1, r1).new GT(cx, y1, cz, chain);

        GeometryTree wall1a = hollowV2(cx, y0, cz, y1, inset, roof1a, r1 - inset, r1 - inset);

        return new GTSequence(roof1a, wall1a);
    }

    public static GeometryTree towerPart2(int cx, int y0, int cz, int y1, int rx, int rz, int inset, GeometryTree chain)
    {
        GeometryTree roof1a = new JapaneseRoof(rx, rz).new GT(cx, y1, cz, chain);

        GeometryTree wall1a = hollowV2(cx, y0, cz, y1, inset, roof1a, rx - inset, rz - inset);

        return new GTSequence(roof1a, wall1a);
    }

    private static GeometryTree hollowV2(int cx, int y0, int cz, int y1, int inset, GeometryTree outside, int rx2, int rz2)
    {
        return hollowV(new Point3Di(cx - rx2, y0, cz - rz2),
            new Point3Di(cx + rx2, y1+inset/2, cz + rz2), 1,
            new GeometryTree.Solid(0), new GeometryTree.Solid(1), outside);
    }

    public static GeometryTree hollowV(Point3Di min, Point3Di max, int thick, GeometryTree core, GeometryTree wall, GeometryTree outer)
    {
        GeometryTree secondary = new GTRectangle(new Point3Di(min.x+thick, min.y, min.z+thick),
            new Point3Di(max.x-thick, max.y, max.z-thick),
            core, wall);

        return new GTRectangle(min, max, secondary, outer);

    }

    public static class RoofTest
    {
        public static void main(String[] argv)
            throws IOException
        {

            File f = WorldPicker.pickSaveDir();

            MinecraftWorld world = new MinecraftWorld(f);

            BasicBlockEditor editor = new AnvilBlockEditor(world);

            if (true) {
                doOne(editor, -460, 150, 255, 13, 9, 4, 6);
                doOne(editor, -495, 150, 255, 12, 9, 4, 6);
                doOne(editor, -460, 150, 280, 13, 10, 4, 6);
                doOne(editor, -495, 150, 280, 12, 10, 4, 6);
            } else {
                doOne2(editor, -460, 150, 255, 13, 9, true, true);
                doOne2(editor, -495, 150, 255, 13, 9, true, false);
                doOne2(editor, -460, 150, 280, 13, 9, false, true);
                doOne2(editor, -495, 150, 280, 13, 9, false, false);

            }
            editor.relight();
            editor.save();

            System.out.println("done");
        }

        private static void doOne(BasicBlockEditor editor, int cx, int y0, int cz, int rx, int rz, int rx2, int rz2)
            throws IOException
        {
            JapaneseRoof roof = new JapaneseRoof(rx, rz, rx2, rz2, true, false, 7, 5);
            Bounds3Di bounds = roof.getBounds(cx, y0, cz).outset(1, 0, 1);
            bounds.y1 = 200;
            editor.apply(roof.new GT(cx, y0, cz), bounds);
        }

        private static void doOne2(BasicBlockEditor editor, int cx, int y0, int cz, int rx, int rz, boolean gableX, boolean gableZ)
            throws IOException
        {
            StairRoof roof = new StairRoof(rx, rz, gableX, gableZ);
            Bounds3Di bounds = roof.getBounds(cx, y0, cz).outset(1, 0, 1);
            bounds.y1 = 200;
            editor.apply(roof.new GT(cx, y0, cz), bounds);
        }
    }
}
