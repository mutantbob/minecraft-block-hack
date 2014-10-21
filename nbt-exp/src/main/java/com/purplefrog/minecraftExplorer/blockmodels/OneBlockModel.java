package com.purplefrog.minecraftExplorer.blockmodels;

import com.purplefrog.jwavefrontobj.*;
import com.purplefrog.minecraftExplorer.*;
import org.apache.log4j.*;
import org.json.*;

import java.io.*;
import java.util.*;

/**
 * Created by thoth on 9/25/14.
 */
public class OneBlockModel
{
    private static final Logger logger = Logger.getLogger(OneBlockModel.class);
    private final int yRotation;
    private final int xRotation;

    public Map<String, String> textures =new TreeMap<String, String>();
    public List<BlockElement> elements = new ArrayList<BlockElement>();

    public OneBlockModel(int yRotation, int xRotation)
    {
        this.yRotation = yRotation;
        this.xRotation = xRotation;
    }

    public void getMeshElements(List<BlenderMeshElement> accum, int x, int y, int z, BlockEnvironment env)
    {
        switch ( (yRotation+45)%360 / 90) {
            case 1:
                env = env.rotated90();
                break;
            case 2:
                env = env.rotated180();
                break;
            case 3:
                env = env.rotated270();
                break;
        }
        List<GLPoly> polys = new ArrayList<GLPoly>();
        for (BlockElement element : elements) {
            element.getPolys(polys, this, env);
        }

        if (polys.isEmpty())
            return;

        if (xRotation != 0) {
            RotationSpec spec = new RotationSpec(new Point3D(8,8,8), "x", xRotation, false);
            transformVertices(polys, spec);
//            System.out.println(yRotation);
        }

        if (yRotation != 0) {
            RotationSpec spec = new RotationSpec(new Point3D(8,8,8), "y", yRotation, false);
            transformVertices(polys, spec);
//            System.out.println(yRotation);
        }

        for (GLPoly poly : polys) {

            BlenderMeshElement me = new BaconMeshElement(poly, x,y,z);
            accum.add(me);
        }
    }

    public static void transformVertices(List<GLPoly> polys, RotationSpec spec)
    {
        for (GLPoly poly : polys) {
            for (int i = 0; i < poly.verts.length; i++) {
                poly.verts[i] = spec.transform(poly.verts[i]);
            }
        }
    }

    protected static Random rand = new Random();

    public static BlockVariants parse(BlockModels.Resources resources, String path)
        throws JSONException, IOException
    {
        InputStream istr = resources.getBlockstate(path);

        JSONObject obj = new JSONObject(new JSONTokener(new InputStreamReader(istr)));

        istr.close();

        JSONObject variants = obj.optJSONObject("variants");

        if (variants==null) {
            throw new UnsupportedOperationException("NYI "+path);
        } else {
            List<Named> v2 = new ArrayList<Named>();

            for (String key : new JSONIterable(variants)) {
                v2.add(new Named(key, modelOptions(resources, variants.get(key))));
            }
            return new BlockVariants(v2);
        }
    }

    public static List<OneBlockModel> modelOptions(BlockModels.Resources resources, Object normal_)
        throws FileNotFoundException, JSONException
    {
        List<OneBlockModel> rval = new ArrayList<OneBlockModel>();
        if (normal_ instanceof JSONArray) {
            JSONArray normal = (JSONArray) normal_;

            for (int i=0; i<normal.length(); i++) {
                rval.add( parseModelRef(resources, normal.getJSONObject(i)));
            }
        } else {
            rval.add(parseModelRef(resources, (JSONObject)normal_));
        }

        return rval;
    }


    protected static OneBlockModel parseModelRef(BlockModels.Resources resources, JSONObject spec)
        throws JSONException, FileNotFoundException
    {
        int y = spec.optInt("y", 0);
        int x = spec.optInt("x", 0);

        String model = spec.getString("model");
        return parseModel(resources, "block/"+model, y, x);
    }

