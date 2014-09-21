package com.purplefrog.minecraftExplorer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/26/13
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BlenderMeshElement
{
    public int x;
    public int y;
    public int z;
    public int bt;
    public int blockData;

    public BlenderMeshElement(int x, int y, int z, int bt, int blockData)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.bt = bt;
        this.blockData = blockData;
    }

    public String locString()
    {
        return x+","+y+","+z;
    }

    @Override
    public String toString()
    {
        return asPython();
    }

    public abstract String asPython();

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof BlenderMeshElement) {
            BlenderMeshElement arg = (BlenderMeshElement) o;
            return equals_(arg);
        } else {
            return false;
        }
    }

    public boolean equals_(BlenderMeshElement arg)
    {
        return arg.x == x
            && arg.y == y
            && arg.z == z
            && arg.bt == bt
            && arg.blockData == blockData;
    }

    public abstract void accumOpenGL(ExportWebGL.GLStore glStore);

//

    public static class Pane
        extends BlenderMeshElement
    {
        /**
         * @see com.purplefrog.minecraftExplorer.BasicBlockEditor#paneOrientation(int, int, int)
         */
        private final int orientation;

        /**
         *
         * @param x
         * @param y
         * @param z
         * @param bt
         * @param blockData
         * @param orientation  see {@link com.purplefrog.minecraftExplorer.BasicBlockEditor#paneOrientation(int, int, int)}
         */
        public Pane(int x, int y, int z, int bt, int blockData, int orientation)
        {
            super(x, y, z, bt, blockData);

            this.orientation = orientation;
        }

        @Override
        public String asPython()
        {

            return "minecraftPane("+locString()+", "+bt+","+blockData+", " +orientation+")";
        }

        @Override
        public void accumOpenGL(ExportWebGL.GLStore glStore)
        {
            ExportWebGL.TextureForGL tex = ExportWebGL.TextureForGL.from(new BlockPlusData(bt, blockData), FaceSide.NORTH);

            if (0 != (orientation&1) ) {
                addHalfPane(glStore, tex, 0.5, 0.5, 0, 0.5);
            }
            if (0 != (orientation&2) ) {
                addHalfPane(glStore, tex, 0.5, 0.5, 1, 0.5);
            }
            if (0 != (orientation&4) ) {
                addHalfPane(glStore, tex, 1, 0.5, 0.5, 0.5);
            }
            if (0 != (orientation&8) ) {
                addHalfPane(glStore, tex, 0, 0.5, 0.5, 0.5);
            }
        }

        private void addHalfPane(ExportWebGL.GLStore glStore, ExportWebGL.TextureForGL tex, double dx1, double dx2, double dz1, double dz2)
        {
            glStore.addFace(tex,
                glStore.getVertex(x+dx1, y, z+dz1, 0,0),
                glStore.getVertex(x+dx2, y, z+dz2, 0.5,0),
                glStore.getVertex(x+dx2, y+1, z+dz2, 0.5,1),
                glStore.getVertex(x+dx1, y+1, z+dz1, 0,1));
            glStore.addFace(tex,
                glStore.getVertex(x+dx2, y, z+dz2, 0.5,0),
                glStore.getVertex(x+dx1, y, z+dz1, 1,0),
                glStore.getVertex(x+dx1, y+1, z+dz1, 1,1),
                glStore.getVertex(x+dx2, y+1, z+dz1, 0.5,1));
        }
    }

    public static class Face
        extends BlenderMeshElement
    {
        public final FaceSide orientation;

        public Face(int x, int y, int z, int bt, int blockData, FaceSide orientation)
        {
            super(x, y, z, bt, blockData);

            this.orientation = orientation;
        }

        public Face(int x, int y, int z, BlockPlusData blockData, FaceSide orientation)
        {
            this(x,y,z,blockData.blockType, blockData.data, orientation);
        }

        @Override
        public String asPython()
        {
            String args = locString() + ", " + bt + "," + blockData;
            return "minecraftBlock" +orientation.capitalized()+
                "(" + args + ")";
        }

        @Override
        public void accumOpenGL(ExportWebGL.GLStore glStore)
        {
            ExportWebGL.TextureForGL tex = ExportWebGL.TextureForGL.from(new BlockPlusData(bt, blockData), orientation);
            switch (orientation) {
                case TOP:
                    glStore.addFace(tex,
                        glStore.getVertex(x,y+1,z, 0,0),
                        glStore.getVertex(x,y+1,z+1, 1,0),
                        glStore.getVertex(x+1,y+1,z+1, 1,1),
                        glStore.getVertex(x+1,y+1,z, 0,1) );
                    break;
                case NORTH:
                    glStore.addFace(tex,
                        glStore.getVertex(x+1,y,z, 0,0),
                        glStore.getVertex(x,y,z, 1,0),
                        glStore.getVertex(x,y+1,z, 1,1),
                        glStore.getVertex(x+1,y+1,z, 0,1));
                    break;
                case SOUTH:
                    glStore.addFace(tex,
                        glStore.getVertex(x,y+1,z+1, 0,1),
                        glStore.getVertex(x,y,z+1, 0,0),
                        glStore.getVertex(x+1,y,z+1, 1,0),
                        glStore.getVertex(x+1,y+1,z+1, 1,1));
                    break;
                case EAST:
                    glStore.addFace(tex,
                        glStore.getVertex(x+1,y,z+1, 0,0),
                        glStore.getVertex(x+1,y,z, 1,0),
                        glStore.getVertex(x+1,y+1,z, 1,1),
                        glStore.getVertex(x+1,y+1,z+1, 0,1));
                    break;
                case WEST:
                    glStore.addFace(tex,
                        glStore.getVertex(x,y,z, 0,0),
                        glStore.getVertex(x,y,z+1, 1,0),
                        glStore.getVertex(x,y+1,z+1, 1,1),
                        glStore.getVertex(x,y+1,z, 0,1));
                    break;
            }
        }
    }

    public static class Widget
        extends BlenderMeshElement
    {
        public Widget(int x, int y, int z, int bt, int blockData)
        {
            super(x, y, z, bt, blockData);
        }

        @Override
        public String asPython()
        {
            return ("minecraftWidget("+locString()+", "+bt+","+blockData+")");
        }

        @Override
        public void accumOpenGL(ExportWebGL.GLStore glStore)
        {
            ExportWebGL.TextureForGL tex = ExportWebGL.TextureForGL.from(new BlockPlusData(bt, blockData), FaceSide.NORTH);

            if (bt==53
                || bt==67
                || bt==108
                || bt==109
                || bt==114
                || bt==128
                || bt==134
                || bt==135
                || bt==136
                || bt==156
                || bt==163
                || bt==164
                || bt==180
                ) {
                stairs(glStore);
            } else {
                System.err.println("unknown widget "+bt);
            }
        }

        private void stairs(ExportWebGL.GLStore glStore)
        {
            XYZUV[] pre = {new XYZUV(1.000000,1.000000,1.000000, -1.000000,1.000000),
                new XYZUV(1.000000,0.000000,1.000000, -1.000000,0.000000),
                new XYZUV(1.000000,0.000000,0.000000, 0.000000,0.000000),
                new XYZUV(1.000000,1.000000,0.000000, 0.000000,1.000000),
                new XYZUV(0.000000,0.500000,1.000000, 1.000000,0.500000),
                new XYZUV(0.000000,0.500000,0.000000, 0.000000,0.500000),
                new XYZUV(0.000000,0.000000,0.000000, 0.000000,0.000000),
                new XYZUV(0.000000,0.000000,1.000000, 1.000000,0.000000),
                new XYZUV(1.000000,0.000000,1.000000, 1.000000,0.000000),
                new XYZUV(0.500000,0.500000,1.000000, 0.500000,0.500000),
                new XYZUV(0.000000,0.500000,1.000000, 0.000000,0.500000),
                new XYZUV(0.000000,0.000000,1.000000, 0.000000,0.000000),
                new XYZUV(1.000000,0.000000,1.000000, -1.000000,-1.000000),
                new XYZUV(0.000000,0.000000,1.000000, 0.000000,-1.000000),
                new XYZUV(0.000000,0.000000,0.000000, 0.000000,0.000000),
                new XYZUV(1.000000,0.000000,0.000000, -1.000000,0.000000),
                new XYZUV(1.000000,0.000000,0.000000, -1.000000,0.000000),
                new XYZUV(0.000000,0.000000,0.000000, 0.000000,0.000000),
                new XYZUV(0.000000,0.500000,0.000000, 0.000000,0.500000),
                new XYZUV(0.500000,0.500000,0.000000, -0.500000,0.500000),
                new XYZUV(0.500000,1.000000,1.000000, 0.500000,-1.000000),
                new XYZUV(1.000000,1.000000,1.000000, 1.000000,-1.000000),
                new XYZUV(1.000000,1.000000,0.000000, 1.000000,0.000000),
                new XYZUV(0.500000,1.000000,0.000000, 0.500000,0.000000),
                new XYZUV(0.500000,0.500000,1.000000, 1.000000,0.500000),
                new XYZUV(0.500000,1.000000,1.000000, 1.000000,1.000000),
                new XYZUV(0.500000,1.000000,0.000000, 0.000000,1.000000),
                new XYZUV(0.500000,0.500000,0.000000, 0.000000,0.500000),
                new XYZUV(0.500000,0.500000,1.000000, 0.500000,-1.000000),
                new XYZUV(0.500000,0.500000,0.000000, 0.500000,0.000000),
                new XYZUV(0.000000,0.500000,0.000000, 0.000000,0.000000),
                new XYZUV(0.000000,0.500000,1.000000, 0.000000,-1.000000),
                new XYZUV(1.000000,1.000000,0.000000, -1.000000,1.000000),
                new XYZUV(1.000000,0.000000,0.000000, -1.000000,0.000000),
                new XYZUV(0.500000,0.500000,0.000000, -0.500000,0.500000),
                new XYZUV(0.500000,1.000000,0.000000, -0.500000,1.000000),
                new XYZUV(1.000000,1.000000,1.000000, 1.000000,1.000000),
                new XYZUV(0.500000,1.000000,1.000000, 0.500000,1.000000),
                new XYZUV(0.500000,0.500000,1.000000, 0.500000,0.500000),
                new XYZUV(1.000000,0.000000,1.000000, 1.000000,0.000000),
            };

            List<XYZUV> points = rotated(Arrays.asList(pre));

            points = translated(points);

            for (int i=0; i<pre.length; i++) {
                pre[i] = points.get(i);
            }

            ExportWebGL.TextureForGL tex0 = ExportWebGL.TextureForGL.from(new BlockPlusData(bt,99), BlockSide.SIDE);

            ExportWebGL.TextureForGL tex1 = ExportWebGL.TextureForGL.from(new BlockPlusData(bt,99), BlockSide.TOP);
            glStore.addFace(tex0,
                glStore.getVertex(points.get(0)),
                glStore.getVertex(points.get(1)),
                glStore.getVertex(points.get(2)),
                glStore.getVertex(points.get(3)));
            glStore.addFace(tex0,
                glStore.getVertex(points.get(4)),
                glStore.getVertex(points.get(5)),
                glStore.getVertex(points.get(6)),
                glStore.getVertex(points.get(7)));
            glStore.addFace(tex0,
                glStore.getVertex(points.get(8)),
                glStore.getVertex(points.get(9)),
                glStore.getVertex(points.get(10)),
                glStore.getVertex(points.get(11)));
            glStore.addFace(tex1,
                glStore.getVertex(points.get(12)),
                glStore.getVertex(points.get(13)),
                glStore.getVertex(points.get(14)),
                glStore.getVertex(points.get(15)));
            glStore.addFace(tex0,
                glStore.getVertex(points.get(16)),
                glStore.getVertex(points.get(17)),
                glStore.getVertex(points.get(18)),
                glStore.getVertex(points.get(19)));
            glStore.addFace(tex1,
                glStore.getVertex(points.get(20)),
                glStore.getVertex(points.get(21)),
                glStore.getVertex(points.get(22)),
                glStore.getVertex(points.get(23)));
            glStore.addFace(tex0,
                glStore.getVertex(points.get(24)),
                glStore.getVertex(points.get(25)),
                glStore.getVertex(points.get(26)),
                glStore.getVertex(points.get(27)));
            glStore.addFace(tex1,
                glStore.getVertex(points.get(28)),
                glStore.getVertex(points.get(29)),
                glStore.getVertex(points.get(30)),
                glStore.getVertex(points.get(31)));
            glStore.addFace(tex0,
                glStore.getVertex(points.get(32)),
                glStore.getVertex(points.get(33)),
                glStore.getVertex(points.get(34)),
                glStore.getVertex(points.get(35)));
            glStore.addFace(tex0,
                glStore.getVertex(points.get(36)),
                glStore.getVertex(points.get(37)),
                glStore.getVertex(points.get(38)),
                glStore.getVertex(points.get(39)));


        }

        double[][] rotX180 = {
            {1,0,0,0},
            {0,-1,0,1},
            {0,0,-1,1},
            {0,0,0,1}
        };

        double[][] identity = {
            {1,0,0,0},
            {0,1,0,0},
            {0,0,1,0},
            {0,0,0,1},
        };

        double[][] rotY180 = {
            {-1,0,0,1},
            {0,1,0,0},
            {0,0,-1,1},
            {0,0,0,1},
        };

        double[][] rotY90 = {
            {0,0,1,0},
            {0,1,0,0},
            {-1,0,0,1},
            {0,0,0,1},
        };

        double[][] rotY270 = {
            {0,0,-1,1},
            {0,1,0,0},
            {1,0,0,0},
            {0,0,0,1},
        };

        public List<XYZUV> rotated(Iterable<XYZUV> points)
        {
            double[][] matrix;
            switch (blockData&3) {
                case 0:
                    matrix = identity; break;
                case 1:
                    matrix = rotY180; break;
                case 2:
                    matrix = rotY270; break;
                default:
                    matrix = rotY90; break;
            }
            if (0 != (blockData&4 )) {
                matrix = mult(rotX180, matrix);
            }

            return rotated(points, matrix);
        }

        public List<XYZUV> translated(List<XYZUV> points)
        {
            List<XYZUV> rval = new ArrayList<XYZUV>();
            for (XYZUV point : points) {
                rval.add(new XYZUV(point.x+x, point.y+y, point.z+z, point.u, point.v));
            }
            return rval;
        }
    }

    public static List<XYZUV> rotated(Iterable<XYZUV>points, double[][]matrix)
    {
        List<XYZUV> rval = new ArrayList<XYZUV>();
        for (XYZUV point : points) {
            double x2 = mult34(point.x, point.y, point.z, matrix[0]);
            double y2 = mult34(point.x, point.y, point.z, matrix[1]);
            double z2 = mult34(point.x, point.y, point.z, matrix[2]);
            rval.add(new XYZUV(x2, y2, z2, point.u, point.v));
        }
        return rval;
    }

    public static double mult34(double x, double y, double z, double[] matrixRow)
    {
        return x*matrixRow[0]
            +y*matrixRow[1]
            +z*matrixRow[2]
            +matrixRow[3];
    }

    public static double[][] mult(double[][] A, double[][]B)
    {
        int l2 = B[0].length;
        double[][] rval = new double[A.length][];

        for (int i=0; i<A.length; i++) {
            rval[i] = new double[l2];
            for (int j=0; j< l2; j++) {
                double accum=0;
                for (int k=0; k<B.length; k++) {
                    accum += A[i][k] * B[k][j];
                }
                rval[i][j] = accum;
            }
        }
        return rval;
    }


    public static class VSet
    {
        List<XYZUV> vertices = new ArrayList<XYZUV>();

    }

    public static class FatFace
        extends BlenderMeshElement
    {
        public final FaceSide orientation;
        public int du;
        public int dv;

        public FatFace(int x, int y, int z, int bt, int blockData, FaceSide orientation, int du, int dv)
        {
            super(x, y, z, bt, blockData);

            this.orientation = orientation;
            this.du = du;
            this.dv = dv;
        }

        public FatFace(Face f1)
        {
            this(f1.x, f1.y, f1.z, f1.bt, f1.blockData, f1.orientation, 1,1);
        }

        public FatFace(FatFace arg)
        {
            this(arg.x, arg.y, arg.z, arg.bt, arg.blockData, arg.orientation, arg.du, arg.dv);
        }

        @Override
        public String asPython()
        {
            return "minecraftFatFace("+locString()+", "+bt+","+blockData+", " +orientation+", "+du+","+dv+ ")";
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof FatFace) {
                FatFace arg = (FatFace) o;
                return super.equals(arg)
                    && arg.orientation == orientation
                    && arg.du == du
                    && arg.dv == dv;
            } else {
                return false;
            }
        }

        @Override
        public void accumOpenGL(ExportWebGL.GLStore glStore)
        {
            // XXX
        }
    }
}
