package com.purplefrog.minecraftExplorer.japanesecastle;

import com.purplefrog.minecraftExplorer.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/1/13
 * Time: 9:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class StairRoof
{
    private final int rx;
    private final int rz;
    private final boolean gableX;
    private final boolean gableZ;

    public StairRoof(int rx, int rz, boolean gableX, boolean gableZ)
    {
        this.rx = rx;
        this.rz = rz;
        this.gableX = gableX;
        this.gableZ = gableZ;
    }

    /**
     *
     * @param x relative to the center
     * @param z relative to the center
     * @return
     */
    public int yFor(int x, int z)
    {
        int dx = rx - Math.abs(x);
        int dz = rz - Math.abs(z);
        if (gableX) {
            if (gableZ)  {
                return Math.max(dx, dz);
            } else {
                return dz;
            }
        } else {
            if (gableZ) {
                return dx;
            } else {
                return Math.min(dx, dz);
            }
        }
    }

    private BlockPlusData halfSlab(boolean upper)
    {
        int slabDetail = 3;
        int bt = 126;

        return new BlockPlusData(bt, (upper ? 8:0) | slabDetail);
    }

    public BlockPlusData blockFor(int x, int z)
    {
        if (gableX) {
            if (gableZ) {
                return forGableXZ(x, z);

            } else {
                return forGableX(x, z);
            }

        } else {
            if (gableZ) {
                return forGableZ(x, z);
            } else {
                return noGable(x, z);
            }
        }
    }

    private BlockPlusData noGable(int x, int z)
    {
        int yx = rx - Math.abs(x) ;
        int yz = rz - Math.abs(z);
        int dy = Math.min(yx, yz);
        int peak = blockHeight();

        if (dy==peak)
            return halfSlab(false);

        if (yz < yx) {
            if (z<0) {
                return stair(2);
            } else {
                return stair(3);
            }
        } else {
            if (x<0 ) {
                return stair(0);
            } else {
                return stair(1);
            }
        }
    }

    private BlockPlusData forGableX(int x, int z)
    {
        int yz = rz - Math.abs(z);
        int dy = yz;
        int peak = rz;

        if (dy==peak)
            return halfSlab(false);

        if (true) {
            if (z<0) {
                return stair(2);
            } else {
                return stair(3);
            }
        } else {
            if (x<0 ) {
                return stair(0);
            } else {
                return stair(1);
            }
        }
    }

    private BlockPlusData forGableZ(int x, int z)
    {
        int yx = rx - Math.abs(x);
        int dy = yx;
        int peak = rx;

        if (dy==peak)
            return halfSlab(false);

        if (false) {
            if (z<0) {
                return stair(2);
            } else {
                return stair(3);
            }
        } else {
            if (x<0 ) {
                return stair(0);
            } else {
                return stair(1);
            }
        }
    }

    private BlockPlusData forGableXZ(int x, int z)
    {
        int yx = rx - Math.abs(x) ;
        int yz = rz - Math.abs(z);
        int dy = Math.max(yx, yz);
        int peak = Math.max(rx, rz);

//        if (dy==peak)
//            return halfSlab(false);

        if (yz < yx
            || (yz==yx && rz< rx)) {
            if (x==0)
                return halfSlab(false);
            if (x<0) {
                return stair(0);
            } else {
                return stair(1);
            }
        } else {
            if (z==0)
                return halfSlab(false);
            if (z<0) {
                return stair(2);
            } else {
                return stair(3);
            }
        }
    }

    private BlockPlusData stair(int detail)
    {
        return new BlockPlusData(136, detail);
    }

    public WormWorld.Bounds getBounds(int cx, int y0, int cz)
    {
        return new WormWorld.Bounds(cx-rx, y0, cz-rz, cx+rx+1, y0+ blockHeight() +1, cz+rz+1);
    }

    private int blockHeight()
    {
        if (gableX) {
            if (gableZ) {
                return Math.max(rx,rz);
            } else {
                return rz;
            }
        } else {
            if (gableZ) {
                return rx;
            } else {
                return Math.min(rx,rz);
            }
        }
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
            int dx = x-cx;
            int dy = y-cy;
            int dz = z-cz;

            if (inBounds(dx, dz))
                return null;

            if (dy==yFor(dx, dz)) {
                return blockFor(dx, dz);
            } else {
                return new BlockPlusData(0);
            }
        }

        public boolean inBounds(int dx, int dz)
        {
            return Math.abs(dx)>rx
                || Math.abs(dz) > rz;
        }
    }
}
