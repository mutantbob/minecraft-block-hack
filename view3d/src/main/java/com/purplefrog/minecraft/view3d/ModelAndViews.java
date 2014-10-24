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
    public static final BlockEnvironment ISOLATION = new BlockEnvironment(new boolean[6], new boolean[6]);

    public static ModelAndView blocksPerData(int bt)
        throws IOException, JSONException
    {
        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        BlockModels blockModels = BlockModels.getInstance();
        blocksPerData(blockModels, accum, bt);
        ModelViewSetter modelView = new ModelViewSetter(-3.5, -1.5, -3.5, 5, 0, 0, -12, 30);
        return new ModelAndView(accum, modelView);
    }

    public static ModelAndView blocks8x8parade(int baseBT)
        throws IOException, JSONException
    {
        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        BlockModels blockModels = BlockModels.getInstance();
        blocks8x8parade(blockModels, accum, baseBT);
        ModelViewSetter modelView = new ModelViewSetter(-7.5, -1.5, -7.5, 10, 0, 0, -20, 24);
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

        ModelViewSetter modelView = viewForBox(x1, y1, z1, x2, y2, z2);

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

    public static ModelViewSetter viewForBox(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return new ModelViewSetter(-(x1+x2)/2.0, -(y1+y2)/2.0, -(z1+z2)/2.0,
            10,
            0,0, -Math.max(x2-x1, z2-z1)*1.2,
            30);
    }

    public static void blocksPerData(BlockModels blockModels, List<BlenderMeshElement> accum, int bt)
        throws IOException, JSONException
    {
        int cols = 4;
        for (int blockData=0; blockData<16; blockData++) {
            int x = 2*(blockData % cols);
            int y = 0;
            int z = 2*(blockData / cols);
            blockModels.modelFor(bt, blockData, ISOLATION).getMeshElements(accum, x, y, z, ISOLATION);
        }
    }

    public static void singleBlock(BlockModels blockModels, List<BlenderMeshElement> accum, int bt, int blockData)
        throws IOException, JSONException
    {
        int x = 0, y = 0, z = 0;
        blockModels.modelFor(bt, blockData, ISOLATION).getMeshElements(accum, x, y, z, ISOLATION);
    }

    public static void cullTest(BlockModels blockModels, List<BlenderMeshElement> accum, int bt, int blockData)
        throws IOException, JSONException
    {
        for (int i=0; i<6; i++) {
            boolean[] culling = new boolean[6];
            culling[i] = true;
            BlockEnvironment env = new BlockEnvironment(culling, culling);
            blockModels.modelFor(bt, blockData, env).getMeshElements(accum, i*2, 0, 0, env);
        }
    }

    public static void blocks8x8parade(BlockModels blockModels, List<BlenderMeshElement> accum, int baseBT)
        throws IOException, JSONException
    {
        int cols = 8;
        for (int i=0; i<64; i++) {
            int blockData = 0;
            int x = 2*(i % cols);
            int y = 0;
            int z = 2*(i / cols);
            blockModels.modelFor(i+ baseBT, blockData, ISOLATION).getMeshElements(accum, x, y, z, ISOLATION);
        }
    }

    public static ModelAndView farmHouse()
    {
        MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = -485, y1=60, z1=159, x2=-448, y2=80, z2=190;

        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);
        ModelViewSetter modelView = ModelAndViews.viewForBox(x1, y1, z1, x2, y2, z2);
        return new ModelAndView(accum, modelView);
    }

    public static ModelAndView melonPatch()
    {
        MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = -448, y1=60, z1=159, x2=-439, y2=80, z2=180;

        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);
        ModelViewSetter modelView = ModelAndViews.viewForBox(x1, y1, z1, x2, y2, z2);
        return new ModelAndView(accum, modelView);
    }

    public static ModelAndView rocketCastle()
    {
        MinecraftWorld world = new MinecraftWorld(WorldPicker.pickSaveDir());
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = -775, y1=60, z1=180, x2=-725, y2=140, z2=230;

        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);
        ModelViewSetter modelView = ModelAndViews.viewForBox(x1, y1, z1, x2, y2, z2);
        return new ModelAndView(accum, modelView);
    }

    public static ModelAndView house1()
    {
        MinecraftWorld world = new MinecraftWorld(new File(WorldPicker.savesDir(), "2014-Sep-28 v1_8"));
        BasicBlockEditor editor = new AnvilBlockEditor(world);

        int x1 = 160, y1=50, z1=190, x2=210, y2=90, z2=240;

        List<BlenderMeshElement> accum = new ArrayList<BlenderMeshElement>();
        convertWorld(accum, editor, x1, y1, z1, x2, y2, z2);
        
        ModelViewSetter modelView = ModelAndViews.viewForBox(x1, y1, z1, x2, y2, z2);
        return new ModelAndView(accum, modelView);
    }

}
