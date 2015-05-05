package cs5625.fancyplane;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import cs5625.gfx.gldata.FileTexture2DData;
import cs5625.gfx.gldata.Texture2DData;
import cs5625.gfx.material.Material;
import cs5625.gfx.material.XToonMaterial;
import cs5625.gfx.objcache.Holder;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Reference;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyEnemy extends FancyShip
{	
	int fireTime;
	int time;
	
	public FancyEnemy(SceneTreeNode parentNode, FancyBulletManager bulletManager)
	{
		super(parentNode, "fancy-enemy", FancyTeam.Enemy, bulletManager);
		fireTime = 0;
		collisionRadius = 1;
		time = 0;
	}
	
	public void initialize()
	{
		health = 5;
		position.x = 30;
		position.y = position.z = 0;
	}
	
	protected boolean hasSpecialMaterial()
	{
		return true;
	}
	
	protected Holder<Material> getSpecialMaterial()
	{
		XToonMaterial material = new XToonMaterial();
		material.setOrientationBased(true);
		
        String textureName = "data/textures/xtoon-enemy.png";
        textureName = FilenameUtils.separatorsToUnix(new File(textureName).getAbsolutePath());
        String key = ObjectCacheKey.makeKey(FileTexture2DData.class, textureName);
        material.setXToonTexture(new Reference<Texture2DData>(key));
        
        return new Value<Material>(material);
	}
	
	public void update()
	{
		if (health > 0)
		{
			velocity.set(-0.2f, (float)Math.sin(time / 20.0f) / 15.0f, 0);
			if (position.x < -30) // Left kill wall
			{
				health = 0;
			}
			time++;
		}
		super.update();
	}
}
