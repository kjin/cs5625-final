package cs5625.fancyplane;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyBullet extends FancyObject
{
	AxisAngle4f orientationTransform;
	
	protected int lifetime;
	public int getLifetime() { return lifetime; }
	public void setLifetime(int value) { lifetime = value; }
	
	public FancyBullet(SceneTreeNode parentNode)
	{
		super(parentNode, "fancy-bullet", FancyTeam.Neutral);
		orientationTransform = new AxisAngle4f();
		orientationTransform.x = 0;
		orientationTransform.y = 1;
		orientationTransform.z = 0;
		
		lifetime = 0;
		visible = false;
	}
	
	public void update()
	{
		if (lifetime > 0)
		{
			visible = true;
			orientationTransform.angle += 0.1f;
			Quat4f quat = node.getOrientation();
			quat.set(orientationTransform);
			node.setOrientation(quat);
			lifetime--;
		}
		else if (visible)
		{
			visible = false;
		}
		super.update();
	}
}
