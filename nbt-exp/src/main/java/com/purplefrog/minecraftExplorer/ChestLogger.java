package com.purplefrog.minecraftExplorer;

import java.util.*;

/**
* Created with IntelliJ IDEA.
* User: thoth
* Date: 9/20/13
* Time: 8:11 AM
* To change this template use File | Settings | File Templates.
*/
public class ChestLogger
{
    public boolean quiet = true;
    public Map<Integer, int[]> totals= new TreeMap<Integer, int[]>();
    public int chestCount=0;
    public String label = "chest";

    PerChestLogger foundChest(int x,int y,int z)
    {
        chestCount++;
        return new PerChestLogger(x,y,z);
    }

    public void accumForTotal(int id, int count)
    {
        int[] bucket = totals.get(id);

        if (bucket==null) {
            bucket = new int[]{0};
            totals.put(id, bucket);
        }

        bucket[0] += count;
    }

    public void finish()
    {
        System.out.println("totals from " +chestCount+ " " + label +"s:");
        for (Map.Entry<Integer, int[]> en : totals.entrySet()) {
            System.out.println("  "+en.getKey()+" x"+en.getValue()[0]);
        }
    }

    public class PerChestLogger
    {
        int uselessCount=0;

        public PerChestLogger(int x, int y, int z)
        {
            System.out.println(label + " at " + x + "," + y +","+z);
        }

        public void logSlot(int id, int count, int slot)
        {
            accumForTotal(id, count);

            if (id==362 || id==360 || id==318)
                System.out.println("  "+id+" x"+count+" @"+slot);
            else
                uselessCount++;
        }

        public void finish()
        {
            if (uselessCount>0 ) {
                if (!quiet)
                    System.out.println(" useless crap x"+uselessCount);
            }
        }
    }
}
