package cs5625.gfx.mesh;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

public class SmokeParticleMesh extends UnstructuredMesh {
	// TODO add getter
	public Point4f[] particlePositionScale;
	
	public SmokeParticleMesh(int numParticles)
	{
		particlePositionScale = new Point4f[numParticles];
		for (int i = 0; i < particlePositionScale.length; i++)
		{
			particlePositionScale[i] = new Point4f(0, -1000, 0, 0);
		}
	}
	
	public void setParticlePosition(int index, Point3f position, float scale)
	{
		particlePositionScale[index].set(position.x, position.y, position.z, scale);
	}
	
	public void removeParticle(int index)
	{
		// just place it down low so no one can see it
		particlePositionScale[index].set(0, -1000, 0, 0);
	}
}
