package com.purplefrog.minecraftExplorer;

import org.jnbt.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/4/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Anvil
{
    private CompoundTag root;


    public Anvil(CompoundTag root)
    {
        //To change body of created methods use File | Settings | File Templates.
        this.root = root;
    }

    public static Tag deref(CompoundTag root, String... keys)
    {
        CompoundTag cursor = root;

        int i=0;
        while (i<keys.length) {
            Tag t = cursor.getValue().get(keys[i]);
            i++;
            if (i>= keys.length) {
                return t;
            } else if (t instanceof CompoundTag) {
                cursor = (CompoundTag) t;
            } else {
                return null; // path cut short
            }
        }

        return cursor;
    }

    public List<Section> getSections()
    {
        ListTag sections = getSections_();
        ArrayList<Section> rval = new ArrayList<Section>();
        for (Tag tag : sections.getValue()) {
            rval.add(new Section((CompoundTag) tag));
        }
        return rval;
    }

    private ListTag getSections_()
    {
        return (ListTag) Anvil.deref(root, "Level", "Sections");
    }

    public Section getSectionFor(int y)
    {
        for (Tag tag : getSections_().getValue()) {
            Section s = new Section((CompoundTag) tag);

            if (s.getY() == y>>4) {
                return s;
            }
        }

        return null;
    }

    public Section getOrCreateSectionFor(int y)
    {
        Section rval = getSectionFor(y);
        if (null==rval) {

            TreeMap<String, Tag> map = new TreeMap<String, Tag>();
            CompoundTag tag = new CompoundTag(null, map);
            map.put("Blocks", new ByteArrayTag("Blocks", new byte[16*16*16]));
            map.put("Y", new ByteTag("Y", (byte) (y>>4)));
            map.put("Data", new ByteArrayTag("Data", new byte[16*16*8]));
            map.put("SkyLight", new ByteArrayTag("SkyLight", new byte[16*16*8]));
            map.put("BlockLight", new ByteArrayTag("BlockLight", new byte[16*16*8]));

            ListTag sections = getSections_();
            sections.getValue().add(tag);

            rval = new Section(tag);
        }
        return rval;
    }

    public static class Section
    {

        private CompoundTag tag;

        public Section(CompoundTag tag)
        {

            this.tag = tag;
        }

        public byte getY()
        {
            return ((ByteTag) getProp("Y")).getValue();
        }

        private Tag getProp(String propName)
        {
            return tag.getValue().get(propName);
        }

        public byte[] getBlocks()
        {
            return ((ByteArrayTag) getProp("Blocks")).getValue();
        }
    }
}
