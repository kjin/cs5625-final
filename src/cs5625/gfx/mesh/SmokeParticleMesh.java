package cs5625.gfx.mesh;

import javax.vecmath.Point3f;

public class SmokeParticleMesh extends UnstructuredMesh {
	// TODO add getter
	public Point3f[] particlePositions;
	
	public SmokeParticleMesh(int numParticles)
	{
		particlePositions = new Point3f[numParticles];
		for (int i = 0; i < particlePositions.length; i++)
		{
			particlePositions[i] = new Point3f(0, -100, 0);
		}
	}
	
	public void setParticlePosition(int index, Point3f position)
	{
		particlePositions[index].set(position);
	}
	
	public void removeParticle(int index)
	{
		// just place it down low so no one can see it
		particlePositions[index].set(0, 1000, 0);
	}
}
