package com.purplefrog.minecraftExplorer.japanesecastle;

import com.purplefrog.minecraftExplorer.*;

import java.io.*;

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
        WormWorld.Bounds bounds = roof.getBounds(cx, y0, cz).outset(1, 0, 1);
        bounds.y1 = 200;
        editor.apply(roof.new GT(cx, y0, cz), bounds);
    }

    private static void doOne2(BasicBlockEditor editor, int cx, int y0, int cz, int rx, int rz, boolean gableX, boolean gableZ)
        throws IOException
    {
        StairRoof roof = new StairRoof(rx, rz, gableX, gableZ);
        WormWorld.Bounds bounds = roof.getBounds(cx, y0, cz).outset(1, 0, 1);
        bounds.y1 = 200;
        editor.apply(roof.new GT(cx, y0, cz), bounds);
    }

}
