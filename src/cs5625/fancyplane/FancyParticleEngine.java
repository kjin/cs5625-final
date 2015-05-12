package cs5625.fancyplane;

import javax.vecmath.Color4f;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyParticleEngine {
	public FancyParticleSystem fire;
	public FancyParticleSystem smoke;
	public FancyParticleSystem blackfire;
	public FancyParticleSystem blacksmoke;
	
	public FancyParticleEngine(SceneTreeNode parentNode)
	{
		smoke = new FancyParticleSystem(parentNode, 200, new Color4f(1.0f, 1.0f, 1.0f, 1.0f));
		smoke.particleLifespan = 60;
		smoke.particleSize = 0.4f;
		fire = new FancyParticleSystem(parentNode, 200, new Color4f(1.0f, 0.5f, 0.25f, 1.0f));
		fire.particleLifespan = 30;
		fire.particleSize = 0.3f;
		blacksmoke = new FancyParticleSystem(parentNode, 200, new Color4f(0.5f, 0.5f, 0.5f, 1.0f));
		blacksmoke.particleLifespan = 120;
		blackfire = new FancyParticleSystem(parentNode, 200, new Color4f(0.5f, 0.0f, 0.0f, 1.0f));
		blackfire.particleLifespan = 120;
	}
	
	public void update()
	{
		fire.update();
		smoke.update();
		blackfire.update();
		blacksmoke.update();
	}
}
