package cs5625.fancyplane;

import java.io.File;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import org.apache.commons.io.FilenameUtils;

import cs5625.gfx.gldata.FileTexture2DData;
import cs5625.gfx.gldata.Texture2DData;
import cs5625.gfx.material.Material;
import cs5625.gfx.material.SingleColorMaterial;
import cs5625.gfx.material.XToonMaterial;
import cs5625.gfx.objcache.Holder;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Reference;
import cs5625.gfx.objcache.Value;
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
	
	protected boolean hasSpecialMaterial()
	{
		return true;
	}
	
	protected Holder<Material> getSpecialMaterial()
	{
		SingleColorMaterial mat = new SingleColorMaterial();
		mat.setColor(new Color4f(0.4f, 0.5f, 0.0f, 1.0f));
		return new Value<Material>(mat);
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
