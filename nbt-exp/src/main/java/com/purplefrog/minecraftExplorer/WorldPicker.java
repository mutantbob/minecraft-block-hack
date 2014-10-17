package com.purplefrog.minecraftExplorer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/11/13
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorldPicker
{
    public static File pickSaveDir()
    {
        File saves = savesDir();

        if (false)
            return new File(saves, "snapshot");
        if (false)
            return new File(saves, "2013-09-scratch");
        return menger5();
    }

    public static File menger5()
    {
        return new File(savesDir(), "menger-5");
    }

    public static File savesDir()
    {
        return new File(System.getProperty("user.home"), ".minecraft/saves");
    }
}
