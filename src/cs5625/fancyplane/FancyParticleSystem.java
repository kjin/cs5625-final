package cs5625.fancyplane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyParticleSystem
{
	int NUM_PARTICLES = 200;
	
	SceneTreeNode node;
	
	LinkedList<FancyParticle> activeParticles;
	ArrayList<FancyParticle> inactiveParticles;
	
	public FancyParticleSystem(SceneTreeNode parentNode)
	{
		node = new SceneTreeNode();
		parentNode.addChild(node);
		
		activeParticles = new LinkedList<FancyParticle>();
		inactiveParticles = new ArrayList<FancyParticle>();
		for (int i = 0; i < NUM_PARTICLES; i++)
		{
			inactiveParticles.add(new FancyParticle(node));
		}
	}
	
	public void update()
	{
		Iterator<FancyParticle> itr = activeParticles.iterator();
		while (itr.hasNext())
		{
			FancyParticle particle = itr.next();
			particle.update();
			if (particle.lifespan <= 0)
			{
				itr.remove();
				inactiveParticles.add(particle);
			}
		}
	}
	
	private void getPerpendicularVector(Vector3f vIn, Vector3f vOut)
	{
		if (vIn.y != 0 && vIn.z != 0)
		{
			vOut.set(1, 0, 0);
		}
		else
		{
			vOut.set(0, 1, 0);
		}
		vOut.cross(vIn, vOut);
		vOut.normalize();
	}
	
	public void releaseParticles(int numParticles, Point3f position, Vector3f directionMagnitude, float randomness)
	{
		Vector3f dir = new Vector3f();
		Vector3f dirT = new Vector3f();
		Vector3f dirB = new Vector3f();
		float mag;
		for (int i = 0; i < numParticles; i++)
		{
			if (inactiveParticles.size() == 0)
			{
				break;
			}
			dir.set(directionMagnitude);
			dir.normalize();
			getPerpendicularVector(dir, dirT);
			dirB.cross(dir, dirT);
			dirT.scale(2 * randomness * (float)(Math.random() - 0.5f));
			dirB.scale(2 * randomness * (float)(Math.random() - 0.5f));
			dir.add(dirT);
			dir.add(dirB);
			mag = directionMagnitude.length();
			mag *= 1 + 2 * randomness * (Math.random() - 0.5f);
			dir.scale(mag);
			FancyParticle particle = inactiveParticles.get(inactiveParticles.size() - 1);
			inactiveParticles.remove(inactiveParticles.size() - 1);
			particle.position.set(position);
			particle.velocity.set(dir);
			particle.lifespan = 60;
			activeParticles.add(particle);
		}
	}
}
