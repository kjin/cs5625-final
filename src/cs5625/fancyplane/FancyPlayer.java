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

public class FancyPlayer extends FancyObject
{
	FancyBulletManager bulletManager;
	
	boolean upPressed = false;
	boolean downPressed = false;
	boolean rightPressed = false;
	boolean leftPressed = false;
	boolean spacePressed = false;
	
	int fireTime;
	
	final int FIRE_RATE = 8;
	
	final float X_SPEED_CAP = 0.4f;
	final float Y_SPEED_CAP = 0.4f;
	final float ACCELERATION = 0.1f;
	final float DAMPENING = 0.8f;
	
	final float UP_BOUND = 10;
	final float DOWN_BOUND = -10;
	final float RIGHT_BOUND = 10;
	final float LEFT_BOUND = -10;
	
	public FancyPlayer(SceneTreeNode parentNode, FancyBulletManager bulletManager)
	{
		super(parentNode, "fancy-plane", FancyTeam.Player);
		this.bulletManager = bulletManager;
		this.velocityDampeningFactor = DAMPENING;
		fireTime = 0;
	}
	
	public void update()
	{
		if (leftPressed)
		{
			velocity.x = Math.max(velocity.x - ACCELERATION, -X_SPEED_CAP);
		}
		if (rightPressed)
		{
			velocity.x = Math.min(velocity.x + ACCELERATION, X_SPEED_CAP);
		}
		if (downPressed)
		{
			velocity.y = Math.max(velocity.y - ACCELERATION, -Y_SPEED_CAP);
		}
		if (upPressed)
		{
			velocity.y = Math.min(velocity.y + ACCELERATION, Y_SPEED_CAP);
		}
		if (spacePressed)
		{
			if (fireTime == 0)
			{
				bulletManager.fireBullet(this);
			}
			fireTime++;
			if (fireTime == FIRE_RATE)
			{
				fireTime = 0;
			}
		}
		else
		{
			fireTime = 0;
		}
		position.x = Math.min(Math.max(position.x, LEFT_BOUND), RIGHT_BOUND);
		position.y = Math.min(Math.max(position.y, DOWN_BOUND), UP_BOUND);
		super.update();
	}
	
	public void upPressed()
	{
		upPressed = true;
	}
	
	public void downPressed()
	{
		downPressed = true;
	}
	
	public void rightPressed()
	{
		rightPressed = true;
	}
	
	public void leftPressed()
	{
		leftPressed = true;
	}
	
	public void spacePressed()
	{
		spacePressed = true;
	}
	
	public void upReleased()
	{
		upPressed = false;
	}
	
	public void downReleased()
	{
		downPressed = false;
	}
	
	public void rightReleased()
	{
		rightPressed = false;
	}
	
	public void leftReleased()
	{
		leftPressed = false;
	}
	
	public void spaceReleased()
	{
		spacePressed = false;
	}
}
