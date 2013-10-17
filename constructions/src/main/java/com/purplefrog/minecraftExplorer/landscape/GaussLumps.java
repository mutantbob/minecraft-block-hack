package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.minecraftExplorer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/15/13
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GaussLumps
    implements GeometryTree
{

    public static final double GAUSS_DENOM = Math.sqrt(2 * Math.PI);

    public static final BlockPlusData AIR = new BlockPlusData(0);
    public static final BlockPlusData SEA = new BlockPlusData(0);
    public static final BlockPlusData LEAVES = new BlockPlusData(18);
    public static final BlockPlusData STONE = new BlockPlusData(1);
    public static final BlockPlusData GRASS = new BlockPlusData(2);

    public final Bounds3Di bounds;
    public final int vScale;
    public double vScale2;

    public double threshold;
    public double threshold2;

    protected final QuantizedDCG topSurface;
    protected final QuantizedDCG bottomSurface;

    public GaussLumps(Bounds3Di bounds, int cellSize, int vScale, double minSigma, double maxSigma, int clusterDiameter, double threshold1, double threshold2)
    {
        topSurface = new QuantizedDCG(clusterDiameter, minSigma, maxSigma, bounds, cellSize);
        bottomSurface = new QuantizedDCG(clusterDiameter, minSigma, maxSigma, bounds, cellSize/2);
        bottomSurface.addRandomNoise(0.5);
        this.vScale = vScale;
        this.vScale2 = vScale/2;
        threshold = threshold1;
        this.threshold2 = threshold2;

        this.bounds = bounds;
    }

    public GaussLumps(Bounds3Di b)
    {
        this(b, 16, 5, 0.3, 1, 4, 1.5, 1.8);
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        if (!topSurface.b.contains(x, y, z))
            return null;

        int y1 = y- topSurface.b.y0;

        double h = topSurface.heightAt(x, z);
        double y2 = h * vScale;
        if (y1>=y2)
            return AIR;
        if (h<threshold)
            return SEA;

        double h2 = bottomSurface.heightAt(x,z);
        double y3 = threshold*vScale - Math.max(0, h2-1)*vScale2;

        if (y1<y3) {
            if (x%4==0 && z%4==0)
                return LEAVES;
            else
                return AIR;
        }

        if (y1< threshold2 *vScale)
            return STONE;
        return GRASS;
    }

    private Bounds3Di bounds()
    {
        return bounds;
    }


    public static class GaussIslands
    {
        public static void main(String[] argv)
            throws IOException
        {
            File w = WorldPicker.menger5();
            BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(w));

            int x1 = 225;
            int y0 = 80;
            int z1 = 400;
            int cellSize = 16;
            int vScale = 5;
            int dy = vScale*5;

            int x2 = x1 + cellSize * 15;
            int z2 = z1 + cellSize * 40;

            int y1 = y0 + dy;
            int y2 = y1+dy;
            int y3 = y2+dy;

            {
                final Point3Di p1 = new Point3Di(x1, y0, z1);
                final Point3Di p2 = new Point3Di(x2, y1, z2);

                GaussLumps lumps = new GaussLumps(new Bounds3Di(p1, p2));
                editor.apply(lumps, lumps.bounds());
            }

            {
                Point3Di p1 = new Point3Di(x1, y1, z1);
                Point3Di p2 = new Point3Di(x2, y2, z2);
                GaussLumps lumps = new GaussLumps(new Bounds3Di(p1, p2), 16, 5, 0.3, 0.9, 4, 1.8, 2.0);
                editor.apply(lumps, lumps.bounds());
            }

            {
                Point3Di p1 = new Point3Di(x1, y2, z1);
                Point3Di p2 = new Point3Di(x2, y3, z2);
                GaussLumps lumps = new GaussLumps(new Bounds3Di(p1, p2), 8, 5, 0.3, 0.9, 4, 2.0, 2.2);
                editor.apply(lumps, lumps.bounds());
            }


            editor.relight();
            editor.save();
        }
    }

    public static class FourByFour {
        public static void main(String[] argv)
        {
            JFrame fr = new JFrame("gauss lumps");

            JPanel p = new JPanel(new GridLayout(4,4));

            for (int i=0; i<16; i++) {
                p.add(new JLabel(icon()));
            }

            fr.getContentPane().add(p);

            fr.pack();
            fr.setVisible(true);
            fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public static class Archipellago
    {
        public static void main(String[] argv)
        {

            JFrame fr = new JFrame("gauss lumps");

            int w = 300;
            int h = 250;
            JPanel p = new JPanel(new GridLayout(3,3));
            Random rand = new Random();
            for (int i=0; i<9; i++) {
                int r = i<3 ? 20 : 10;
                HeightMap hm = new GaussHM(rand, r * r, r);
                hm = new GaussHM2(rand, r*r, 1, 2);
                double scale = h / r / 1.2;
                ImageProducer ip = render(hm, w, h, scale, (int) scale, (int) scale);
                p.add( new JLabel(toIcon(ip)));
            }

            fr.getContentPane().add(p);

            fr.pack();
            fr.setVisible(true);
            fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public static ImageIcon icon()
    {
        ImageProducer ip = render2(new GaussHM(new Random(), 10), 200, 200);
        return toIcon(ip);
    }

    private static ImageIcon toIcon(ImageProducer ip)
    {
        Image img = Toolkit.getDefaultToolkit().createImage(ip);
        return new ImageIcon(img);
    }

    private static ImageProducer render2(HeightMap hm, int w, int h)
    {
        double scale = Math.min(w,h)/2.0;
        int cx=w/2;
        int cy=h/2;
        return render(hm, w, h, scale, cx, cy);
    }

    private static ImageProducer render(HeightMap hm, int w, int h, double scale, int cx, int cy)
    {
        int[] pixels = new int[w*h];
        for (int u=0; u<w; u++) {
            for (int v=0; v<h; v++) {
                double x = (u-cx)/scale ;
                double y = (v-cy)/scale ;

                double q = hm.blargh(x, y);
                int grey = clamp255(q/4);
//                System.out.print(grey+" ");
                int color = q>1.5 ? 0x10100 : 1;
                int pix = (grey* color) | 0xff000000;

                pixels[u+ v*w] = pix;
            }
        }
        return new MemoryImageSource(w, h, pixels, 0, w);
    }

    public static int clamp255(double f)
    {
        int grey;
        if (f <0)
            grey=0;
        else if (f >1)
            grey=0xff;
        else {
            grey = (int) (f *0xff);
        }
        return grey & 0xf0;
    }


    public static double L2(double dx, double dy)
    {
        return Math.sqrt(L22(dx, dy));
    }

    public static double L22(double dx, double dy)
    {
        return dx * dx + dy * dy;
    }

    public static double blargh(double x, double y)
    {
        return ( gauss(L22(x, y), 0.3)
            + gauss(L22(x - 0.7, y + 0.8), 0.2))
            /2.0;
    }

    public static double gauss(double xsq, double sigma)
    {
        return Math.exp(-xsq / (2*sigma*sigma)) / ( sigma * GAUSS_DENOM);
    }

    public interface HeightMap
    {
        double blargh(double x, double y);
    }
    
    public static class GaussHM
        implements HeightMap
    {
        double[] centers;

        public GaussHM(Random rand, int count)
        {
            centers = new double[3*count];

            double r = 1.5;
            for (int i=0; i<count; i++) {
                int j=i*3;
                centers[j] = r*(rand.nextDouble()-0.5);
                centers[j+1] = r*(rand.nextDouble()-0.5);
                centers[j+2] = rand.nextDouble()*0.8 +0.1;
            }
        }

        public GaussHM(Random rand, int count, double r)
        {
            centers = new double[3*count];

            for (int i=0; i<count; i++) {
                int j=i*3;
                centers[j] = r*(rand.nextDouble()-0.5);
                centers[j+1] = r*(rand.nextDouble()-0.5);
                centers[j+2] = rand.nextDouble()*0.6 +0.3;
            }
        }

        @Override
        public double blargh(double x, double y)
        {
            double sum=0;
            for (int i=0; i<centers.length; i+=3) {
                double cx = centers[i];
                double cy = centers[i+1];
                double sigma = centers[i+2];

                double d= L22(x-cx,y-cy);
                sum += gauss(d, sigma);
            }
            return sum;
        }
    }

    public static class GaussHM2
        implements HeightMap
    {
        double[] centers;

        public GaussHM2(Random rand, int count, double d, double d2)
        {
            centers = new double[3*count];

            int rt = (int) Math.ceil(Math.sqrt(count));

            for (int u=0; u<rt; u++) {
                for (int v=0; v<rt; v++) {
                    int i = u+v*rt;
                    if (i>=count)
                        break;

                    int j=i*3;
                    centers[j] = u*d + d2*(rand.nextDouble()-0.5);
                    centers[j+1] = v*d + d2*(rand.nextDouble()-0.5);
                    centers[j+2] = rand.nextDouble()*0.8 +0.1;
                }
            }
        }

        @Override
        public double blargh(double x, double y)
        {
            double sum=0;
            for (int i=0; i<centers.length; i+=3) {
                double cx = centers[i];
                double cy = centers[i+1];
                double sigma = centers[i+2];

                double d= L22(x-cx,y-cy);
                sum += gauss(d, sigma);
            }
            return sum;
        }
    }

}
