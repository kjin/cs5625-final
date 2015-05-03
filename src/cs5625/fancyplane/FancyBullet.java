package cs5625.fancyplane;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyBullet extends FancyObject
{
	AxisAngle4f orientationTransform;
	
	public FancyBullet(SceneTreeNode parentNode)
	{
		super(parentNode, "fancy-bullet");
		orientationTransform = new AxisAngle4f();
		orientationTransform.x = 0;
		orientationTransform.y = 1;
		orientationTransform.z = 0;
		visible = false;
	}
	
	public void update()
	{
		orientationTransform.angle += 0.1f;
		Quat4f quat = node.getOrientation();
		quat.set(orientationTransform);
		node.setOrientation(quat);
	}
}
