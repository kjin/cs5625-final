package cs5625.fancyplane;

import cs5625.gfx.scenetree.SceneTreeNode;

/**
 * Contains object properties specific to fancy ships.
 * @author jink2
 *
 */
public class FancyShip extends FancyObject
{
	protected FancyBulletManager bulletManager;
	protected int maxHealth;

	public FancyShip(SceneTreeNode parentNode, String modelName, FancyTeam team, FancyBulletManager bulletManager)
	{
		super(parentNode, modelName, team);
		maxHealth = health;
		this.bulletManager = bulletManager;
	}
	
	public int getMaxHealth() { return maxHealth; }
}
