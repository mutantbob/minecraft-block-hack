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
    public static final BlockPlusData TNT = new BlockPlusData(18);
    public static final BlockPlusData STONE = new BlockPlusData(1);
    public static final BlockPlusData GRASS = new BlockPlusData(2);

    private final double[] heightmap;
    protected final int vScale;
    protected final double minSigma;

    public double threshold;
    public int cellSize;
    Bounds3Di b;

    Map<Point, HeightMap> elements = new HashMap<Point, HeightMap>();
    protected final int clusterDiameter;
    protected final Random rand = new Random();
    protected double sigmaSpan;

    public GaussLumps(Bounds3Di bounds, int cellSize, int vScale, double minSigma, int maxSigma, int clusterDiameter, double threshold1)
    {
        this.cellSize = cellSize;
        this.vScale = vScale;
        this.minSigma = minSigma;
        this.clusterDiameter = clusterDiameter;
        sigmaSpan = maxSigma - minSigma;
        threshold = threshold1;

        b = bounds;

        this.heightmap = fabricateHeightMap();
    }

    public GaussLumps(Bounds3Di b)
    {
        this(b, 16, 5, 0.3, 1, 4, 1.5);
    }

    private double[] fabricateHeightMap()
    {
        int dx = b.xSize();
        int dy = b.ySize();
        int dz = b.zSize();
        double[] heightmap = new double[dx*dz];

        for (int x=b.x0; x<b.x1; x++) {
            for (int z=b.z0; z<b.z1; z++) {
                heightmap[x-b.x0 + dx*(z-b.z0)] = height(x,z);
            }
        }
        System.out.println("height map");
        return heightmap;
    }

    private double height(int x, int z)
    {
        int r = 3;
        int uc = (int) Math.floor(x / (double)cellSize);
        int vc = (int) Math.floor(z / (double)cellSize);

        double x1 = x / (double) cellSize;
        double z1 = z / (double) cellSize;

        double rval =0;
        for (int u= uc - r; u<= uc+r; u++) {
            for (int v=vc-r; v<= vc+r; v++) {
                HeightMap hm = getElement(u,v);
                rval += hm.blargh(x1, z1);
            }
        }
        return rval;
    }

    private HeightMap getElement(int u, int v)
    {
        Point key = new Point(u, v);
        HeightMap rval = elements.get(key);
        if (rval==null) {
            double cx = u + clusterDiameter * (rand.nextDouble() - 0.5);
            double cz = v + clusterDiameter * (rand.nextDouble() - 0.5);
            double sigma = rand.nextDouble() * sigmaSpan + minSigma;
            rval = new SingleGaussMap(cx, cz, sigma);
            elements.put(key, rval);
        }
        return rval;
    }

    @Override
    public BlockPlusData pickFor(int x, int y, int z)
    {
        int u = x-b.x0;
        int v = z-b.z0;
        if (u<0 || x>=b.x1)
            return null;
        if (z<0 || z>=b.z1)
            return null;

        int y1 = y-b.y0;
        if (y1<0)
            return null;

        int dx = b.xSize();
        double h = heightmap[u + v * dx];
        double y2 = h * vScale;
        if (y1>=y2)
            return AIR;
        if (h<threshold)
            return TNT;
        if (y1<(threshold+0.3)*vScale)
            return STONE;
        return GRASS;
    }

    private Bounds3Di bounds()
    {
        return b;
    }


    public static class GaussIslands
    {
        public static void main(String[] argv)
            throws IOException
        {
            File w = WorldPicker.menger5();
            BasicBlockEditor editor = new AnvilBlockEditor(new MinecraftWorld(w));

            final Point3Di p1 = new Point3Di(225, 80, 400);
            int cellSize = 16;
            int vScale = 5;
            final Point3Di point3Di = new Point3Di(p1.x + cellSize * 15, p1.y + vScale * 5, p1.z + cellSize * 40);
            GaussLumps lumps = new GaussLumps(new Bounds3Di(p1, point3Di));
            editor.apply(lumps, lumps.bounds());
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

    private class SingleGaussMap
        implements HeightMap
    {
        double cx;
        double cy;
        double sigma;

        public SingleGaussMap(double cx, double cy, double sigma)
        {
            this.cx = cx;
            this.cy = cy;
            this.sigma = sigma;
        }

        @Override
        public double blargh(double x, double y)
        {
            return gauss(L22(x-cx, y-cy), sigma);
        }
    }
}
