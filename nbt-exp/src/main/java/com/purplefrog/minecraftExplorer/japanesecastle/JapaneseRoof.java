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

    public int rx;
    public int rz;

    public StairRoof secondary;
    public int secondaryRX;
    public int secondaryRZ;

    protected final int slabDetail = 3;

    public JapaneseRoof(int rx, int rz)
    {

        this.rx = rx;
        this.rz = rz;
    }

    public JapaneseRoof(int rx, int rz, int rx2, int rz2, boolean gableX, boolean gableZ, int secondaryRX, int secondaryRZ)
    {
        this(rx, rz);

        secondary = new StairRoof(rx2, rz2, gableX, gableZ);
        this.secondaryRX = secondaryRX;
        this.secondaryRZ = secondaryRZ;
    }

    public Bounds3Di getBounds(int cx, int y0, int cz)
    {
        return new Bounds3Di(cx-rx, y0, cz-rz,
            cx+rx+1, y0+blockHeight(), cz+rz+1);
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
        protected final GeometryTree chain;
        int cx, cy, cz;

        public GT(int cx, int cy, int cz, GeometryTree chain)
        {
            this.cx = cx;
            this.cy = cy;
            this.cz = cz;
            this.chain = chain;
        }

        public GT(int cx, int cy, int cz)
        {
            this(cx,cy,cz, new BlockPlusData(0));
        }

        public GT(int cx, int cy, int cz, BlockPlusData chain)
        {
            this(cx,cy,cz, new Solid(chain));
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            int dx_ = x - cx;
            int dx = Math.abs(dx_);
            int dz_ = z - cz;
            int dz = Math.abs(dz_);

            int inset = Math.min(rx-dx,rz-dz);

            boolean upper = 0==(inset&1);

            int dy = y - cy;

            if (dy<0 || dy > Math.min(rx,rz))
                return chain_(x,y,z);

            if (dx>rx || dz>rz)
                return chain_(x,y,z);


            if (dx+dz+2 >= rx+rz) {
                return cornerDetails(x,y,z);
            }

            int y1 = (inset + 1) / 2;

            if (secondary != null && dx<=secondaryRX && dz <= secondaryRZ) {
                int y2 = secondary.yFor(dx_, dz_);

                if (dy == y2) {
                    if (y2==y1 && upper)
                        return halfSlab(upper);
                    if (y2 >= y1)
                        return secondary.blockFor(dx_, dz_);
                }
            }

            if (dy == y1) {
                return halfSlab(upper);
            }

            return chain_(x,y,z);

        }

        private BlockPlusData cornerDetails(int x, int y, int z)
        {

            int dx_ = x - cx;
            int dx = Math.abs(dx_);
            int dz_ = z - cz;
            int dz = Math.abs(dz_);

            int dy = y - cy;

            if (rx == dx && rz==dz) {
                // corner points
                if (dy==2)
                    return material1();
                else
                    return chain_(x, y, z);

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
                    return chain_(x, y, z);

            } else if (rx+rz == dx+dz+2) {
                // stair-adjacent slab

                if (dy==1)
                    return halfSlab(false);
                else
                    return chain_(x, y, z);


            }

            return null;
        }

        private BlockPlusData chain_(int x,int y,int z)
        {
            return chain==null ?null : chain.pickFor(x,y,z);
        }
    }

    private BlockPlusData material1()
    {
        return new BlockPlusData(125, slabDetail);
    }

    public static BlockPlusData stair(int detail)
    {
        return new BlockPlusData(136, detail);
    }

    public BlockPlusData halfSlab(boolean upper)
    {
        int bt = 126;

        return new BlockPlusData(bt, (upper ? 8:0) | slabDetail);
    }
}
