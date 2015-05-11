package cs5625.gfx.mesh;

import javax.vecmath.Point3f;

public class SmokeParticleMesh extends UnstructuredMesh {
	// TODO add getter
	public float[] particlePositions;
	
	public SmokeParticleMesh(int numParticles)
	{
		particlePositions = new float[3 * numParticles];
	}
	
	public void setParticlePosition(int index, Point3f position)
	{
		particlePositions[3 * index + 0] = position.x;
		particlePositions[3 * index + 1] = position.y;
		particlePositions[3 * index + 2] = position.z;
	}
}
