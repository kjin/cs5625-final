package cs5625.fancyplane;

import java.awt.event.KeyEvent;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cs5625.gfx.camera.Camera;
import cs5625.gfx.camera.PerspectiveCamera;
import cs5625.gfx.json.NamedObject;
import cs5625.gfx.light.ShadowingSpotLight;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyGameController {
	private SceneTreeNode fancyScene;
	
	private FancyPlayer fancyPlayer;
	private FancyBulletManager fancyBulletManager;
	private FancyLandscape fancyLandscape;
	private FancyEnemyManager fancyEnemyManager;
	
    private PerspectiveCamera camera;
	
	public FancyGameController(SceneTreeNode rootNode)
	{
		// mode stuff
		fancyScene = rootNode;
		fancyBulletManager = new FancyBulletManager(fancyScene);
		fancyPlayer = new FancyPlayer(fancyScene, fancyBulletManager);
		fancyEnemyManager = new FancyEnemyManager(fancyScene, fancyBulletManager);
		fancyLandscape = new FancyLandscape(fancyScene);
    	
		// light stuff
    	ShadowingSpotLight spotLight = new ShadowingSpotLight();
    	spotLight.setPosition(new Point3f(5, 25, 0));
    	spotLight.setTarget(new Point3f(0,0,0));
    	spotLight.setColor(new Color3f(1.0f, 1.0f, 1.0f));
    	SceneTreeNode spotLightNode = new SceneTreeNode();
    	spotLightNode.setData(new Value<NamedObject>(spotLight));
    	//fancyScene.addChild(spotLightNode);
    	
    	// sun stuff
    	/* make the sun out of a doodad. */
    	
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
		
		// Collision handling
		FancyBullet[] bullets = fancyBulletManager.getBullets();
		FancyEnemy[] enemies = fancyEnemyManager.getEnemies();
		// Player - Enemy
		if (fancyPlayer.health > 0)
		{
			for (int i = 0; i < enemies.length; i++)
			{
				if (enemies[i].health > 0 && fancyPlayer.collidesWith(enemies[i]))
				{
					fancyPlayer.setHealth(fancyPlayer.getHealth() - 1);
					enemies[i].setHealth(enemies[i].getHealth() - 1);
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
					fancyPlayer.setHealth(fancyPlayer.getHealth() - 1);
					bullets[i].setHealth(bullets[i].getHealth() - 1);
				}
				if (bullets[i].health > 0)
				{
					for (int j = 0; j < enemies.length; j++)
					{
						if (enemies[j].health > 0 && enemies[j].collidesWith(bullets[i]))
						{
							enemies[j].setHealth(enemies[j].getHealth() - 1);
							bullets[i].setHealth(bullets[i].getHealth() - 1);
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
