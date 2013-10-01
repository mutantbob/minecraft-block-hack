package com.purplefrog.minecraftExplorer;

import junit.framework.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 6/26/13
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestMeshConsolidate
    extends TestCase
{

    public void test1()
    {

        List<BlenderMeshElement> in = Arrays.asList((BlenderMeshElement) new BlenderMeshElement.Face(0,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(0,1,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(0,2,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(0,3,0, 1, 0, FaceSide.NORTH)
        );
        List<BlenderMeshElement> out = BlenderBlockEditor.consolidateFaces(new ArrayList<BlenderMeshElement>(in));

        assertEquals(1, out.size());
        assertEquals(new BlenderMeshElement.FatFace(0,0,0, 1, 0, FaceSide.NORTH, 1, 4), out.get(0));
    }

    public void test2()
    {

        List<BlenderMeshElement> in = Arrays.asList((BlenderMeshElement)
            new BlenderMeshElement.Face(0,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(1,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(2,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(3,0,0, 1, 0, FaceSide.NORTH)
        );
        List<BlenderMeshElement> out = BlenderBlockEditor.consolidateFaces(new ArrayList<BlenderMeshElement>(in));

        assertEquals(1, out.size());
        assertEquals(new BlenderMeshElement.FatFace(3,0,0, 1, 0, FaceSide.NORTH, 4,1), out.get(0));
    }


    public void test3()
    {

        List<BlenderMeshElement> in = Arrays.asList((BlenderMeshElement)
            new BlenderMeshElement.Face(0,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(1,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(2,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(3,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(0,1,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(1,1,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(2,1,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(3,1,0, 1, 0, FaceSide.NORTH)
        );
        List<BlenderMeshElement> out = BlenderBlockEditor.consolidateFaces(new ArrayList<BlenderMeshElement>(in));

        assertEquals(1, out.size());
        assertEquals(new BlenderMeshElement.FatFace(3,0,0, 1, 0, FaceSide.NORTH, 4,2), out.get(0));
    }

    public void test4()
    {

        List<BlenderMeshElement> in = Arrays.asList((BlenderMeshElement)
            new BlenderMeshElement.Face(2,1,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(0,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(2,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(3,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(0,1,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(1,1,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(1,0,0, 1, 0, FaceSide.NORTH),
            new BlenderMeshElement.Face(3,1,0, 1, 0, FaceSide.NORTH)
        );
        List<BlenderMeshElement> out = BlenderBlockEditor.consolidateFaces(new ArrayList<BlenderMeshElement>(in));

        assertEquals(1, out.size());
        assertEquals(new BlenderMeshElement.FatFace(3,0,0, 1, 0, FaceSide.NORTH, 4,2), out.get(0));
    }


    public void test5()
    {
        List<BlenderMeshElement> in = new ArrayList<BlenderMeshElement>();

        for (int u = 0; u<3; u++) {
            for (int v=0; v<3; v++) {
                in.add(new BlenderMeshElement.Face(u,v,0, 98,3, FaceSide.NORTH));
                in.add(new BlenderMeshElement.Face(u,v,2, 98,3, FaceSide.SOUTH));
                in.add(new BlenderMeshElement.Face(u,0,v, 98,3, FaceSide.BOTTOM));
                in.add(new BlenderMeshElement.Face(u,2,v, 98,3, FaceSide.TOP));
                in.add(new BlenderMeshElement.Face(0,u,v, 98,3, FaceSide.WEST));
                in.add(new BlenderMeshElement.Face(2,u,v, 98,3, FaceSide.EAST));
            }
        }


        List<BlenderMeshElement> out = BlenderBlockEditor.consolidateFaces(new ArrayList<BlenderMeshElement>(in));

        for (BlenderMeshElement bme : out) {
            System.out.println(bme.asPython());
        }
    }
}
