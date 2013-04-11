package com.purplefrog.minecraftExplorer;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/11/13
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateMaze1
{
    public static void main(String[] argv)
        throws IOException
    {
        Maze m = new Maze(30, 5);

        Random r = new Random();
        m.fill(r);

        Maze.EdgeCandidate begin = m.new EdgeCandidate(0, r.nextInt(m.height), 8);
        Maze.EdgeCandidate end = m.new EdgeCandidate(m.width-1, r.nextInt(m.height), 4);
        m.knockDownEdgeWall(begin);
        m.knockDownEdgeWall(end);


        System.out.println(m.textDiagram());



        File saveDir = pickSaveDir();

        BlockEditor editor = new BlockEditor(new MinecraftWorld(saveDir));


        int x0=150;
        int y0=100;
        int z0 = 7;

        int dU=3; // should be at least 2, otherwise there is no space between hedges
        int dV=4;

        int[] corner = { 17,18,18,0};
        int[] wall = {18,18,18,0};
        int[] arch = {0,0,0,0};
        int[] room = {0,0,0,0};
        int floorBT = 2;
        for (int u=0; u<m.width; u++) {
            for (int v=0; v<m.height; v++) {


                for (int a=0; a<dU; a++) {
                    for (int b=0; b<dV; b++) {
                        // floor
                        int wx = x0 + u * dU + a;
                        int wz = z0 + v * dV + b;
                        editor.setBlock(wx, y0, wz, floorBT);
                        if (a>0&&b>0) {
                            buildWallColumn(editor, wx,y0, wz, true , room);
                        }
                    }
                }

                // "south" wall
                for (int a=0; a<dU; a++) {
                    int wx = x0 + u * dU + a;
                    int wz = z0 + v * dV ;

                    buildWallColumn(editor, wx, y0, wz, a==0 || m.hasSouthWall(u, v), a ==0 ? corner : m.hasSouthWall(u,v) ? wall : arch);
                }

                // "west" wall
                for (int a=0; a<dV; a++) {
                    int wx = x0 + u * dU;
                    int wz = z0 + v * dV + a;

                    buildWallColumn(editor, wx, y0, wz, a==0 || m.hasWestWall(u, v), a ==0 ? corner : m.hasWestWall(u,v) ? wall: arch);
                }
            }
        }

        {
            int v = m.height-1;
            int wz = z0 + m.height * dV;
            for (int u=0; u<m.width; u++) {
                for (int a=0; a<dU; a++) {
                    int wx = x0 + u * dU + a;
                    editor.setBlock(wx, y0, wz, floorBT);
                    buildWallColumn((editor), wx,y0, wz, m.hasNorthWall(u,v), a==0?corner:m.hasNorthWall(u,v)?wall:arch);
                }
            }
        }

        {
            int u=m.width-1;
            int wx = x0 + m.width*dU;
            for (int v=0; v<m.height; v++) {
                for (int b=0; b<dV; b++) {
                    int wz = z0+v*dV+b;
                    editor.setBlock(wx,y0, wz, floorBT);
                    buildWallColumn(editor, wx, y0, wz, false, b==0?corner:m.hasEastWall(u,v)?wall:arch);
                }
            }
        }

        editor.relight();

        editor.save();
    }

    public static File pickSaveDir()
    {
        return new File(System.getProperty("user.home"), ".minecraft/saves/menger-5");
    }

    public static void buildWallColumn(BlockEditor editor, int x, int y, int z, boolean hasWall, int[] wallShape)
        throws IOException
    {
        for (int c=0; c< wallShape.length; c++) {
            int bt;
            if (true) {
                bt = wallShape[c];
            } else {
                bt = 0;
            }
            editor.setBlock(x, y+c+1, z, bt);
        }
    }

}
