package cs5625.fancyplane;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

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
	protected FancyParticleEngine particles;
	protected Vector3f forward;
	
	int time;
	
	private Point3f tempP = new Point3f();
	private Vector3f tempV = new Vector3f();

	public FancyShip(SceneTreeNode parentNode, String modelName, FancyTeam team, FancyBulletManager bulletManager, FancyParticleEngine particles)
	{
		super(parentNode, modelName, team);
		maxHealth = health;
		this.bulletManager = bulletManager;
		this.particles = particles;
		time = 0;
		forward = new Vector3f();
		if (team == FancyTeam.Player)
		{
			forward.x = 1;
		}
		else if (team == FancyTeam.Enemy)
		{
			forward.x = -1;
		}
	}
	
	public void update()
	{
		time++;
		int interval = 0;
		if (health > 0)
		{
			float healthPercentage = (float)health / maxHealth;
			if (healthPercentage < 0.7f) { interval = 4; }
			else if (healthPercentage < 0.4f) { interval = 2; }
			if (interval > 0 && time % interval == 0)
			{
				tempV.set(0, 0.05f, 0);
				particles.blacksmoke.releaseParticles(1, getPosition(), tempV, 0.5f);
			}
			tempP.set(getPosition());
			tempP.x -= 0.75f * forward.x;
			tempV.set(-0.1f * forward.x, 0, 0);
			particles.smoke.releaseParticles(1, tempP, tempV, 0.3f);
			tempV.set(-0.05f * forward.x, 0, 0);
			particles.fire.releaseParticles(1, tempP, tempV, 0.3f);
		}
		super.update();
	}
	
	public int getMaxHealth() { return maxHealth; }
}
