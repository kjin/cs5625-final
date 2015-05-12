package cs5625.fancyplane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL2;
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
	int NUM_PARTICLES = 400;
	
	SceneTreeNode node;
	
	LinkedList<FancyParticle> activeParticles;
	ArrayList<FancyParticle> inactiveParticles;
	
	SmokeParticleMesh fancyMesh;
	
	public FancyParticleSystem(SceneTreeNode parentNode)
	{
		node = new SceneTreeNode();
		parentNode.addChild(node);
		
		activeParticles = new LinkedList<FancyParticle>();
		inactiveParticles = new ArrayList<FancyParticle>();
		for (int i = 0; i < NUM_PARTICLES; i++)
		{
			inactiveParticles.add(new FancyParticle(i));
		}
		
		// Init vertex data
		SmokeParticleVertexData vertexData = new SmokeParticleVertexData();
		vertexData.startBuild().setNumParticles(NUM_PARTICLES).endBuild();
		
		// Init index data
		IndexData indexData = new IndexData();
		IndexData.Builder builder = indexData.startBuild();
		for (int i = 0; i < NUM_PARTICLES; i++)
		{
			builder.add(5 * i + 0);
			builder.add(5 * i + 1);
			builder.add(5 * i + 4);
			builder.add(5 * i + 1);
			builder.add(5 * i + 2);
			builder.add(5 * i + 4);
			builder.add(5 * i + 2);
			builder.add(5 * i + 3);
			builder.add(5 * i + 4);
			builder.add(5 * i + 3);
			builder.add(5 * i + 0);
			builder.add(5 * i + 4);
		}
		builder.endBuild();
		
		fancyMesh = new SmokeParticleMesh(NUM_PARTICLES);
		fancyMesh.setVertexData(new Value<VertexData>(vertexData));
		fancyMesh.setIndexData(new Value<IndexData>(indexData));
		
		MeshPart fancyMeshPart = new MeshPart(GL2.GL_TRIANGLES, 0, 5 * NUM_PARTICLES, new Value<Material>(new SmokeParticleMaterial()));
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
			fancyMesh.setParticlePosition(particle.id, particle.position, particle.lifespan / 120.0f);
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
			particle.lifespan = 120;
			activeParticles.add(particle);
		}
	}
}
