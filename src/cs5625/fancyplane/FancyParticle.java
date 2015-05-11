package cs5625.fancyplane;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cs5625.gfx.json.NamedObject;
import cs5625.gfx.material.Material;
import cs5625.gfx.material.SmokeParticleMaterial;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyParticle
{
	SceneTreeNode node;
	
	public Point3f position;
	public Vector3f velocity;
	public float lifespan;
	
	public FancyParticle(SceneTreeNode parentNode)
	{
		position = new Point3f();
		velocity = new Vector3f();
		lifespan = 0;
		
		ArrayList<TriMesh> meshes = new ArrayList<TriMesh>();
		try {
			meshes.addAll(WavefrontObjToTriMeshConverter.load("data/models/fancy-unitcube.obj", false, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		TriMesh fancyMesh = meshes.get(0);
		SmokeParticleMaterial mat = new SmokeParticleMaterial();
		mat.setDiffuseColor(new Color4f(0.2f + (float)Math.random() / 3.0f, 0.2f, 0.2f, 0.1f));
		fancyMesh.getPart(0).material = new Value<Material>(mat);
		
		node = new SceneTreeNode();
		node.setData(new Value<NamedObject>(fancyMesh));
		node.setScale(0.0001f); // since particle is invisible for now
		parentNode.addChild(node);
	}
	
	public void update()
	{
		if (lifespan > 0)
		{
			velocity.setY(velocity.y + 0.001f);
			position.add(velocity);
			node.setPosition(position);
			node.setScale(0.1f);
			lifespan--;
			if (lifespan == 0)
			{
				node.setScale(0.0001f);
			}
		}
	}
}
