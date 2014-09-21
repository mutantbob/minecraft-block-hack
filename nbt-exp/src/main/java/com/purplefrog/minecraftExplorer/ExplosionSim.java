package com.purplefrog.minecraftExplorer;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/3/13
 * Time: 10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExplosionSim
{

    QueryableBlockEditor preExplosion;
    RAMBlockEditor postExplosion;

    VoxelBlock<Double> blockDisappearTime = new VoxelBlock<Double>(Double.class, null);

    Random rand;
    private Map<Point3Di, Double> detonations = new HashMap<Point3Di, Double>();
    public static final double BLOCK_DESTROY_RADIUS = 3.1;
    public static final double TNT_CHAIN_RADIUS = 3.5;


    public ExplosionSim(QueryableBlockEditor world, Random rand, String outDir)
    {
        preExplosion = world;
        this.rand = rand;
        postExplosion = new BlenderBlockEditor(new File(outDir, "/postexplosion").getPath(), world);
    }

    public static void simulateExplosions(QueryableBlockEditor editor, Random rand, String outDir)
        throws IOException
    {
        ExplosionSim sim = new ExplosionSim(editor, rand, outDir);

        sim.audit();

        int startingCount = countBlocks(editor, BlockDatabase.BLOCK_TYPE_TNT);

        sim.simulateExplosions(1.0);

        int endingCount =countBlocks(sim.postExplosion, BlockDatabase.BLOCK_TYPE_TNT);

        System.out.print("from "+startingCount+" to "+endingCount);

        sim.postExplosion.save();

        {
            emitPerBlockDestruction(outDir, sim);
        }

        emitPerSectionBlockDestruction(sim, new File(outDir));

        sim.emitPerGlowstone(new File(outDir));

        {
            sim.audit();
        }

        {
            Writer fw = new FileWriter(new File(outDir, "explosions.py"));
            emitExplosionSequence(sim, fw);

            fw.close();
        }
    }

    public static void emitPerSectionBlockDestruction(ExplosionSim sim, File outDir)
        throws IOException
    {
        Map<Point3Di, BlockDestructionSet> destructions = new HashMap<Point3Di, BlockDestructionSet>();

        for (Map.Entry<Point3Di, Double> en : sim.blockDisappearTime.entrySet()) {

            Point3Di loc = en.getKey();
            int x = loc.x;
            int y = loc.y;
            int z = loc.z;

            BlockPlusData blockData = sim.preExplosion.getBlockData(x, y, z);

            Point3Di loc16 = RAMBlockEditor.cook(x, y, z);

            BlockDestructionSet bds = destructions.get(loc16);
            if (null==bds) {
                bds = new BlockDestructionSet();
                destructions.put(loc16, bds);
            }

            double disappearTime = en.getValue();

            BlockDatabase.TransparencyClass tc1 = BlockDatabase.tClass(blockData.blockType);

            if (tc1== BlockDatabase.TransparencyClass.OpaqueFunky || tc1== BlockDatabase.TransparencyClass.Widget) {
                BlenderMeshElement cmd = ((BlenderBlockEditor) sim.preExplosion).blenderCommandForFunky(x, y, z, blockData.blockType, blockData.data);
                bds.register(cmd, disappearTime);
            } else {
                if (blockDisappearFaceTest(sim, x-1, y, z, disappearTime, tc1)) {
                    bds.register(new BlenderMeshElement.Face(x,y,z, blockData, FaceSide.WEST), disappearTime );
                }
                if (blockDisappearFaceTest(sim, x+1, y, z, disappearTime, tc1)) {
                    bds.register(new BlenderMeshElement.Face(x,y,z, blockData, FaceSide.EAST), disappearTime );
                }
                if (blockDisappearFaceTest(sim, x, y+1, z, disappearTime, tc1)) {
                    bds.register(new BlenderMeshElement.Face(x,y,z, blockData, FaceSide.TOP), disappearTime );
                }
                if (blockDisappearFaceTest(sim, x, y-1, z, disappearTime, tc1)) {
                    bds.register(new BlenderMeshElement.Face(x,y,z, blockData, FaceSide.BOTTOM), disappearTime );
                }
                if (blockDisappearFaceTest(sim, x, y, z-1, disappearTime, tc1)) {
                    bds.register(new BlenderMeshElement.Face(x,y,z, blockData, FaceSide.NORTH), disappearTime );
                }
                if (blockDisappearFaceTest(sim, x, y, z+1, disappearTime, tc1)) {
                    bds.register(new BlenderMeshElement.Face(x,y,z, blockData, FaceSide.SOUTH), disappearTime );
                }
            }
        }


        int seq=0;
        for (Map.Entry<Point3Di, BlockDestructionSet> en : destructions.entrySet()) {

            Point3Di loc = en.getKey();

            String basename = "blockDestruction2.py."+(seq++);
            Writer w = new FileWriter(new File(outDir, basename));
            w.write(en.getValue().emitDestructionMeshes(loc));
            w.close();
        }

    }

    public static boolean blockDisappearFaceTest(ExplosionSim sim, int x2, int y2, int z2, double disappearTime, BlockDatabase.TransparencyClass tc1)
    {
        Double otherDT = sim.blockDisappearTime.get(x2, y2, z2);

        if (otherDT==null) {
            int bt2 = sim.preExplosion.getBlockType(x2, y2, z2);
            BlockDatabase.TransparencyClass tc2 = BlockDatabase.tClass(bt2);
            return (tc1!=tc2) ;
        } else {

            return otherDT < disappearTime;
        }
    }

    public static void emitPerBlockDestruction(String outDir, ExplosionSim sim)
        throws IOException
    {
        Writer fw = new FileWriter(new File(outDir, "blockDestruction.py"));
        for (Map.Entry<Point3Di, Double> en : sim.blockDisappearTime.entrySet()) {

            Point3Di loc = en.getKey();
            int x = (int) loc.x;
            int y = (int) loc.y;
            int z = (int) loc.z;
            BlockPlusData blockData = sim.preExplosion.getBlockData(x, y, z);
            String msg = "blockDestroyed("+ loc.x+","+loc.y+","+loc.z+", "+ blockData.blockType+","+blockData.data+", "+en.getValue()+")\n";

            fw.write(msg);
        }

        fw.close();
    }

    public static void emitExplosionSequence(ExplosionSim sim, Writer fw)
        throws IOException
    {
        List<Map.Entry<Point3Di, Double>> blargh = new ArrayList<Map.Entry<Point3Di, Double>>();

        for (Map.Entry<Point3Di, Double> en : sim.detonations.entrySet()) {
            blargh.add(en);
        }

        Collections.sort(blargh, new Comparator<Map.Entry<Point3Di, Double>>()
        {
            @Override
            public int compare(Map.Entry<Point3Di, Double> a, Map.Entry<Point3Di, Double> b)
            {
                double a_ = a.getValue();
                double b_ = b.getValue();
                if (a_ < b_)
                    return -1;
                else if (a_>b_)
                    return 1;
                else
                    return 0;
            }
        });

        LinkedList<ExplosionReuse> explosions = new LinkedList<ExplosionReuse>();

        for (Map.Entry<Point3Di, Double> en : blargh) {

            ExplosionReuse q=null;
            if (!explosions.isEmpty()) {
                q = explosions.getFirst();
                if (q.horizon() > en.getValue()) {
                    q = null;
                } else {
                    explosions.removeFirst();
                    explosions.addLast(q);
                }
            }
            if (q==null) {
                q = new ExplosionReuse();
                explosions.addLast(q);

            }
            q.addExplosion(en);
        }

        for (ExplosionReuse explosion : explosions) {
            fw.write(explosion.toString());
        }
    }

    public static int countBlocks(QueryableBlockEditor world, int match)
    {
        int rval=0;
        for (Map.Entry<Point3Di, RAMBlockEditor.Combo> en : world.getCachedSections()) {
            for (Map.Entry<Point3Di, BlockPlusData> block : en.getValue().allBlocks(en.getKey())) {
                if (block.getValue().blockType== match)
                rval++;
            }
        }
        return rval;
    }

    public static Point3Di pickFirstTNT(QueryableBlockEditor editor)
    {
        Point3Di chosen = null;

        for (Map.Entry<Point3Di, RAMBlockEditor.Combo> en : editor.getCachedSections()) {

            Iterator<Map.Entry<Point3Di, BlockPlusData>> i2 = en.getValue().allBlocksIter(en.getKey());

            if (chosen != null && en.getKey().x >= chosen.x)
                continue; // this section can't possibly contain something better.

            while (i2.hasNext()) {
                Map.Entry<Point3Di, BlockPlusData> q = i2.next();

                if (q.getValue().blockType == BlockDatabase.BLOCK_TYPE_TNT) {
                    if (chosen==null || q.getKey().x < chosen.x)
                        chosen = q.getKey();
                }
            }
        }
        return chosen;
    }

    public void emitPerGlowstone(File outDir)
        throws IOException
    {
        List<String> commands = new ArrayList<String>();
        for (Map.Entry<Point3Di, BlockPlusData> en : preExplosion.allVoxels()) {

            Double destroyTime = blockDisappearTime.get(en.getKey());
            if (preExplosion.getBlockType(en.getKey()) == BlockDatabase.BLOCK_TYPE_GLOWSTONE) {

                checkLightVisible(en.getKey(), -1, 0, 0, destroyTime, commands);
                checkLightVisible(en.getKey(), 1, 0, 0, destroyTime, commands);
                checkLightVisible(en.getKey(), 0, -1, 0, destroyTime, commands);
                checkLightVisible(en.getKey(), 0, 1, 0, destroyTime, commands);
                checkLightVisible(en.getKey(), 0, 0, -1, destroyTime, commands);
                checkLightVisible(en.getKey(), 0, 0, 1, destroyTime, commands);
            }
        }

        Writer w = new FileWriter(new File(outDir, "glowstone.py"));
        for (String cmd : commands) {
            w.write(cmd);
        }
        w.close();
    }

    /**
     *
     * @param loc the coordinates of the glowstone
     * @param dx nearby block delta
     * @param destroyTime when the glowstone disappears (could be null)
     * @param commands where we add any python commands for blender
     */
    public void checkLightVisible(Point3Di loc, int dx, int dy, int dz, Double destroyTime, List<String> commands)
    {
        Double time2 = blockDisappearTime.get(loc);

        BlockDatabase.TransparencyClass tc2 = BlockDatabase.tClass(preExplosion.getBlockType(loc.x + dx, loc.y + dy, loc.z + dz));

        Double activate,deactivate;
        boolean needLight;

        deactivate = destroyTime;

        if (tc2== BlockDatabase.TransparencyClass.Solid) {

            if (destroyTime==null) {
                activate = time2;
                needLight = time2 != null;
            } else {

                if (time2==null) {
                    needLight = false;
                    activate=null;
                } else {
                    if (time2 < destroyTime) {
                        needLight = true;
                        activate = time2;
                    } else {
                        needLight = false;
                        activate=null;
                    }
                }
            }

        } else {
            needLight = true;
            activate = null;
        }

        if (needLight) {
            double q = 9.0/16;
            double x2 = loc.x+0.5 + dx* q;
            double y2 = loc.y+0.5 + dy* q;
            double z2 = loc.z+0.5 + dz* q;
            String cmd = "glowstoneLight(" + x2 + "," + y2 + "," + z2 + ", " +
                (activate == null ? "None" : activate) + ","
                + (deactivate == null ? "None" : deactivate) + ")\n";
            commands.add(cmd);
        }
    }

    public void audit()
    {
        int count=0;
        // audit
        for (Map.Entry<Point3Di, BlockPlusData> entry : preExplosion.allVoxels()) {
            Double q = blockDisappearTime.get(entry.getKey());
            if (q==null) {
                Point3Di loc = entry.getKey();
                int bt2 = postExplosion.getBlockType(loc.x, loc.y, loc.z);
                int bt1 = entry.getValue().blockType;
                if (bt2 != bt1) {
                    count++;
                    System.out.println("mismatch at "+loc.x+","+loc.y+","+loc.z+"  "+bt1+"!="+bt2);
                }
            }
        }
        if (count>0) {
            System.out.println(count+" mismatches");
        }
    }

    public Set<Point3Di> detonate(Point3Di loc, double tm)
        throws IOException
    {
        RAMBlockEditor world = postExplosion;
        Set<Point3Di> chained = new HashSet<Point3Di>();

        detonations.put(loc, tm);
        world.setBlock(loc.x, loc.y, loc.z, 0);
        blockDisappearTime.set(loc.x, loc.y, loc.z, tm);

        GeometryTree gt1 = new GTEllipse(loc.asD(), BLOCK_DESTROY_RADIUS, 2, 1);
        GeometryTree gt = new GTEllipse(loc.asD(), TNT_CHAIN_RADIUS, gt1, new GeometryTree.Solid(0));

        for (int z= (int) (loc.z - TNT_CHAIN_RADIUS); z <= loc.z+ TNT_CHAIN_RADIUS; z++) {
            for (int y= (int) (loc.y - TNT_CHAIN_RADIUS); y <= loc.y+ TNT_CHAIN_RADIUS; y++) {
                for (int x = (int) (loc.x - TNT_CHAIN_RADIUS); x<=loc.x+ TNT_CHAIN_RADIUS; x++) {
                    int code = gt.pickFor(x, y, z).blockType;
                    if (code <= 0)
                        continue; // this block isn't exploding

                    int bt = world.getBlockType(x,y,z);
//                    System.out.println("destroy @"+x+","+y+","+z+" was "+bt);

                    if (bt==BlockDatabase.BLOCK_TYPE_TNT) {
                        chained.add(new Point3Di(x, y, z));
                        world.setBlock(x, y, z, 0);
                    } else if (code==2) {
                        if (bt!=0) {
                            blockDisappearTime.set(x,y,z, tm);
                            world.setBlock(x, y, z, 0);
                        }
                    }
                }
            }
        }

        return chained;
    }

    /**
     *
     * @param firstExposionTime time in seconds
     * @throws IOException
     */
    public void simulateExplosions(double firstExposionTime)
        throws IOException
    {
        Point3Di chosen = pickFirstTNT(postExplosion);


        Map<Point3Di, Double> burning = new HashMap<Point3Di, Double>();

        burning.put(chosen, firstExposionTime);

        while (true) {

            Map.Entry<Point3Di, Double> nextExplosion = null;
            for (Map.Entry<Point3Di, Double> en : burning.entrySet()) {
                if (nextExplosion==null)
                    nextExplosion = en;
                else {
                    if (nextExplosion.getValue() > en.getValue())
                        nextExplosion = en;
                }
            }
            if (nextExplosion == null)
                break;

            Point3Di explosionLocation = nextExplosion.getKey();
            double when = burning.remove(explosionLocation);

//            System.out.println("explosion at "+explosionLocation+" t="+nextExplosion.getValue());

            Set<Point3Di> toBurn = detonate(explosionLocation, when);

//            System.out.println("chain-detonates ["+toBurn.size()+"]");
            for (Point3Di point3D : toBurn) {
                burning.put(point3D, when+ tntFuseDuration());
            }
        }
    }

    public double tntFuseDuration()
    {
        return 2 + (rand.nextInt(8)/8.0);
    }

    public static class ExplosionReuse
    {
        List<Map.Entry<Point3Di, Double>> sequence = new ArrayList<Map.Entry<Point3Di, Double>>();
        protected double EXPLOSION_DURATION=0.8;

        public double horizon()
        {
            if (sequence.isEmpty())
                return 0;
            else {
                return sequence.get(sequence.size()-1).getValue() + EXPLOSION_DURATION;
            }
        }

        public String toString()
        {
            StringBuilder rval = new StringBuilder();

            List<String> parts = new ArrayList<String>(sequence.size());
            for (Map.Entry<Point3Di, Double> exp : sequence) {
                Point3Di loc = exp.getKey();
                parts.add("(" + loc.x + "," + loc.y + "," + loc.z + ", " + exp.getValue() + ")");
            }

            rval.append("explosionSequence(");
            RAMBlockEditor.appendJoin(rval, ",\n\t", parts);
            rval.append(")\n");

            return rval.toString();
        }

        public void addExplosion(Map.Entry<Point3Di, Double> exp)
        {
            sequence.add(exp);
        }
    }

    public static class BlockDestructionSet
    {
        List<Map.Entry<BlenderMeshElement, Double>> parts = new ArrayList<Map.Entry<BlenderMeshElement, Double>>();

        public void register(BlenderMeshElement meshPart, double disappearTime)
        {
            parts.add(new AbstractMap.SimpleEntry<BlenderMeshElement, Double>(meshPart, disappearTime));
        }

        public String emitDestructionMeshes(Point3Di sectionCoords)
        {
            Collections.sort(parts, new Comparator<Map.Entry<BlenderMeshElement, Double>>()
            {
                @Override
                public int compare(Map.Entry<BlenderMeshElement, Double> a, Map.Entry<BlenderMeshElement, Double> b)
                {
                    return a.getValue().compareTo(b.getValue());
                }
            });

            StringBuilder rval = new StringBuilder();

            List<BlenderMeshElement> faces = new ArrayList<BlenderMeshElement>();

            double curr=-1;
            for (Map.Entry<BlenderMeshElement, Double> en : parts) {

                if (faces.size()==0)
                    curr = en.getValue();

                if (en.getValue().doubleValue() != curr) {
                    // emit
                    if (faces.size()>0) {
                        rval.append(emitter(sectionCoords, faces, curr));
                    }
                    faces = new ArrayList<BlenderMeshElement>();
                    curr = en.getValue();
                }

                faces.add(en.getKey());
            }

            if (faces.size()>0) {
                rval.append(emitter(sectionCoords, faces, curr));
            }

            return rval.toString();
        }

        public String emitter(Point3Di sectionCoords, List<BlenderMeshElement> faces, double destructionTime)
        {
            StringBuilder rval = new StringBuilder("sectionFaces = [");
            RAMBlockEditor.appendJoin(rval, ",\n\t", faces);
            rval.append("\n\t]\n" +
                "buildBlockDestructionMesh(sectionFaces, (" + sectionCoords.x
                + "," + sectionCoords.y
                + "," + sectionCoords.z + "), " + destructionTime + ")\n");

            return rval.toString();
        }
    }
}

