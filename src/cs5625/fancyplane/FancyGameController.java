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
	
	private FancyPlayer fancyPlayer;
	private FancyBulletManager fancyBulletManager;
	private FancyLandscape fancyLandscape;
	
    private PerspectiveCamera camera;
	
	public FancyGameController(SceneTreeNode rootNode)
	{
		// mode stuff
		fancyScene = rootNode;
		fancyBulletManager = new FancyBulletManager(fancyScene);
		fancyPlayer = new FancyPlayer(fancyScene, fancyBulletManager);
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
		//for (int i = 0; i < )
		fancyBulletManager.update();
		fancyLandscape.update();
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
