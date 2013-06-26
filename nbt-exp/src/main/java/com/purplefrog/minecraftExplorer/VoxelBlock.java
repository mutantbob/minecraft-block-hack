package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/4/13
 * Time: 10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class VoxelBlock<T>
{

    protected int minX, minY, minZ;
    protected int deltaX=0, deltaY=0, deltaZ=0;
    protected T[] data;
    protected Class<T> cls;
    protected T zero;


    public VoxelBlock(Class<T> cls, T zero)
    {
        this.cls = cls;
        this.zero = zero;
    }

    public T get(int x, int y, int z)
    {
        if (outOfBounds(x,y,z) )
            return zero;

        return data[indexOf(x,y,z)];
    }

    public void set(int x, int y, int z, T newValue)
    {
        if (0 == deltaX) {
            // bootstrap
            minX = x;
            minY = y;
            minZ = z;
            deltaX = deltaY = deltaZ=1;
            int n = deltaX * deltaY * deltaZ;
            data = (T[]) Array.newInstance(cls, n);
        } else if (outOfBounds(x, y, z)) {

            int nmx = Math.min(minX, x);
            int nmy = Math.min(minY, y);
            int nmz = Math.min(minZ, z);
            int ndx = Math.max(x+1, afterX()) - nmx;
            int ndy = Math.max(y+1, afterY()) - nmy;
            int ndz = Math.max(z+1, afterZ()) - nmz;

            resize(nmx, nmy, nmz, ndx, ndy, ndz);
        }

        int idx = indexOf(x, y, z);
        data[idx] = newValue;
    }

    public boolean outOfBounds(int x, int y, int z)
    {
        return x<minX || y<minY || z<minZ
            || x>= afterX() || y>= afterY() || z>= afterZ();
    }

    private void resize(int nmx, int nmy, int nmz, int ndx, int ndy, int ndz)
    {
        T[] newData = (T[]) Array.newInstance(cls, ndx * ndy * ndz);
        Arrays.fill(newData, zero);
        for (int c=minZ; c<afterZ(); c++) {
            for (int b=minY; b<afterY(); b++) {
                for (int a=minX; a<afterX(); a++) {
                    newData[ ((c-nmz)*ndy + (b-nmy))*ndx + (a-nmx)]
                        = data[indexOf(a, b, c)];
                }
            }
        }

        data = newData;
        minX = nmx;
        minY = nmy;
        minZ = nmz;
        deltaX = ndx;
        deltaY = ndy;
        deltaZ = ndz;
    }

    public int indexOf(int x, int y, int z)
    {
        return ( (z -minZ)*deltaY + (y -minY))*deltaX + x -minX;
    }

    private int afterZ()
    {
        return minZ+deltaZ;
    }

    private int afterY()
    {
        return minY+deltaY;
    }

    private int afterX()
    {
        return minX+deltaX;
    }

    public Iterable<Map.Entry<Point3Di, T>> entrySet()
    {
        return new Iterable<Map.Entry<Point3Di, T>>()
        {
            @Override
            public Iterator<Map.Entry<Point3Di, T>> iterator()
            {
                return new VBIterator(minX, minY, minZ, deltaX, deltaY, deltaZ, data, zero);
            }
        };
    }

    public T get(Point3Di loc)
    {
        return get(loc.x, loc.y, loc.z);
    }

    public class VBIterator
        implements Iterator<Map.Entry<Point3Di, T>>
    {

        protected int minX, minY, minZ;
        protected int deltaX=0, deltaY=0, deltaZ=0;
        protected T[] data;
        protected Class<T> cls;
        protected T zero;

        int cursor=0;
        protected final int size;

        public VBIterator(int minX, int minY, int minZ, int deltaX, int deltaY, int deltaZ, T[] data, T zero)
        {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.deltaZ = deltaZ;
            this.data = data;
            this.zero = zero;
            size = deltaX*deltaY*deltaZ;
        }

        @Override
        public boolean hasNext()
        {
            while (cursor < size && data[cursor]==zero) {
                cursor++;
            }
            return cursor< size;
        }

        @Override
        public Map.Entry<Point3Di, T> next()
        {
            if (!hasNext())
                throw new NoSuchElementException();

            int x = minX + cursor%deltaX;
            int c2 = cursor / deltaX;
            int y = minY + c2%deltaY;
            int z = minZ + c2/deltaY;

            AbstractMap.SimpleEntry<Point3Di, T> rval = new AbstractMap.SimpleEntry<Point3Di, T>(new Point3Di(x, y, z), data[cursor]);
            cursor++;
            return rval;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
