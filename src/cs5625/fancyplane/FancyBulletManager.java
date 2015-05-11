package cs5625.fancyplane;

import javax.vecmath.Vector3f;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyBulletManager
{
	int NUM_BULLETS = 20;
	int BULLET_LIFETIME = 50;
	
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
		if (bullets[currentIndex].getHealth() <= 0)
		{
			Vector3f velocity = bullets[currentIndex].getVelocity();
			if (plane.getTeam() == FancyTeam.Player)
			{
				velocity.x = 1.0f;
			}
			else if (plane.getTeam() == FancyTeam.Enemy)
			{
				velocity.x = -1.0f;
			}
			else
			{
				return;
			}
			bullets[currentIndex].setTeam(plane.getTeam());
			bullets[currentIndex].setPosition(plane.getPosition());
			bullets[currentIndex].setVelocity(velocity);
			bullets[currentIndex].setLifetime(BULLET_LIFETIME);
			bullets[currentIndex].setHealth(1);
			currentIndex++;
			if (currentIndex == bullets.length)
			{
				currentIndex = 0;
			}
		}
	}
	
	public FancyBullet[] getBullets()
	{
		return bullets;
	}
}
