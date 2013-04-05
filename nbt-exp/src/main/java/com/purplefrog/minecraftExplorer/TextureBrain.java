package com.purplefrog.minecraftExplorer;

import com.purplefrog.jwavefrontobj.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/4/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextureBrain
{
    private File outDir;

    public TextureBrain(File outDir)
    {
        this.outDir = outDir;
    }

    public String getMaterial(int bt0, MaterialLib mLib)
    {
        String rval = "bt" + bt0;
        if (mLib.hasMaterial(rval)) {
            return rval;
        } else {
            return null;
        }
    }

    public String getMaterialTop(int bt0, MaterialLib mLib)
    {
        String rval = "bt" + bt0+"_top";
        if (mLib.hasMaterial(rval)) {
            return rval;
        }

        rval = "bt"+bt0;
        if (mLib.hasMaterial(rval)) {
            return rval;
        }

        return null;
    }

    public String getMaterial(int blockType, MaterialLib mLib, boolean top)
    {
        return top ? getMaterialTop(blockType, mLib)
            : getMaterial(blockType, mLib);
    }
}
