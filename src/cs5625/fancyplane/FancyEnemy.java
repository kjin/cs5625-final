package cs5625.fancyplane;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyEnemy extends FancyObject
{
	FancyBulletManager bulletManager;
	
	int fireTime;
	
	public FancyEnemy(SceneTreeNode parentNode, FancyBulletManager bulletManager)
	{
		super(parentNode, "fancy-enemy", FancyTeam.Player);
		
		this.bulletManager = bulletManager;
		fireTime = 0;
	}
}
