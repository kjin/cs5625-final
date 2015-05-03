package cs5625.fancyplane;

import javax.vecmath.Vector3f;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyBulletManager
{
	int NUM_BULLETS = 40;
	int BULLET_LIFETIME = 100;
	
	SceneTreeNode bulletParentNode;
	FancyBullet[] bullets;
	int currentIndex;
	
	public FancyBulletManager(SceneTreeNode parentNode)
	{
		bulletParentNode = new SceneTreeNode();
		parentNode.addChild(bulletParentNode);
		
		bullets = new FancyBullet[NUM_BULLETS];
		for (int i = 0; i < bullets.length; i++)
		{
			bullets[i] = new FancyBullet(bulletParentNode);
		}
		currentIndex = 0;
	}
	
	public void update()
	{
		for (int i = 0; i < bullets.length; i++)
		{
			bullets[i].update();
		}
	}
	
	public void fireBullet(FancyObject plane)
	{
		if (!bullets[currentIndex].visible)
		{
			if (plane.getTeam() == FancyTeam.Player)
			{
				Vector3f velocity = bullets[currentIndex].getVelocity();
				velocity.x = 1.0f;
				bullets[currentIndex].setPosition(plane.getPosition());
				bullets[currentIndex].setVelocity(velocity);
				bullets[currentIndex].setLifetime(BULLET_LIFETIME);
				currentIndex++;
				if (currentIndex == bullets.length)
				{
					currentIndex = 0;
				}
			}
		}
	}
}