    public static OneBlockModel parseModel(BlockModels.Resources resources, String modelName, int yRotation, int xRotation)
        throws JSONException, FileNotFoundException
    {
        InputStream istr = resources.getBlockModel(modelName);

        JSONObject root = new JSONObject(new JSONTokener(new InputStreamReader(istr)));

        String parent = root.optString("parent", null);

        OneBlockModel rval;
        if (null != parent) {
            rval = parseModel(resources, parent, yRotation, xRotation);
        } else {
            rval = new OneBlockModel(yRotation, xRotation);
        }
        rval.load(root);

        return rval;
    }

    private void load(JSONObject root)
        throws JSONException
    {
        JSONObject textures = root.optJSONObject("textures");

        if (textures != null) {
            Iterator iter = textures.keys();
            while (iter.hasNext()) {
                String key = iter.next().toString();
                this.textures.put(key, textures.optString(key));
            }
        } else {
            logger.debug("no textures");
        }

        JSONArray elements = root.optJSONArray("elements");

        if (elements==null) {
            // provided by a base or derived class?
        } else {
            for (int i=0; i<elements.length(); i++) {
                BlockElement parseElement = parseElement(elements.getJSONObject(i));
                this.elements.add(parseElement);
            }
        }
    }

    private BlockElement parseElement(JSONObject element)
        throws JSONException
    {
        JSONArray from_ = element.getJSONArray("from");
        JSONArray to_ = element.getJSONArray("to");
        Point3Di from = parsePointI(from_);
        Point3Di to = parsePointI(to_);

        Map<String, FaceSpec> faces = new TreeMap<String, FaceSpec>();
        JSONObject faces_ = element.getJSONObject("faces");
        for (String key : new JSONIterable(faces_)) {
            faces.put(key, FaceSpec.parse(faces_.getJSONObject(key)));
        }

        RotationSpec rotation;
        {
            JSONObject rotation_ = element.optJSONObject("rotation");
            if (rotation_==null)
                rotation = null;
            else
                rotation = RotationSpec.parse(rotation_);
        }

        return new BlockElement(from, to, faces, rotation);
    }

    public static Point3Di parsePointI(JSONArray coords)
        throws JSONException
    {
        int x = coords.getInt(0);
        int y = coords.getInt(1);
        int z = coords.getInt(2);
        return new Point3Di(x,y,z);
    }

    public static Point3D parsePoint(JSONArray coords)
        throws JSONException
    {
        double x = coords.getDouble(0);
        double y = coords.getDouble(1);
        double z = coords.getDouble(2);
        return new Point3D(x,y,z);
    }

    public String resolveTexture(String textureName)
    {
        if (textureName.startsWith("#")) {
            return resolveTexture(textures.get(textureName.substring(1)));
        } else {
            return textureName;
        }
    }

    public static class BaconMeshElement
        extends BlenderMeshElement
    {
        private final GLPoly poly;

        public BaconMeshElement(GLPoly poly, int x, int y, int z)
        {
            super(x,y,z, 0,0);
            this.poly = poly;
            if (null==poly)
                throw new NullPointerException();
        }

        @Override
        public String asPython()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void accumOpenGL(ExportWebGL.GLStore glStore)
        {
            int[] vis = new int[poly.verts.length];
            for (int i=0; i<poly.verts.length; i++)  {
                Point3D v0 = poly.verts[i];
                double u = poly.uvs[i * 2 + 0];
                double v = poly.uvs[1 + i * 2];
                vis[i] = glStore.getVertex(v0.x+x, v0.y + y, v0.z + z, u, v);
            }
            glStore.addFace(new ExportWebGL.TextureForGL(poly.texture, -1), vis);
        }
    }


    public static class Named
    {
        String name;
        List<OneBlockModel> model;

        public Named(String name, List<OneBlockModel>model)
        {
            this.model = model;
            this.name = name;
        }
    }
}
