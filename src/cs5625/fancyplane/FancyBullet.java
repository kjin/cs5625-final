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
		health = 0;
	}
	
	public void update()
	{
		if (health > 0)
		{
			if (lifetime > 0)
			{
				orientationTransform.angle += 0.1f;
				Quat4f quat = node.getOrientation();
				quat.set(orientationTransform);
				node.setOrientation(quat);
				lifetime--;
			}
			else
			{
				health = 0;
			}
		}
		super.update();
	}
}
