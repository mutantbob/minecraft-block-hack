package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/9/13
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckerTube
{

    public static void main(String[] argv)
        throws IOException
    {

        File saveDir = GenerateMaze1.pickSaveDir();

        BlockEditor editor = new BlockEditor(new MinecraftWorld(saveDir));

        int ry=12;
        int rx=20;
        double thick = 1.6;

        int l = 0;


        int startX=100;
        int startY=100;
        int startZ=0;

        int du = 5;
        int nr = 5;

        for (int z=0; z<50; z++) {
            for (int y=(int) Math.floor(-ry-thick); y<= Math.ceil(ry+thick); y++) {
                for (int x=(int)Math.floor(-rx-thick); x<= Math.ceil(rx+thick); x++) {
                    double r1 = Math.sqrt(x*x + y*y);
                    double x_ = x/(double)rx;
                    double y_ = y/(double)ry;
                    double r2 = Math.sqrt(x_*x_ + y_*y_);

                    double theta = Math.atan2(y_, x_);

                    int u = z/du;
                    int v = (int) Math.floor(theta/(Math.PI+0.01) *nr);

                    int bt;
                    boolean checker = ((u + v) & 1) == 0;
                    if (thick >= Math.abs( r1 - r1/r2 )) {
//                        bt = checker ? 85:101;
//                        bt = checker ? 98:45;
                        bt = checker?98:0;
                    } else {
                        bt = 0;
                    }

                    editor.setBlock(x+startX, y+startY, z+startZ, bt);
                }
            }
        }

        editor.relight();

        editor.save();

    }
}
