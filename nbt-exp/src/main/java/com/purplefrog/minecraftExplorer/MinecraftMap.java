package com.purplefrog.minecraftExplorer;

import org.jnbt.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/3/13
 * Time: 5:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinecraftMap
    extends JComponent
{
    private final MinecraftWorld world;

    double wcx=0, wcz =0;
    private Map<Point, Image> chunkImageCache = new HashMap<Point, Image>();

    public MinecraftMap(MinecraftWorld world)
    {
        this.world = world;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(800,600);
    }

    @Override
    protected void paintComponent(Graphics g_)
    {
        super.paintComponent(g_);


        Graphics g = g_.create();

        Dimension sSize = getSize();

        ScreenWorldTransform xform = getScreenWorldTransform();

        double wx0 = xform.screenXToWorld(0);
        double wy0 = xform.screenYToWorld(0);
        double wx9 = xform.screenXToWorld(sSize.width);
        double wy9 = xform.screenYToWorld(sSize.height);

        int scale = 16;
        int wx1 = (int) Math.floor(wx0/ scale);
        int wy1 = (int) Math.floor(wy0/ scale);

        int wx8 = (int) Math.ceil(wx9 / scale);
        int wy8 = (int) Math.ceil(wy9 / scale);


        for (int x = wx1; x< wx8; x++) {
            for (int y=wy1; y<wy8; y++) {
                // iterating through regions

                int sx1 = (int) xform.worldXToScreen(x* scale);
                int sy1 = (int) xform.worldZToScreen((y) * scale);
                int sw = (int) xform.worldXToScreen((x+1)* scale) - sx1;
                int sh = (int) xform.worldZToScreen((y + 1) * scale) - sy1;
                Image image = imageForRegion(x, y);
                if (null != image)
                    g.drawImage(image, sx1, sy1, sw, sh,null);
            }
        }

    }

    private Image imageForRegion(int cx, int cz)
    {
        Point key = new Point(cx,cz);
        Image image = chunkImageCache.get(key);
        if (null==image) {
            ColorModel cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
            int[] pix = new int[16*16];


            try {
                CompoundTag t = (CompoundTag) world.makeChunkFor(cx, cz);
                if (t==null)
                    return null;
                Anvil anvil = new Anvil(t);
                List<Anvil.Section> sections = anvil.getSections();
                if (null == sections)
                    return null;

                int[] pBlocks = MinecraftMinimap.topBlocks(sections);
                for (int i = 0; i < pBlocks.length; i++) {
                    int blockType = pBlocks[i];
                    pix[i] = pack(BlockDatabase.colorForBlockType(blockType));
                }


                image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, cm, pix, 0, 16 ));

                chunkImageCache.put(key, image);
            } catch (IOException e) {
                return null;
            }
        }
        return image;

    }

    private int pack(int[] rgb)
    {
        return (rgb[0]<<16)
            | (rgb[1]<<8)
            |rgb[2];
    }

    private ScreenWorldTransform getScreenWorldTransform()
    {
        return new ScreenWorldTransform(getSize(), wcx, wcz, 1);
    }

    public void setCenter(double x, double z)
    {
        wcx = x;
        wcz = z;
        repaint(1);
    }
}
