package cs5625.fancyplane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cs5625.gfx.json.NamedObject;
import cs5625.gfx.material.Material;
import cs5625.gfx.mesh.MeshPart;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Holder;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyObject {
	protected Vector3f forward;
	
	protected int health;
	public int getHealth() { return health; }
	public void setHealth(int value) { health = value; node.setScale(health > 0 ? 1.0f : 0.001f); }
	
	protected float collisionRadius;
	public float getCollisionRadius() { return collisionRadius; }
	public void setCollisionRadius(float value) { collisionRadius = value; }
	
	protected Vector3f velocity;
	public Vector3f getVelocity() { return velocity; }
	public void setVelocity(Vector3f value) { velocity.set(value); }
	
	protected float velocityDampeningFactor;
	public float getVelocityDampeningFactor() { return velocityDampeningFactor; }
	public void setVelocityDampeningFactor(float value) { velocityDampeningFactor = value; }
	
	protected Point3f position;
	public Point3f getPosition() { return position; }
	public void setPosition(Point3f value) { position.set(value); }
	
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
		fancyMesh.setCastsShadow(true);
		
		if(this.hasSpecialMaterial()) {
			MeshPart fancyPart = meshes.get(0).getPart(0);
			fancyPart.material = this.getSpecialMaterial();
		}
		
		node = new SceneTreeNode();
		node.setData(new Value<NamedObject>(fancyMesh));
		Quat4f rotation = new Quat4f();
		rotation.set(new AxisAngle4f(new Vector3f(0.0f, 1.0f, 0.0f), (float)Math.PI / 2));
		node.setOrientation(rotation);
		parentNode.addChild(node);
		
		this.team = team;
		
		collisionRadius = 0;
		velocityDampeningFactor = 1.0f;
		health = 1;
		velocity = new Vector3f(0,0,0);
		position = node.getPosition();
		
		forward = new Vector3f();
		if (team == FancyTeam.Player)
		{
			forward.x = 1;
		}
		else if (team == FancyTeam.Enemy)
		{
			forward.x = -1;
		}
	}
	
	protected boolean hasSpecialMaterial()
	{
		return false;		
	}
	
	protected Holder<Material> getSpecialMaterial()
	{
		return null;
	}
	
	public void update()
	{
		if (health > 0)
		{
			velocity.scale(velocityDampeningFactor);
			position.x += velocity.x;
			position.y += velocity.y;
			node.setPosition(position);
		}
		node.setScale(health > 0 ? 1.0f : 0.001f);
	}
	
	public boolean collidesWith(FancyObject other)
	{
		if (this.health > 0 && other.health > 0 && this.team != other.team)
		{
			float x = this.position.x - other.position.x;
			float y = this.position.y - other.position.y;
			float z = this.position.z - other.position.z;
			if (x * x + y * y + z * z < this.collisionRadius + other.collisionRadius)
			{
				return true;
			}
		}
		return false;
	}
}
