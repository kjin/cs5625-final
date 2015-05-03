package cs5625.fancyplane;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import cs5625.gfx.json.NamedObject;
import cs5625.gfx.light.ShadowingSpotLight;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class GameController {
	private SceneTreeNode fancyScene;
	
	public GameController(SceneTreeNode rootNode)
	{
		fancyScene = rootNode;

    	ArrayList<TriMesh> meshes = new ArrayList<TriMesh>();
		try {
			meshes.addAll(WavefrontObjToTriMeshConverter.load("data/models/fancy-plane.obj", true, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SceneTreeNode fancyPlaneNode = new SceneTreeNode();
		fancyPlaneNode.setData(new Value<NamedObject>(meshes.get(0)));
    	fancyScene.addChild(fancyPlaneNode);
    	
    	ShadowingSpotLight spotLight = new ShadowingSpotLight();
    	spotLight.setPosition(new Point3f(5, 50, 0));
    	spotLight.setTarget(new Point3f(0,0,0));
    	spotLight.setColor(new Color3f(1.0f,0.0f,0.0f));
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
