package com.purplefrog.minecraftExplorer;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/21/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface F3
{
    double eval(double x, double y, double z);

    public class GT
        implements GeometryTree
    {
        private F3 base;
        private final GeometryTree high;
        public final Tuple[] tuples;

        public GT(F3 base, GeometryTree high, double threshold, GeometryTree low)
        {
            this.base = base;
            this.high = high;
            tuples = new Tuple[] {
                new Tuple(threshold, low)
            };

        }

        public GT(F3 base, GeometryTree high, double t1, GeometryTree low1, double t2, GeometryTree low2)
        {
            this.base = base;
            this.high = high;
            tuples = new Tuple[] {
                new Tuple(t1, low1),
                new Tuple(t2, low2)
            };
        }

        @Override
        public BlockPlusData pickFor(int x, int y, int z)
        {
            GeometryTree q = whichLayer(base.eval(x, y, z));
            if (q==null)
                return null;
            else
                return q.pickFor(x, y, z);
        }

        public GeometryTree whichLayer(double val)
        {
            GeometryTree q;
            for (int i=tuples.length-1; i>=0; i--) {
                if (val < tuples[i].threshold) {
                    return tuples[i].low;
                }
            }
            return high;
        }

        public static class Tuple
        {
            public double threshold;
            public GeometryTree low;

            public Tuple(double threshold, GeometryTree low)
            {
                this.threshold = threshold;
                this.low = low;
            }
        }
    }

}
