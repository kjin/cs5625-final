package cs5625.fancyplane;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cs5625.gfx.json.NamedObject;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyLandscape {
	public FancyLandscape(SceneTreeNode rootNode)
	{
    	ArrayList<TriMesh> meshes = new ArrayList<TriMesh>();
		try {
			meshes.addAll(WavefrontObjToTriMeshConverter.load("data/models/fancy-landscape.obj", true, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TriMesh fancyMesh = meshes.get(0);
		
		SceneTreeNode node = new SceneTreeNode();
		node.setData(new Value<NamedObject>(fancyMesh));
		node.setPosition(0, -5, 0);
		rootNode.addChild(node);
	}
}
