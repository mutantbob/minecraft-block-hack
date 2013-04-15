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
        return new File(System.getProperty("user.home"), ".minecraft/saves/menger-5");
    }
}
