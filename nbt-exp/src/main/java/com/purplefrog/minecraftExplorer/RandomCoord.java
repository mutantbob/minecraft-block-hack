package com.purplefrog.minecraftExplorer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/8/13
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomCoord
{
    public int n=0;

    public int x,y;

    Random rand ;

    public RandomCoord(Random rand)
    {
        this.rand = rand;
    }

    public RandomCoord()
    {
        this(new Random());
    }

    public void addCoord(int x, int y)
    {
        if (n <= 0 || rand.nextInt(n) == 0) {
            this.x = x;
            this.y = y;
        }
        n++;
    }


}
