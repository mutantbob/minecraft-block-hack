package com.purplefrog.minecraftExplorer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/30/13
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeMaterials
{

    public BlockPlusData log;
    public BlockPlusData planks;
    public BlockPlusData leaves;

    public final static TreeMaterials OAK = new TreeMaterials(17, 0, 0, 18, 0);
    public final static TreeMaterials SPRUCE = new TreeMaterials(17, 1, 1, 18, 1);
    public final static TreeMaterials BIRCH = new TreeMaterials(17, 2, 2, 18, 2);
    public final static TreeMaterials JUNGLE = new TreeMaterials(17, 3, 3, 18, 3);
    public final static TreeMaterials ACACIA = new TreeMaterials(162, 0, 4, 161, 0);
    public final static TreeMaterials DARK_OAK = new TreeMaterials(162, 1, 5, 161, 1);

    public final static TreeMaterials[] ALL = {
        OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK
    };

    public static TreeMaterials randomTree(Random rand)
    {
        return ALL[rand.nextInt(ALL.length)];
    }

    /**
     * for version 1.6 of minecraft which doesn't have Acacia or Dark Oak.
     */
    public static TreeMaterials randomTree16(Random rand)
    {
        return ALL[rand.nextInt(4)];
    }

    public TreeMaterials(int logBT, int logDetail, int planks, int leavesBT, int leavesDetail)
    {
        log = new BlockPlusData(logBT, logDetail&3);
        this.planks = new BlockPlusData(5, planks);
        this.leaves = new BlockPlusData(leavesBT, leavesDetail);
    }

    public BlockPlusData logVertical()
    {
        return logOrient(LogOrientation.UD);
    }

    private BlockPlusData logOrient(LogOrientation orient)
    {
        return new BlockPlusData(log.blockType, log.data | (orient.mask));
    }

    public enum LogOrientation
    {
        UD(0), EW(4), NS(8), ZZ(12);
        public final int mask;

        private LogOrientation(int mask)
        {
            this.mask = mask;
        }
    }
}
