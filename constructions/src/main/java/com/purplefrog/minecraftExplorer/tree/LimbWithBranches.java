package com.purplefrog.minecraftExplorer.tree;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;
import com.purplefrog.minecraftExplorer.landscape.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/23/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class LimbWithBranches
    implements F3
{
    private final CompoundLimb core;
    private final List<CompoundLimb> branches = new ArrayList<CompoundLimb>();
    private final F3 mix;

    public LimbWithBranches(Point3D origin, Point3D axis, double r1, double r9, double a1, double a9, double inflation, Random rand)
    {
        core = new CompoundLimb(origin, axis, r1, r9, a1, a9, inflation, rand);

        double da = a9 - a1;
        boolean flip = true;
        for (double i=0; i< da; i+=5)
        {
            Point3D p1 = core.interpolatePoint(i+a1);
            Point3D axis_ = new Point3D(p1.x-origin.x,
                (rand.nextDouble()-0.5) * r1*4,
                p1.z-origin.z);
            Point3D axis2 = flip
            ? new Point3D(axis_.z, axis_.y, -axis_.x)
                : new Point3D(-axis_.z, axis_.y, axis_.x);

            CompoundLimb branch = new CompoundLimb(p1, axis2, r1 / 2, r9 / 2, 0, 0.6 * (da - i), 10, rand);
            branches.add(branch);

            flip = !flip;
        }

        mix = new GaussMultiNode(branches, core);
    }

    @Override
    public double eval(double x, double y, double z)
    {
        return mix.eval(x, y, z);
    }

    public List<F3> lumpCloud(Random rand)
    {
        List<F3> nodes = new ArrayList<F3>();
        for (CompoundLimb branch : branches) {
            nodes.addAll(branch.lumpCloud(4, rand));
        }
        return nodes;
    }
}
