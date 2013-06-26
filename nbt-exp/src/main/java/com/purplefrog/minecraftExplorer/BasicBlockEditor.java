package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/19/13
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicBlockEditor
    implements BlockEditor
{
    @Override
    public void setBlock(int x, int y, int z, int bt)
        throws IOException
    {
        setBlock(x,y,z, new BlockPlusData(bt, 0));
    }

    @Override
    public abstract void setBlock(int x, int y, int z, BlockPlusData block)
        throws IOException;

    @Override
    public abstract void save()
        throws IOException;

    @Override
    public abstract void relight();

    public void fillCube(int blockType, int x0, int y0, int z0, int dx, int dy, int dz)
        throws IOException
    {
        for (int x=0; x<dx; x++) {
            for (int y=0; y<dy; y++) {
                for (int z=0; z<dz; z++) {
                    setBlock(x + x0, y + y0, z + z0, blockType);
                }
            }
        }
    }

    public void fillCube(int blockType, WormWorld.Bounds b)
        throws IOException
    {
        fillCube(blockType, b.x0, b.y0, b.z0, b.x1, b.y1, b.z1);
    }

    public void fillCubeByCorners(BlockTemplate template, int x1, int y1, int z1, int x2, int y2, int z2)
        throws IOException
    {
        for (int x=x1; x<x2; x++) {
            for (int y=y1; y<y2; y++) {
                for (int z=z1; z<z2; z++) {
                    setBlock(x, y, z, template.getBlock(x - x1, y - y1, z - z1));
                }
            }
        }
    }

    public void drawBorderedRectangle(int x1, int y0, int z1, int x2, int z2, BlockTemplate north, BlockTemplate south, BlockTemplate east, BlockTemplate west, BlockTemplate meadow)
        throws IOException
    {
        for (int x=0; x+x1 < x2; x++) {
            for (int z=0; z+z1 < z2; z++) {

                int n=2;
                int d=1;
                ColumnRef col=null;

                if (null != west) {
                    n = x;
                    d = west.depth;
                    col = west.referenceColumn(z, x);
                }

                if (null != east) {
                    int n2 = x2-(x+x1)-1;
                    int d2 = east.depth;
                    if ( n2*d <= n*d2) {
                        col = east.referenceColumn(z, n2);
                        n = n2;
                        d = d2;
                    }
                }

                if (null != south) {
                    int n2 = z2-(z+z1)-1;
                    int d2 = south.depth;
                    if ( n2*d <= n*d2) {
                        col = south.referenceColumn(x,n2);
                        n = n2;
                        d = d2;
                    }
                }

                if (null != north) {
                    int n2 = z;
                    int d2 = north.depth;
                    if ( n2*d <= n*d2) {
                        col = north.referenceColumn(x,z);
                        n = n2;
                        d = d2;
                    }
                }

                if (n>=d)
                    col = meadow.referenceColumn(x,z);

                col.renderColumn(this, x+x1, y0, z+z1);
            }
        }
    }

    public void drawPyramid(int x1, int y1, int z1, int x2, int z2, BlockTemplate side, BlockTemplate corner)
        throws IOException
    {
        drawPyramid(x1, y1, z1, x2, z2, side, side.rot90(), side.rot180(), side.rot270(), corner, 1);
    }

    public void drawPyramid(int x1, int y1, int z1, int x2, int z2, BlockTemplate side, BlockTemplate corner, int dy)
        throws IOException
    {
        drawPyramid(x1, y1, z1, x2, z2, side, side.rot90(), side.rot180(), side.rot270(), corner, dy);
    }

    public void drawPyramid(int x1, int y1, int z1, int x2, int z2, BlockTemplate north, BlockTemplate east, BlockTemplate south, BlockTemplate west, BlockTemplate corner, int dy)
        throws IOException
    {

        for (int x=x1; x<x2; x++) {
            for (int z=z1; z<z2; z++) {

                ColumnRef col=null;

                int n=1;
                int d=0;

                if (west != null) {
                    int x_ = z2-1-z;
                    int z_ = x-x1;
                    int n2 = z_;
                    int d2 = west.depth;
                    if (n2*d == n*d2) {
                        col = corner.referenceColumn(x_, z_);
                    } else if (n2 * d <= n * d2) {
                        col = west.referenceColumn(x_, z_);
                        n = n2;
                        d = d2;
                    }
                }


                if (east != null) {
                    int x_ = z-z1;
                    int z_ = x2-1-x;
                    int n2 = z_;
                    int d2 = east.depth;

                    if (n2*d == n*d2) {
                        col = corner.referenceColumn(x_, z_);
                    } else if (n2*d <= n*d2) {
                        col = east.referenceColumn(x_, z_);
                        n = n2;
                        d = d2;
                    }
                }

                if (south != null) {
                    int x_ = x2-x-1;
                    int z_ = z2-z-1;
                    int n2 = z_;
                    int d2 = south.depth;
                    if (n2*d == n*d2) {
                        col = corner.referenceColumn(x_, z_);
                    } else if (n2*d <= n*d2) {
                        col = south.referenceColumn(x_, z_);
                        n = n2;
                        d = d2;
                    }
                }

                if (north != null) {
                    int x_ = x-x1;
                    int z_ = z-z1;
                    int n2 = z_;
                    int d2 = north.depth;
                    if (n2*d == n*d2) {
                        col = corner.referenceColumn(x_, z_);
                    } else if (n2*d <= n*d2) {
                        col = north.referenceColumn(x_, z_);
                        n = n2;
                        d = d2;
                    }
                }

                int y = y1 + (n/d) * dy;
                col.renderColumn(this, x,y,z);
            }
        }

    }

    public void apply(GeometryTree tree, WormWorld.Bounds b)
        throws IOException
    {
        for (int x=b.x0; x<b.x1; x++) {
            for (int y=b.y0; y<b.y1; y++) {
                for (int z=b.z0; z<b.z1; z++) {
                    BlockPlusData bt = tree.pickFor(x, y, z);
                    if (bt!=null)
                        setBlock(x,y,z, bt);
                }
            }
        }
    }

    public interface GetLightingCube
    {
        public NibbleCube getLightLevels(Anvil.Section s);
    }

    public static class GetSkyLightCube
        implements GetLightingCube
    {
        @Override
        public NibbleCube getLightLevels(Anvil.Section s)
        {
            return s.getSkyLight();
        }
    }

    public static class GetBlockLightCube
        implements GetLightingCube
    {
        @Override
        public NibbleCube getLightLevels(Anvil.Section s)
        {
            return s.getBlockLight();
        }
    }
}
