package com.purplefrog.minecraft.view3d;

import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.blockmodels.*;
import org.json.*;

import java.io.*;
import java.util.*;

/**
 * Created by thoth on 10/21/14.
 */
public class ModelAndViews
{
    public static ModelAndView blocksPerData(int bt)
        throws IOException, JSONException
    {
        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        BlockModels blockModels = BlockModels.getInstance();
        ModelAndView.blocksPerData(blockModels, accum, bt);
        ModelAndView.ModelViewSetter modelView = new ModelAndView.ModelViewSetter(-3.5, -1.5, -3.5, 5, 0, 0, -12, 30);
        return new ModelAndView(accum, modelView);
    }

    public static ModelAndView blocks8x8parade(int baseBT)
        throws IOException, JSONException
    {
        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        BlockModels blockModels = BlockModels.getInstance();
        ModelAndView.blocks8x8parade(blockModels, accum, baseBT);
        ModelAndView.ModelViewSetter modelView = new ModelAndView.ModelViewSetter(-7.5, -1.5, -7.5, 10, 0, 0, -20, 24);
        return new ModelAndView(accum, modelView);
    }


    public static ModelAndView farm()
        throws IOException, JSONException
    {

        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();

        MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = -485, y1=60, z1=130, x2=-426, y2=80, z2=190;

        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);

        ModelAndView.ModelViewSetter modelView = ModelAndView.viewForBox(x1, y1, z1, x2, y2, z2);

        return new ModelAndView(accum, modelView);
    }

    public static void convertWorld(List<BlenderMeshElement> accum, BasicBlockEditor editor, int x1, int y1, int z1, int x2, int y2, int z2)
    {
        for (int y=y1; y<=y2; y++) {
            for (int x = x1; x<=x2; x++) {
                for (int z=z1; z<=z2; z++) {
                    editor.getBlenderMeshElements(accum, x,y,z);
                }
            }
        }
    }
}
