package cs5625.gfx.mesh;

import javax.vecmath.Point3f;

public class SmokeParticleMesh extends UnstructuredMesh {
	// TODO add getter
	public Point3f[] particlePositions;
	
	public SmokeParticleMesh(int numParticles)
	{
		particlePositions = new Point3f[numParticles];
	}
	
	public void setParticlePosition(int index, Point3f position)
	{
		particlePositions[index].set(position);
	}
}
