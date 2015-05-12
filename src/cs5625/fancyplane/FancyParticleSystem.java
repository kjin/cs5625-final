package cs5625.fancyplane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL2;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cs5625.gfx.gldata.IndexData;
import cs5625.gfx.gldata.SmokeParticleVertexData;
import cs5625.gfx.gldata.SmokeParticleVertexData.Builder;
import cs5625.gfx.gldata.VertexData;
import cs5625.gfx.json.NamedObject;
import cs5625.gfx.material.Material;
import cs5625.gfx.material.SmokeParticleMaterial;
import cs5625.gfx.mesh.MeshPart;
import cs5625.gfx.mesh.SmokeParticleMesh;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.UnstructuredMesh;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;

public class FancyParticleSystem
{
	int numParticles = 100;
	
	SceneTreeNode node;
	
	LinkedList<FancyParticle> activeParticles;
	ArrayList<FancyParticle> inactiveParticles;
	
	public float particleLifespan = 120;
	public float particleSize = 1;
	
	SmokeParticleMesh fancyMesh;
	
	public FancyParticleSystem(SceneTreeNode parentNode, int numParticles, Color4f color)
	{
		this.numParticles = numParticles;
		node = new SceneTreeNode();
		parentNode.addChild(node);
		
		activeParticles = new LinkedList<FancyParticle>();
		inactiveParticles = new ArrayList<FancyParticle>();
		for (int i = 0; i < numParticles; i++)
		{
			inactiveParticles.add(new FancyParticle(i));
		}

		int numSides = 8;
		// Init vertex data
		SmokeParticleVertexData vertexData = new SmokeParticleVertexData();
		vertexData.startBuild().setNumParticles(numParticles).setNumSides(numSides).endBuild();
		
		// Init index data
		IndexData indexData = new IndexData();
		IndexData.Builder builder = indexData.startBuild();
		for (int i = 0; i < numParticles; i++)
		{
			for (int j = 0; j < numSides; j++)
			{
				builder.add((numSides + 1) * i + j);
				builder.add((numSides + 1) * i + (j + 1) % numSides);
				builder.add((numSides + 1) * i + numSides);
			}
		}
		builder.endBuild();
		
		fancyMesh = new SmokeParticleMesh(numParticles);
		fancyMesh.setVertexData(new Value<VertexData>(vertexData));
		fancyMesh.setIndexData(new Value<IndexData>(indexData));
		
		SmokeParticleMaterial smokeParticleMaterial = new SmokeParticleMaterial();
		smokeParticleMaterial.setDiffuseColor(color);
		MeshPart fancyMeshPart = new MeshPart(GL2.GL_TRIANGLES, 0, 3 * numSides * numParticles, new Value<Material>(smokeParticleMaterial));
		fancyMesh.addPart(fancyMeshPart);
		
		node = new SceneTreeNode();
		node.setData(new Value<NamedObject>(fancyMesh));
		parentNode.addChild(node);
	}
	
	public void update()
	{
		Iterator<FancyParticle> itr = activeParticles.iterator();
		while (itr.hasNext())
		{
			FancyParticle particle = itr.next();
			particle.update();
			fancyMesh.setParticlePosition(particle.id, particle.position, particleSize * particle.lifespan / particleLifespan);
			if (particle.lifespan <= 0)
			{
				itr.remove();
				fancyMesh.removeParticle(particle.id);
				inactiveParticles.add(particle);
			}
		}
	}
	
	private void getPerpendicularVector(Vector3f vIn, Vector3f vOut)
	{
		if (Float.isNaN(vIn.lengthSquared()))
		{
			return;
		}
		do
		{
			vOut.set((float)Math.random() - 0.5f, (float)Math.random() - 0.5f, (float)Math.random() - 0.5f);
			vOut.cross(vIn, vOut);
		} while (vOut.lengthSquared() == 0 || Float.isNaN(vOut.lengthSquared()));
		vOut.normalize();
	}
	
	public void releaseParticles(int numParticles, Point3f position, Vector3f directionMagnitude, float randomness, float range)
	{
		Vector3f fromPosition = new Vector3f();
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
			FancyParticle particle = inactiveParticles.get(inactiveParticles.size() - 1);
			inactiveParticles.remove(inactiveParticles.size() - 1);
			
			dir.set(directionMagnitude);
			dir.normalize();
			getPerpendicularVector(dir, dirT);
			dirB.cross(dir, dirT);
			dirT.scale(2 * randomness * (float)(Math.random() - 0.5f));
			dirB.scale(2 * randomness * (float)(Math.random() - 0.5f));
			dir.add(dirT);
			dir.add(dirB);
			dir.normalize();
			mag = directionMagnitude.length();
			mag *= 1 + 2 * randomness * (Math.random() - 0.5f);
			
			fromPosition.set(dir);
			fromPosition.scale(range);
			fromPosition.add(position);
			particle.position.set(fromPosition);
			
			dir.scale(mag);
			particle.velocity.set(dir);
			particle.lifespan = particleLifespan;
			activeParticles.add(particle);
		}
	}
	
	public void releaseParticles(int numParticles, Point3f position, Vector3f directionMagnitude, float randomness)
	{
		releaseParticles(numParticles, position, directionMagnitude, randomness, 0);
	}
}
