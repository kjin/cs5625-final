package cs5625.fancyplane;

import java.awt.event.KeyEvent;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cs5625.gfx.camera.Camera;
import cs5625.gfx.camera.PerspectiveCamera;
import cs5625.gfx.json.NamedObject;
import cs5625.gfx.light.ShadowingSpotLight;
import cs5625.gfx.material.Material;
import cs5625.gfx.material.SingleColorMaterial;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyGameController {
	private SceneTreeNode fancyScene;

	private FancyLandscape fancyLandscape;
	private FancyBulletManager fancyBulletManager;
	private FancyPlayer fancyPlayer;
	private FancyEnemyManager fancyEnemyManager;
	private FancyParticleSystem fancyParticleSystem;
	
    private PerspectiveCamera camera;
	
	public FancyGameController(SceneTreeNode rootNode)
	{
		// mode stuff
		fancyScene = rootNode;
		fancyLandscape = new FancyLandscape(fancyScene);
		fancyBulletManager = new FancyBulletManager(fancyScene);
		fancyPlayer = new FancyPlayer(fancyScene, fancyBulletManager);
		fancyEnemyManager = new FancyEnemyManager(fancyScene, fancyBulletManager);
		fancyParticleSystem = new FancyParticleSystem(fancyScene);
		
		// light stuff
    	ShadowingSpotLight spotLight = new ShadowingSpotLight();
    	spotLight.setPosition(new Point3f(30,45,-10));
    	spotLight.setTarget(new Point3f(0,0,0));
    	spotLight.setColor(new Color3f(1.0f, 1.0f, 1.0f));
    	SceneTreeNode spotLightNode = new SceneTreeNode();
    	spotLightNode.setData(new Value<NamedObject>(spotLight));
    	fancyScene.addChild(spotLightNode);
    	
    	// sun stuff
    	/* make the sun out of a doodad. */
    	Material m = new SingleColorMaterial(new Color4f(1.0f,0.5f,0.2f,1.0f));
    	FancyDoodad fancySun = new FancyDoodad(fancyScene, "fancy-unitsphere", new Value<Material>(m));
    	fancySun.setPosition(new Point3f(100,150,-400));
    	fancySun.setScale(50);
    	fancySun.update();
    	
    	// camera stuff
    	camera = new PerspectiveCamera(new Point3f(0, 0, 20),
                new Point3f(0, 0, 0),
                new Vector3f(0, 1, 0), 0.1f, 500, 60.0f);
	}
	
	public void update()
	{
		fancyPlayer.update();
		fancyBulletManager.update();
		fancyEnemyManager.update();
		fancyLandscape.update();
		fancyParticleSystem.update();
		
		// Collision handling
		FancyBullet[] bullets = fancyBulletManager.getBullets();
		FancyEnemy[] enemies = fancyEnemyManager.getEnemies();
		Vector3f temp = new Vector3f();
		// Player - Enemy
		if (fancyPlayer.health > 0)
		{
			for (int i = 0; i < enemies.length; i++)
			{
				if (enemies[i].health > 0 && fancyPlayer.collidesWith(enemies[i]))
				{
					doCollisionAftermath(fancyPlayer, enemies[i], 20, 20);
				}
			}
		}
		// Bullet - Ship
		for (int i = 0; i < bullets.length; i++)
		{
			if (bullets[i].health > 0)
			{
				if (fancyPlayer.health > 0 && fancyPlayer.collidesWith(bullets[i]))
				{
					doCollisionAftermath(fancyPlayer, bullets[i], 20, 1);
				}
				if (bullets[i].health > 0)
				{
					for (int j = 0; j < enemies.length; j++)
					{
						if (enemies[j].health > 0 && enemies[j].collidesWith(bullets[i]))
						{
							doCollisionAftermath(enemies[j], bullets[i], 20, 1);
						}
						if (bullets[i].health <= 0)
						{
							break;
						}
					}
				}
			}
		}
	}
	
	Vector3f temp = new Vector3f(); // for below
	
	private void doCollisionAftermath(FancyObject o1, FancyObject o2, int o1Particles, int o2Particles)
	{
		o1.setHealth(o1.getHealth() - 1);
		o2.setHealth(o2.getHealth() - 1);
		if (o1Particles > 0 && o1.getHealth() == 0)
		{
			temp.set(o2.getVelocity());
			temp.setY(temp.y - 1);
			temp.normalize();
			temp.scale(0.05f);
			fancyParticleSystem.releaseParticles(o1Particles, o1.getPosition(), temp, 0.5f);
		}
		if (o2Particles > 0 && o2.getHealth() == 0)
		{
			temp.set(o1.getVelocity());
			temp.setY(temp.y - 1);
			temp.normalize();
			temp.scale(0.05f);
			fancyParticleSystem.releaseParticles(o2Particles, o2.getPosition(), temp, 0.5f);
		}
	}
	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP)
		{
			fancyPlayer.upPressed();
		}
		else if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			fancyPlayer.leftPressed();
		}
		else if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			fancyPlayer.downPressed();
		}
		else if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			fancyPlayer.rightPressed();
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			fancyPlayer.spacePressed();
		}

	}
	
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP)
		{
			fancyPlayer.upReleased();
		}
		else if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			fancyPlayer.leftReleased();
		}
		else if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			fancyPlayer.downReleased();
		}
		else if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			fancyPlayer.rightReleased();
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			fancyPlayer.spaceReleased();
		}
		
		//let player know?
	}
	
	public Camera getCamera()
	{
		return camera;
	}
}
