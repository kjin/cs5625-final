package cs5625.fancyplane;

import javax.vecmath.Vector3f;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyEnemyManager
{
	int NUM_ENEMIES = 10;
	int SPAWN_TIME = 50;
	
	SceneTreeNode enemyParentNode;
	FancyEnemy[] enemies;
	int currentIndex;
	int time;
	
	public FancyEnemyManager(SceneTreeNode parentNode, FancyBulletManager bulletManager, FancyParticleEngine particles)
	{
		enemyParentNode = new SceneTreeNode();
		parentNode.addChild(enemyParentNode);
		
		enemies = new FancyEnemy[NUM_ENEMIES];
		for (int i = 0; i < enemies.length; i++)
		{
			enemies[i] = new FancyEnemy(enemyParentNode, bulletManager, particles);
			enemies[i].setHealth(0);
		}
		currentIndex = 0;
		time = 0;
	}
	
	public void update()
	{
		for (int i = 0; i < enemies.length; i++)
		{
			if (enemies[i].health > 0)
			{
				enemies[i].update();
			}
		}
		time++;
		if (time >= SPAWN_TIME && enemies[currentIndex].health <= 0)
		{
			enemies[currentIndex].initialize();
			currentIndex++;
			if (currentIndex == enemies.length)
			{
				currentIndex = 0;
			}
			time = 0;
		}
	}
	
	public FancyEnemy[] getEnemies()
	{
		return enemies;
	}
}
