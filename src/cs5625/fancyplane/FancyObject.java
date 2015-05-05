package cs5625.fancyplane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.apache.commons.io.FilenameUtils;

import cs5625.gfx.gldata.FileTexture2DData;
import cs5625.gfx.gldata.Texture2DData;
import cs5625.gfx.json.NamedObject;
import cs5625.gfx.material.Material;
import cs5625.gfx.material.XToonMaterial;
import cs5625.gfx.mesh.MeshPart;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Reference;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyObject {
	protected Vector3f velocity;
	public Vector3f getVelocity() { return velocity; }
	public void setVelocity(Vector3f value) { velocity.set(value); }
	
	protected float velocityDampeningFactor;
	public float getVelocityDampeningFactor() { return velocityDampeningFactor; }
	public void setVelocityDampeningFactor(float value) { velocityDampeningFactor = value; }
	
	protected Point3f position;
	public Point3f getPosition() { return position; }
	public void setPosition(Point3f value) { position.set(value); }
	
	protected boolean visible;
	public boolean getVisibility() { return visible; }
	public void setVisibility(boolean value) { visible = value; node.setScale(visible ? 1.0f : 0.001f); }
	
	protected FancyTeam team;
	public FancyTeam getTeam() { return team; }
	public void setTeam(FancyTeam value) { team = value; }
	
	protected SceneTreeNode node;
	
	public FancyObject(SceneTreeNode parentNode, String modelName, FancyTeam team)
	{
		ArrayList<TriMesh> meshes = new ArrayList<TriMesh>();
		try {
			meshes.addAll(WavefrontObjToTriMeshConverter.load("data/models/" + modelName + ".obj", true, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		TriMesh fancyMesh = meshes.get(0);
		
		node = new SceneTreeNode();
		node.setData(new Value<NamedObject>(fancyMesh));
		Quat4f rotation = new Quat4f();
		rotation.set(new AxisAngle4f(new Vector3f(0.0f, 1.0f, 0.0f), (float)Math.PI / 2));
		node.setOrientation(rotation);
		parentNode.addChild(node);
		
		this.team = team;
		
		velocityDampeningFactor = 1.0f;
		velocity = new Vector3f(0,0,0);
		position = node.getPosition();
		visible = true;
	}
	
	public void update()
	{
		if (visible)
		{
			velocity.scale(velocityDampeningFactor);
			position.x += velocity.x;
			position.y += velocity.y;
			node.setPosition(position);
		}
		node.setScale(visible ? 1.0f : 0.001f);
	}
	
	public void DoWork()
	{
		System.out.println("Base class");
	}
}
