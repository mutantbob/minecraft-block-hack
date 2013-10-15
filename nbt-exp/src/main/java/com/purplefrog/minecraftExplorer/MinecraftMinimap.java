package com.purplefrog.minecraftExplorer;

import com.mojang.nbt.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/3/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinecraftMinimap
    extends JComponent
{
    protected final ExecutorService executor = Executors.newFixedThreadPool(1);
    private MinecraftWorld world;

    /**
     * world coordinates of the center of the visual map
     */
    double wcx=0, wcy=0;

    /**
     * how many screen pixels is a minecraft block?
     */
    double magnification = 0.2;
    private Map<Point,Color> colorCache = new HashMap<Point, Color>();
    private Map<Point,Image> regionImageCache = new HashMap<Point, Image>();

    public MinecraftMinimap(MinecraftWorld world)
    {
        this.world = world;

        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {

                ScreenWorldTransform xform = getScreenWorldTransform();

                int x = (int) xform.screenXToWorld(e.getX());
                int z = (int) xform.screenYToWorld(e.getY());

                Graphics g = getGraphics();

                FontMetrics fm = g.getFontMetrics();


                String msg = "x=" + x + ", z=" + z;

                int y = fm.getAscent()+5+3;

                g.setColor(Color.black);
                g.fillRect(0, 5, fm.charsWidth(msg.toCharArray(), 0,msg.length())+20, fm.getHeight()+6);

                g.setColor(Color.white);
                g.drawString(msg, 10,y);
            }
        });

    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(200,200);
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

        if (false) {
            int wx1 = (int) Math.floor(wx0/16);
            int wy1 = (int) Math.floor(wy0/16);

            int wx8 = (int) Math.ceil(wx9 / 16);
            int wy8 = (int) Math.ceil(wy9 / 16);


            for (int x = wx1; x< wx8; x++) {
                for (int y=wy1; y<wy8; y++) {
                    // iterating through chunks

                    g.setColor(colorFor(x, y));

                    int sx1 = (int) xform.worldXToScreen(x*16);
                    int sy1 = (int) xform.worldZToScreen((y) * 16);
                    int sw = (int) xform.worldXToScreen((x+1)*16) - sx1;
                    int sh = (int) xform.worldZToScreen((y + 1) * 16) - sy1;
                    g.fillRect(sx1, sy1, sw, sh);
                }
            }
        } else {

            int wx1 = (int) Math.floor(wx0/ regionImageTileSize);
            int wy1 = (int) Math.floor(wy0/ regionImageTileSize);

            int wx8 = (int) Math.ceil(wx9 / regionImageTileSize);
            int wy8 = (int) Math.ceil(wy9 / regionImageTileSize);


            for (int x = wx1; x< wx8; x++) {
                for (int y=wy1; y<wy8; y++) {
                    // iterating through regions
                    drawRegionEventually(g, x, y);
                }
            }
        }

    }

    int chunkWidthForRegion = 32;
    int regionImageTileSize = chunkWidthForRegion*16;

    private void drawRegionEventually(Graphics g, int x, int y)
    {
        Point key = new Point(x, y);
        Image image1 = regionImageCache.get(key);
        if (null== image1) {
            backgroundDrawRegion(x,y);
        } else {
            drawRegionToScreen(g, x, y, image1);
        }
    }

    private void drawRegionToScreen(Graphics g, int x, int y, Image image)
    {
        ScreenWorldTransform xform = getScreenWorldTransform();

        int sx1 = (int) xform.worldXToScreen(x* regionImageTileSize);
        int sy1 = (int) xform.worldZToScreen((y) * regionImageTileSize);
        int sw = (int) xform.worldXToScreen((x+1)* regionImageTileSize) - sx1;
        int sh = (int) xform.worldZToScreen((y + 1) * regionImageTileSize) - sy1;

        g.drawImage(image, sx1, sy1, sw, sh,null);
    }

    private void backgroundDrawRegion(final int x, final int y)
    {
        executor.submit(new Runnable()
        {
            @Override
            public void run()
            {
                final Image img = imageForRegion(x, y);
                drawRegionToScreen_(img, x, y);
            }
        });
    }

    private void drawRegionToScreen_(final Image img, final int x, final int y)
    {
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                drawRegionToScreen(getGraphics(), x,y, img);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    private synchronized Image imageForRegion(int rx, int ry)
    {
        Point key = new Point(rx,ry);
        Image image = regionImageCache.get(key);
        if (null==image) {
            image = fabricateImage(rx, ry);

            regionImageCache.put(key, image);
        }
        return image;
    }

    public Image fabricateImage(int rx, int ry)
    {
        Image image;ColorModel cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
        int cwfr = chunkWidthForRegion;
        int[] pix = new int[cwfr * cwfr];

        for (int z=0; z< cwfr; z++) {
            for (int x=0; x< cwfr; x++) {
                pix[z* cwfr +x ] = colorFor(rx* cwfr +x, ry* cwfr +z).getRGB();
            }
        }
        image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(cwfr, cwfr, cm, pix, 0, cwfr));
        return image;
    }

    private ScreenWorldTransform getScreenWorldTransform()
    {
        return new ScreenWorldTransform(getSize(), wcx, wcy, magnification);
    }

    private Color colorFor(int chunkX, int chunkY)
    {
        Color c;

        Point key = new Point(chunkX, chunkY);

        c = colorCache.get(key);
        if (c==null) {
            c = computeColor(chunkX, chunkY);
        }
        return c;

    }

    private Color computeColor(int chunkX, int chunkY)
    {
        Color c;
        try {
            Tag t = world.makeChunkFor(chunkX, chunkY);
            if (t==null) {
                c = Color.black;
            } else {
                c = colorFor((CompoundTag) t);
            }
        } catch (IOException e) {
            e.printStackTrace();
            c = Color.black;
        }
        return c;
    }

    public static Color colorFor(CompoundTag t)
    {
        Anvil anvil = new Anvil(t);
        List<Anvil.Section> sections = anvil.getSections();
        if (null == sections)
            return Color.black;

        int[] topBlockType = topBlocks(sections);

        int r=0,g=0,b=0;
        for (int i=0; i<topBlockType.length; i++) {
            int[] color = BlockDatabase.colorForBlockType(topBlockType[i]);
            r += color[0];
            g += color[1];
            b += color[2];
        }

        int n = topBlockType.length;
        return new Color(r/n,g/n,b/n);
    }

    public static int[] topBlocks(List<Anvil.Section> sections)
    {
        int[] topBlockType = new int[16*16];
        int[] ya = new int[16*16];
        for (Anvil.Section section : sections) {
            ByteCube blocks_ = section.getBlocks();
            int y0 = section.getY();
            int ptr=0;
            for (int y=0; y<16; y++) {
                for (int z=0; z<16; z++) {
                    for (int x=0; x<16; x++, ptr++) {
                        int blockType = blocks_.get_(ptr);
                        if (BlockDatabase.transparent(blockType))
                            continue;
                        int o2 = x + 16 * z;

                        if (0 != blockType && y+16*y0 > ya[o2]) {
                            topBlockType[o2] = blockType;
                            ya[o2] = y+16*y0;
                        }
                    }
                }
            }
        }
        return topBlockType;
    }

    public void linkClickEvents(final MinecraftMap worldMap)
    {
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                ScreenWorldTransform xform = getScreenWorldTransform();
                double x = xform.screenXToWorld(e.getX());
                double z = xform.screenYToWorld(e.getY());

                worldMap.setCenter(x,z);
                setCenter((int) x, (int) z);
            }
        });


        if (false) {
            addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent ev)
                {
                    ScreenWorldTransform xform = getScreenWorldTransform();
                    int x = (int) xform.screenXToWorld(ev.getX());
                    int y = (int) xform.screenYToWorld(ev.getY());

                }
            });
        }
    }

    private void setCenter(int x, int z)
    {
        wcx = x;
        wcy = z;
        repaint(1);
        System.out.println(wcx+","+wcy);
    }
}
