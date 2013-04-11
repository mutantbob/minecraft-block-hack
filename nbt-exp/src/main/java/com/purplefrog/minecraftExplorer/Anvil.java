package com.purplefrog.minecraftExplorer;

import com.mojang.nbt.*;

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
            if (null==cursor) {
                System.out.println("Debug");
            }
            Tag t = cursor.getValue().get(keys[i]);
            i++;

            if (t==null) {
                System.out.println("Debug");
            }
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
        ListTag<?> sections = getSections_();
        ArrayList<Section> rval = new ArrayList<Section>();
        for (Tag tag : sections.getValue()) {
            rval.add(new Section((CompoundTag) tag));
        }
        return rval;
    }

    private ListTag<?> getSections_()
    {
        return (ListTag) Anvil.deref(root, "Level", "Sections");
    }

    public Section getSectionFor(int y)
    {
        return getSectionForChunkY(y >> 4);
    }

    public Section getSectionForChunkY(int chunkY)
    {
        for (Tag tag : getSections_().getValue()) {
            Section s = new Section((CompoundTag) tag);

            if (s.getY() == chunkY) {
                return s;
            }
        }

        return null;
    }

    public Section getOrCreateSectionForRaw(int y)
    {
        Section rval = getSectionFor(y);
        if (null==rval) {

            CompoundTag tag = new CompoundTag(null);
            Map<String, Tag> map = tag.getValue();
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
            return ((ByteTag) getProp("Y")).data;
        }

        private Tag getProp(String propName)
        {
            return tag.getValue().get(propName);
        }

        public byte[] getBlocks_()
        {
            return ((ByteArrayTag) getProp("Blocks")).data;
        }

        public NibbleCube getBlockLight()
        {
            return new NibbleCube(((ByteArrayTag) getProp("BlockLight")).data);
        }

        public NibbleCube getSkyLight()
        {
            return new NibbleCube(((ByteArrayTag) getProp("SkyLight")).data);
        }

        public ByteCube getBlocks()
        {
             // XXX this ignores the "Add" tag,
             // which will become important when minecraft,
             // or an add-on exceeds 256 block types.
            return new ByteCube(getBlocks_());
        }

        public NibbleCube getData()
        {
            return new NibbleCube(getData_().data);
        }

        private ByteArrayTag getData_()
        {
            return ((ByteArrayTag)getProp("Data"));
        }
    }
}
