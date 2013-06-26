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
    public final int x;
    public final int y;
    public final int z;
    public final int bt;
    public final int blockData;

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
        private final FaceSide orientation;

        public Face(int x, int y, int z, int bt, int blockData, FaceSide orientation)
        {
            super(x, y, z, bt, blockData);

            this.orientation = orientation;
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
}
