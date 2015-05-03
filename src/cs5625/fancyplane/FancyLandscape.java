package cs5625.fancyplane;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cs5625.gfx.json.NamedObject;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyLandscape {
	SceneTreeNode[] nodes;
	
	float WIDTH = 40;
	float minBound;
	
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
		
		nodes = new SceneTreeNode[3];
		for (int i = 0; i < nodes.length; i++)
		{
			nodes[i] = new SceneTreeNode();
			nodes[i].setData(new Value<NamedObject>(fancyMesh));
			nodes[i].setPosition(WIDTH * (i - nodes.length / 2.0f), -5, 0);
			rootNode.addChild(nodes[i]);
		}
		minBound = nodes[0].getPosition().y - 1.5f * WIDTH;
	}
	
	public void update()
	{
		for (int i = 0; i < nodes.length; i++)
		{
			Point3f position = nodes[i].getPosition();
			position.x = position.x - 0.5f;
			if (position.x < minBound)
			{
				position.x += WIDTH * nodes.length;
			}
			nodes[i].setPosition(position);
		}
	}
}
