package com.purplefrog.minecraftExplorer.blockmodels;

import org.json.*;

import java.util.*;

/**
 * Created by thoth on 9/26/14.
 */
public class JSONIterable
    implements Iterable<String>
{
    private final JSONObject obj;

    public JSONIterable(JSONObject object)
    {
        this.obj = object;
    }

    @Override
    public Iterator<String> iterator()
    {
        return obj.keys();
    }
}
