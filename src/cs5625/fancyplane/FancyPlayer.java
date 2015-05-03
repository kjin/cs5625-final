package cs5625.fancyplane;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs5625.gfx.json.NamedObject;
import cs5625.gfx.material.BlinnPhongMaterial;
import cs5625.gfx.material.Material;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyPlayer {
	
	SceneTreeNode node;
	
	Vector2f velocity;
	
	boolean upPressed = false;
	boolean downPressed = false;
	boolean rightPressed = false;
	boolean leftPressed = false;
	
	final float X_SPEED = 0.4f;
	final float Y_SPEED = 0.4f;
	
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
		
		node = new SceneTreeNode();
		node.setData(new Value<NamedObject>(fancyMesh));
		Quat4f rotation = new Quat4f();
		rotation.set(new AxisAngle4f(new Vector3f(0.0f, 1.0f, 0.0f), (float)Math.PI / 2));
		node.setOrientation(rotation);
		rootNode.addChild(node);
		
		velocity = new Vector2f(0,0);
	}
	
	public void update()
	{
		Point3f position = node.getPosition();
		
		position.x += velocity.x;
		position.y += velocity.y;
		
		node.setPosition(position);
	}
	
	public void upPressed()
	{
		if(!upPressed)
		{
			velocity.y += Y_SPEED;
			upPressed = true;
		}	
	}
	
	public void downPressed()
	{
		if(!downPressed)
		{
			velocity.y += -Y_SPEED;
			downPressed = true;
		}
	}
	
	public void rightPressed()
	{
		if(!rightPressed)
		{
			velocity.x += X_SPEED;
			rightPressed = true;
		}	
	}
	
	public void leftPressed()
	{
		if(!leftPressed)
		{
			velocity.x += -X_SPEED;
			leftPressed = true;
		}
	}
	
	public void upReleased()
	{
		if(upPressed)
		{
			velocity.y -= Y_SPEED;
			upPressed = false;
		}	
	}
	
	public void downReleased()
	{
		if(downPressed)
		{
			velocity.y += Y_SPEED;
			downPressed = false;
		}	
	}
	
	public void rightReleased()
	{
		if(rightPressed)
		{
			velocity.x -= X_SPEED;
			rightPressed = false;
		}	
	}
	
	public void leftReleased()
	{
		if(leftPressed)
		{
			velocity.x += X_SPEED;
			leftPressed = false;
		}
	}
}
