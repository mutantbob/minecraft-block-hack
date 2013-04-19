package com.purplefrog.minecraftExplorer;

import junit.framework.*; /**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/18/13
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestBlockRotate
    extends TestCase
{

    public void test1()
    {
        BlockTemplate t1 = new BlockTemplate(109);
        BlockTemplate t2 = t1.rot90();
        BlockTemplate t3 = t2.rot90();
        BlockTemplate t4 = t3.rot90();
        BlockTemplate t5 = t4.rot90();

        BlockTemplate [] seq = { t1, t2, t3, t4 };

        assertEquals(2, t2.getBlock(0, 0, 0).data);
        assertEquals(1, t3.getBlock(0,0,0).data);
        assertEquals(3, t4.getBlock(0,0,0).data);

        assertEquals(t5, t1);

        for (int i=0; i<4; i++) {
            assertEquals(seq[i].rot90(), seq[(i+1)%4]);
            assertEquals(seq[i].rot180(), seq[(i+2)%4]);
            assertEquals(seq[i].rot270(), seq[(i+3)%4]);
        }
    }
}
