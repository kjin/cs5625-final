package cs5625.fancyplane;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyBulletManager
{
	int NUM_BULLETS = 40;
	
	SceneTreeNode bulletParentNode;
	FancyBullet[] bullets;
	
	public FancyBulletManager(SceneTreeNode parentNode)
	{
		bulletParentNode = new SceneTreeNode();
		parentNode.addChild(bulletParentNode);
		
		bullets = new FancyBullet[NUM_BULLETS];
		for (int i = 0; i < bullets.length; i++)
		{
			bullets[i] = new FancyBullet(bulletParentNode);
		}
	}
	
	public void update()
	{
		for (int i = 0; i < bullets.length; i++)
		{
			bullets[i].update();
		}
	}
}
