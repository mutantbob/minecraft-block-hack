package com.purplefrog.minecraftExplorer.japanesecastle;

import com.purplefrog.minecraftExplorer.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 9/30/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class JapaneseRoof
{

    int rx;
    int rz;
    private final int rx2;
    private final int rz2;
    int r2x, r2z;
    boolean gableX;
    boolean gableZ;
    protected final int slabDetail = 3;


    public JapaneseRoof(int rx, int rz, int rx2, int rz2, boolean gableX, boolean gableZ)
    {
        this.rx = rx;
        this.rz = rz;
        this.rx2 = rx2;
        this.rz2 = rz2;
        this.gableX = gableX;
        this.gableZ = gableZ;
    }

    public WormWorld.Bounds getBounds(int cx, int y0, int cz, JapaneseRoof roof)
    {
        return new WormWorld.Bounds(cx-rx, y0, cz-rz,
            cx+rx+1, y0+roof.blockHeight(), cz+rz+1);
    }

    public int blockHeight()
    {
        return (1+Math.min(rx, rz) )
//            / 2
            +1;
    }

    public class GT
        implements GeometryTree
    {
        int cx, cy, cz;

        public GT(int cx, int cy, int cz)
        {
            this.cx = cx;
            this.cy = cy;
            this.cz = cz;
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            int dx = Math.abs(x-cx);
            int dz = Math.abs(z-cz);

            int inset = Math.min(rx-dx,rz-dz);

            boolean upper = 0==(inset&1);

            int dy = y - cy;

            if (dy<0 || dy > Math.min(rx,rz))
                return null;

            if (dx>rx || dz>rz)
                return new BlockPlusData(0);

            if (rx == dx && rz==dz) {
                // corner points
                if (dy==2)
                    return new BlockPlusData(125, slabDetail);
                else
                    return new BlockPlusData(0);

            } else if (rx+rz == dx+dz+1) {
                // corner stairs
                if (dy==1) {
                    int detail;
                    if (dz+1 == rz) {
                        if (z<cz) {
                            detail = 3;
                        } else {
                            detail = 2;
                        }
                    } else {
                        if (x<cx) {
                            detail = 1;
                        } else {
                            detail = 0;
                        }
                    }
                    return stair(detail);
                } else
                    return new BlockPlusData(0);

            } else if (rx+rz == dx+dz+2) {
                // stair-adjacent slab

                if (dy==1)
                    return halfSlab(false);
                else
                    return new BlockPlusData(0);

            } else if (dx<rx2 && dz < rz2) {

                int peak = Math.min(rx + rx2, rz+rz2) /2;

                int dyx = (rx-dx + (dx < rx2 ? rx2-dx:0))/2;
                int dyz = (rz-dz + (dz < rz2 ? rz2-dz:0))/2;

                if (dyz< dyx) {
                    if (dy==dyz) {
                        if (dy==peak)
                            return halfSlab(false);
                        else if (z<cz)
                            return stair(2);
                        else
                            return stair(3);
                    } else
                        return new BlockPlusData(0);
                } else {
                    if (dy==dyx)
                        if (dy==peak)
                            return halfSlab(false);
                        else if (x<cx ) {
                            return stair(0);
                        } else {
                            return stair(1);
                        }
                    else
                        return new BlockPlusData(0);
                }

            } else {
                if (dy != ((inset+1)/2))
                    return new BlockPlusData(0);

                return halfSlab(upper);
            }
        }
    }

    public BlockPlusData stair(int detail)
    {
        return new BlockPlusData(136, detail);
    }

    public BlockPlusData halfSlab(boolean upper)
    {
        int bt = 126;

        return new BlockPlusData(bt, (upper ? 8:0) | slabDetail);
    }
}
