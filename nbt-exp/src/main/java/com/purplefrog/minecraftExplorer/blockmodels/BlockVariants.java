package com.purplefrog.minecraftExplorer.blockmodels;

import java.util.*;

/**
 * Created by thoth on 9/26/14.
 */
public class BlockVariants
{
    private final List<OneBlockModel.Named> variants;

    public BlockVariants(List<OneBlockModel.Named> v2)
    {
        this.variants = v2;
    }

    public static Random rand = new Random();

    public OneBlockModel getVariant(int blockType, int blocKData)
    {

        List<OneBlockModel> x=null;
        
//        x = variants.get("normal");
        if (null==x) {
            // XXX not right
            x = variants.get(0).model;
        }

        int idx = rand.nextInt(x.size());
        return x.get(idx);
    }
}
