package com.purplefrog.minecraftExplorer.spleef;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 7/2/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpleefCombo
{

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor;
        if (false) {
            editor = new BlenderBlockEditor("/tmp/spleef/combo.py");
        } else {
            editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.pickSaveDir()));
        }

        //-221 40
        int cx = -140;
        int cz= 121;
        int y0 = 99;

        cliche(editor, -140, y0, 121);
        cliche(editor, -221, y0, 40);
        cliche(editor, -221, y0, 202);
        cliche(editor, -302, y0, 121);

        editor.relight();
        editor.save();
    }

    public static void cliche(BasicBlockEditor editor, int cx, int y0, int cz)
        throws IOException
    {
        Bounds3Di bounds;
        {
            int r1 = 15;
            bounds = new Bounds3Di(cx- r1, y0, cz- r1,
                cx+ r1 +1, y0+40, cz+ r1 +1);
        }
        SpleefBasin1 basin = new SpleefBasin1(cx, y0, cz, 15, 12);

        int y2 = y0+19;
        SpleefSiloWall silo = new SpleefSiloWall(new Point3Di(cx, 0, cz), y0 + 7, y2, 10.3, 12.5, new BlockPlusData(7));

        GeometryTree spleefArena = new SpleefArena(cx, cz, y0+12, 2, 7, new BlockPlusData(49));

        GeometryTree dome3 = glassHalfDome(new Point3D(cx, y2, cz));

        SpleefDivingBoard divingBoard = new SpleefDivingBoard(cx, y0 + 4, cz + 12, y0 + 18, cz + 8);

        editor.apply(new GTSequence(spleefArena, divingBoard, dome3, silo, basin), bounds);
    }

    private static GeometryTree glassHalfDome(Point3D domeCenter)
    {
        GeometryTree dome1 = new GTEllipse(domeCenter, 10.3, null, new GeometryTree.Solid(20));
        GeometryTree dome2 = new GTEllipse(domeCenter, 12.5, dome1, null);
        return new PlanarSplit(domeCenter.x, domeCenter.y, domeCenter.z, 0,1,0, dome2, new GeometryTree.Solid(null));
    }
}
