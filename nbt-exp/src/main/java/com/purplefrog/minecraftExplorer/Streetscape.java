package com.purplefrog.minecraftExplorer;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/11/13
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class Streetscape
{
    public final RoadSpec[] streetSegments;
    public final int[] streetWidths;
    public final int[] avenueWidths;
    public final RoadSpec[] avenueSegments;
    public final int nStreets;
    public final int nAvenues;
    public  BlockPlusData cobblestoneSlab =new BlockPlusData(44, 3);
    public BlockTemplate meadow = new BlockTemplate(1,1,2, 3,2);


    public Streetscape(int nStreets, int nAvenues)
    {
        BlockTemplate sidewalk = new BlockTemplate(1,2,2, 3, 3, 43, 43);
        BlockTemplate pavement = new BlockTemplate(1, 1, 2, new BlockPlusData(3), cobblestoneSlab);

        this.nStreets = nStreets;
        this.nAvenues = nAvenues;

        streetWidths = new int[nStreets];
        Arrays.fill(streetWidths, 5);

        streetSegments = new RoadSpec[nStreets * (nAvenues-1)];
        Arrays.fill(streetSegments, new RoadSpec(5, pavement, sidewalk, sidewalk));

        avenueWidths = new int[nAvenues];
        Arrays.fill(avenueWidths, 3);

        avenueSegments = new RoadSpec[(nStreets-1)*nAvenues];
        Arrays.fill(avenueSegments, new RoadSpec(3, pavement, sidewalk, sidewalk) );
    }

    public void randomClosures(int count)
    {
        Random rand = new Random();
        for (int i=0; i<count; i++) {
            int close = rand.nextInt(streetSegments.length + avenueSegments.length);

            if (close<streetSegments.length) {
                streetSegments[close] = null;
            } else {
                avenueSegments[close-streetSegments.length] = null;
            }
        }
    }

    public void render(BasicBlockEditor editor, int x0, int y0, int z0)
        throws IOException
    {
//        for (int u=0; u<nStreets; u++) {
//            for (int v=0; v<nAvenues; v++) {
//                drawIntersection(editor, u, v, x0+xForIntersection(u), y0, z0+zForIntersection(v));
//            }
//        }

        for (int u=0; u<nStreets; u++) {
            for (int v=0; v<nAvenues-1; v++) {
                RoadSpec seg = streetSegments[u + v * nStreets];
                if (seg!=null) {
                    drawStreet(editor, u, v, x0+xForIntersection(u), y0, z0+zForIntersection(v), z0+zForIntersection(v+1), seg);
                }
            }
        }
        for (int u=0; u<nStreets-1; u++) {
            for (int v=0; v<nAvenues; v++) {
                RoadSpec seg = avenueSegments[u + v * (nStreets - 1)];
                if (seg!=null) {
                    drawAvenue(editor, u, v, x0+xForIntersection(u), x0+xForIntersection(u+1), y0, z0+zForIntersection(v), seg);
                }
            }
        }

        {
            int farEast = xSize();
            int farSouth = zSize();
            for (int u=0; u<=nStreets; u++) {
                for (int v=0; v<=nAvenues; v++) {
                    RoadSpec west;
                    RoadSpec east;
                    if (v>0 && v<nAvenues) {
                        west = u<1 ? null : streetSegments[u-1 +(v-1)*nStreets];
                        east = u<nStreets ? streetSegments[u +(v-1)*nStreets] : null;
                    } else {
                        west = east = null;
                    }
                    RoadSpec north;
                    RoadSpec south;
                    if (u>0&&u<nStreets) {
                        north = v<1 ? null : avenueSegments[u-1 + (v-1)*(nStreets-1)];
                        south = v<nAvenues ? avenueSegments[u-1 + v*(nStreets-1)] : null;
                    } else {
                        north = south = null;
                    }
                    int x1 = x0 + ( u<1 ? 0 : xOfEastSideOfStreet(west, u - 1));
                    int x2 = x0 + (u < nStreets ? xOfWestSideOfStreet(east, u) : farEast);
                    int z1 = z0 + (v<1 ? 0 : zOfSouthSideOfAvenue(north, v - 1));
                    int z2 = z0 + (v < nAvenues ? zOfNorthSideOfAvenue(south, v) : farSouth);

                    System.out.println("block(" +u+ "," + v + ") " + x1 + ".." + x2 + ", " +y0+", "+z1+".."+z2+"  n="+north+" s="+south+" e="+east+" w="+west);


                    editor.drawBorderedRectangle(x1, y0, z1, x2, z2,
                        north == null ? null : north.side1,
                        south == null ? null : south.side2,
                        east == null ? null : east.side1,
                        west == null ? null : west.side2, meadow);
                }
            }
        }
    }

    public int zSize()
    {
        return zForIntersection(nAvenues - 1) + 10;  // XXX
    }

    public int xSize()
    {
        return xForIntersection(nStreets-1) + 10;  // XXX
    }

    public int zOfNorthSideOfAvenue(RoadSpec avenue, int v)
    {
        return zForIntersection(v) - (null== avenue ? 0 : avenue.width/2);
    }

    public int zOfSouthSideOfAvenue(RoadSpec avenue, int v1)
    {
        return zForIntersection(v1) + (avenue ==null ? 0 : avenue.width - avenue.width/2);
    }

    public int xOfWestSideOfStreet(RoadSpec street, int u)
    {
        return xForIntersection(u) - (null== street ? 0 : street.width/2);
    }

    public int xOfEastSideOfStreet(RoadSpec street, int u1)
    {
        return xForIntersection(u1) + (null == street ? 0 : street.width -street.width/2);
    }

    public Rectangle meadowSouthEastOf(int u, int v)
    {
        RoadSpec west = streetSegments[u   + v*nStreets];
        RoadSpec east = streetSegments[u+1 + v*nStreets];
        RoadSpec north = avenueSegments[u + v*(nStreets-1)];
        RoadSpec south = avenueSegments[u + (v+1)*(nStreets-1)];
        int x1 = xOfEastSideOfStreet(west, u) + west.side1.depth;
        int x2 = xOfWestSideOfStreet(east, u+1) - east.side2.depth;
        int z1 = zOfSouthSideOfAvenue(north, v) + north.side1.depth;
        int z2 = zOfNorthSideOfAvenue(south, v+1) -south.side2.depth;

        return new Rectangle(x1,z1, x2-x1, z2-z1);
    }

    public void drawAvenue(BasicBlockEditor editor, int u, int v, int x1, int x2, int y, int z, RoadSpec seg)
        throws IOException
    {
        if (true) {
            int z1 = z - seg.width/2;
            int z2 = z1+seg.width;
            editor.fillCubeByCorners(seg.pavement.transpose(), x1, y, z1, x2, y + seg.pavement.elevation, z2);
        } else {
            for (int x=x1; x<x2; x++) {
                for (int b=0; b<avenueWidths[v]; b++) {
                    editor.setBlock(x, y, z + b, cobblestoneSlab);
                }
            }
        }
    }

    public void drawStreet(BasicBlockEditor editor, int u, int v, int x, int y, int z1, int z2, RoadSpec seg)
        throws IOException
    {
        int x1 = x - seg.width/2;
        int x2 = x1 + seg.width;
        editor.fillCubeByCorners(seg.pavement, x1, y, z1, x2, y + seg.pavement.elevation, z2);

    }

    public void drawIntersection(BlockEditor editor, int u, int v, int x, int y, int z)
        throws IOException
    {
        for (int a=0; a<streetWidths[u]; a++) {
            for (int b=0; b<avenueWidths[v]; b++) {
                editor.setBlock(x+a,y,z+b, cobblestoneSlab);
            }
        }
    }

    public int zForIntersection(int v)
    {
        return 8+30*v; // XXX
    }

    public int xForIntersection(int u)
    {
        return 11+40*u; // XXX
    }


    public static void main(String[] argv)
        throws IOException
    {
        File saveDir = WorldPicker.pickSaveDir();

        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(saveDir));

        Streetscape q = new Streetscape(3, 4);

        int x0=200;
        int y0=80;
        int z0 =100;

        editor.fillCube(0, x0, 70, z0, 200, 150, 200);

//        q.randomClosures(4);

        q.render(editor, x0, y0, z0);

        editor.relight();

        editor.save();
    }


    public static class RoadSpec
    {
        public int width;
        public BlockTemplate pavement;
        public BlockTemplate side1;
        public BlockTemplate side2;

        public RoadSpec(int width, BlockTemplate pavement, BlockTemplate side1, BlockTemplate side2)
        {
            this.width = width;
            this.pavement = pavement;
            this.side1 = side1;
            this.side2 = side2;
        }
    }
}
