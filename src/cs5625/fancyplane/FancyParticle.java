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
	public int id;
	
	public FancyParticle(int id)
	{
		position = new Point3f();
		velocity = new Vector3f();
		lifespan = 0;
	}
	
	public void update()
	{
		if (lifespan > 0)
		{
			velocity.setY(velocity.y + 0.001f);
			position.add(velocity);
			lifespan--;
			if (lifespan == 0)
			{
			}
		}
	}
}
