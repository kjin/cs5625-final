package cs5625.fancyplane;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cs5625.gfx.json.NamedObject;
import cs5625.gfx.light.ShadowingSpotLight;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class GameController {
	private SceneTreeNode fancyScene;
	
	private FancyPlayer fancyPlayer;
	private FancyLandscape fancyLandscape;
	
	public GameController(SceneTreeNode rootNode)
	{
		fancyScene = rootNode;
		fancyPlayer = new FancyPlayer(fancyScene);
		fancyLandscape = new FancyLandscape(fancyScene);
    	
    	ShadowingSpotLight spotLight = new ShadowingSpotLight();
    	spotLight.setPosition(new Point3f(5, 25, 0));
    	spotLight.setTarget(new Point3f(0,0,0));
    	spotLight.setColor(new Color3f(1.0f,1.0f,1.0f));
    	SceneTreeNode spotLightNode = new SceneTreeNode();
    	spotLightNode.setData(new Value<NamedObject>(spotLight));
    	fancyScene.addChild(spotLightNode);
	}
	
	public void keyPressed(KeyEvent e)
	{
		//example:
		if(e.getKeyCode() == KeyEvent.VK_W)
		{
			System.out.println("W pressed!");
		}
		
		//let player know?
		
	}
	
	public void keyReleased(KeyEvent e)
	{
		//example:
		if(e.getKeyCode() == KeyEvent.VK_W)
		{
			System.out.println("W released!");
		}
		
		//let player know?
	}
	
}
