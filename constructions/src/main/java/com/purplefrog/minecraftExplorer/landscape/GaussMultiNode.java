package com.purplefrog.minecraftExplorer.landscape;

import com.purplefrog.minecraftExplorer.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/21/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class GaussMultiNode
    implements F3
{

    F3 [] nodes;

    public GaussMultiNode(F3... nodes)
    {
        this.nodes = nodes;
    }

    public GaussMultiNode(F3[] n1, F3... n2)
    {
        this.nodes = concat(n1, n2, F3.class);
    }

    public GaussMultiNode(List<? extends F3> n1, F3... n2)
    {
        this.nodes = concat(n1, n2, F3.class);
    }

    public static <T> T[] concat(T[] n1, T[] n2, Class<T> cls)
    {
        if (n1.length ==0)
            return n2;
        if (n2.length ==0)
            return n1;

        T[] rval = (T[]) java.lang.reflect.Array.newInstance(cls, n1.length+n2.length);


        System.arraycopy(n1, 0, rval, 0, n1.length);
        System.arraycopy(n2, 0, rval, n1.length, n2.length);

        return (T[]) rval;
    }

    public static <T> T[] concat(List<? extends T> n1, T[] n2, Class<T> cls)
    {
        if (n1.size() ==0)
            return n2;

        T[] rval = (T[]) java.lang.reflect.Array.newInstance(cls, n1.size()+n2.length);

        n1.toArray(rval);
        System.arraycopy(n2, 0, rval, n1.size(), n2.length);

        return (T[]) rval;
    }


    public double eval(double x, double y, double z)
    {
        double val=0;
        for (F3 node : nodes) {
            val += node.eval(x, y, z);
        }
        return val;
    }


}
