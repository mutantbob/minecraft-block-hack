package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.minecraftExplorer.*;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/17/13
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Clouds
    implements GeometryTree
{

    public static final BlockPlusData AIR = new BlockPlusData(0);
    public static final BlockPlusData SPARSE = new BlockPlusData(
        //85
        18
    );
    public static final BlockPlusData DIRT = new BlockPlusData(89);
    public static final BlockPlusData STONE = new BlockPlusData(
        //1
        46
    );
    private final Bounds3Di bounds;
    private final int cellSize;
    private final int cellSizeY;
    GaussNode[] nodes;

    public Clouds()
    {

        int x1 = 225;
        int y0 = 80;
        int z1 = 400;
        cellSize = 16;
        cellSizeY = cellSize/2;
        int vScale = 5;
        int dy = vScale*5;

        int x2 = x1 + cellSize * 4;
        int z2 = z1 + cellSize * 4;

        bounds = new Bounds3Di(x1, y0, z1, x2, 160, z2);

        Random rand = new Random();
        int count = bounds.volume() / (cellSize * cellSizeY * cellSize);
        nodes =new GaussNode[count];

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = randomNode(rand, i*0.7/nodes.length + 0.3);
        }
    }

    public GaussNode randomNode(Random rand, double sigma)
    {
        double x0 = bounds.xSize() * rand.nextDouble() + bounds.x0;
        double y0 = bounds.ySize() * rand.nextDouble() + bounds.y0;
        double z0 = bounds.zSize() * rand.nextDouble() + bounds.z0;
        return new GaussNode(x0/cellSize, y0/cellSizeY, z0/cellSize, sigma);
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        if (!bounds.contains(x,y,z))
            return null;

        double val=0;
        for (GaussNode node : nodes) {
            val += node.eval(x/(double)cellSize,y/(double)cellSizeY,z/(double)cellSize);
        }

        if (y%6 < 2)
            return AIR;
        if (x%2 != 0 || z%2 != 0)
            return AIR;

//        System.out.println(val);
        if (val<3.0)
            return AIR;
        else if (val<3.5)
            return SPARSE;
        else if (val<4.0)
            return DIRT;
        else
            return STONE;

    }

    public static void main(String[] argv)
        throws IOException
    {
        BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(WorldPicker.menger5()));

        Clouds clouds = new Clouds();

        editor.apply(clouds, clouds.getBounds());

        editor.relight();
        editor.save();
    }

    public Bounds3Di getBounds()
    {
        return bounds;
    }

}
