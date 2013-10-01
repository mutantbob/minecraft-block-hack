package com.purplefrog.minecraftExplorer;

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
//

    public static class Pane
        extends BlenderMeshElement
    {
        private final int orientation;

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
    }
}
