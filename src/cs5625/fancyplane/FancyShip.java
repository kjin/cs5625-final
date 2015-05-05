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

	public FancyShip(SceneTreeNode parentNode, String modelName, FancyTeam team, FancyBulletManager bulletManager)
	{
		super(parentNode, modelName, team);
		this.bulletManager = bulletManager;
	}
}
