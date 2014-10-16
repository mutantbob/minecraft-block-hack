package com.purplefrog.minecraftExplorer.blockmodels;

import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.blockmodels.*;
import junit.framework.*;
import org.json.*;

import java.io.*;
import java.util.*;

/**
 * Created by thoth on 9/26/14.
 */
public class ExpParseModels
{
    public static void main(String[] argv)
        throws IOException, JSONException
    {
        BlockModels db = BlockModels.getInstance();
        ArrayList<BlenderMeshElement> me = new ArrayList<BlenderMeshElement>();

        ExportWebGL.GLStore gl = new ExportWebGL.GLStore();

        boolean[] culling = new boolean[6];
        culling[BlockEnvironment.Orientation.up.ordinal()] = true;

        BlockEnvironment isolation = new BlockEnvironment(culling, culling);
        if (false) {
            OneBlockModel m = db.modelFor(1, 0, isolation);
            m.getMeshElements(me, 1,10,100, isolation);
        } else {

//        assertEquals(6, me.size());

            for (int i=1; i<=198; i++) {
                int x = i%16;
                int y = i/16;

                db.modelFor(i,0, isolation).getMeshElements(me, x*2, 0, y*2, isolation);
//                System.out.println(i+" "+me.size());
            }
        }

        for (BlenderMeshElement blenderMeshElement : me) {
            blenderMeshElement.accumOpenGL(gl);
        }

        System.out.println(gl);

        OutputStreamWriter w = new OutputStreamWriter(System.out);
        w = new FileWriter("/tmp/shit.js");
        ExportWebGL exporter = new ExportWebGL(0, 0, 0, 0, 0, 0,
            "/tmp/shit.js");

        w.write("webgl_segments=[\n");

        exporter.dumpSegment(w, gl);
        w.write("]\n");
        w.flush();
    }
}
