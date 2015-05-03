package cs5625.fancyplane;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cs5625.gfx.json.NamedObject;
import cs5625.gfx.material.BlinnPhongMaterial;
import cs5625.gfx.material.Material;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyPlayer {
	public FancyPlayer(SceneTreeNode rootNode)
	{
    	ArrayList<TriMesh> meshes = new ArrayList<TriMesh>();
		try {
			meshes.addAll(WavefrontObjToTriMeshConverter.load("data/models/fancy-plane.obj", true, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TriMesh fancyMesh = meshes.get(0);
		
		SceneTreeNode node = new SceneTreeNode();
		node.setData(new Value<NamedObject>(fancyMesh));
		Quat4f rotation = new Quat4f();
		rotation.set(new AxisAngle4f(new Vector3f(0.0f, 1.0f, 0.0f), (float)Math.PI / 2));
		node.setOrientation(rotation);
		rootNode.addChild(node);
	}
}
