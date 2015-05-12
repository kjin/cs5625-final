/*
 * Copied from PosColData.java; edits by Kelvin Jin (kkj9)
 */

package cs5625.gfx.gldata;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cs5625.jogl.AttributeSpec;
import cs5625.util.BufferUtil;

import javax.media.opengl.GL2;

import java.util.HashMap;

/**
 * Vertex data where each vertex has a position and a color.
 */
public class SmokeParticleVertexData extends VertexData {
    public static final AttributeSpec PARTICLE_INDEX = new AttributeSpec("particle_index", 1,
            GL2.GL_FLOAT, false, 4 * 2, 0);
    public static final AttributeSpec PARTICLE_CORNER = new AttributeSpec("particle_color", 1,
            GL2.GL_FLOAT, false, 4 * 2, 4);
    private static final HashMap<String, AttributeSpec> vertexAttributes = new HashMap<String, AttributeSpec>();

    static {
        vertexAttributes.put("particle_index", PARTICLE_INDEX);
        vertexAttributes.put("vert_particle_index", PARTICLE_INDEX);
        vertexAttributes.put("particle_corner", PARTICLE_CORNER);
        vertexAttributes.put("vert_particle_corner", PARTICLE_CORNER);
    }

    public static class Builder {
        public SmokeParticleVertexData data;
        private int numParticles = 0;

        Builder(SmokeParticleVertexData data) {
            this.data = data;
        }
        
        public Builder setNumParticles(int value)
        {
        	numParticles = value;
        	return this;
        }

        public SmokeParticleVertexData endBuild() {
            data.allocate(4 * numParticles);
            for (int i = 0; i < numParticles; i++) {
                data.setParticleNumber(4 * i + 0, i + 1);
                data.setParticleNumber(4 * i + 1, i + 1);
                data.setParticleNumber(4 * i + 2, i + 1);
                data.setParticleNumber(4 * i + 3, i + 1);
                data.setParticleCorner(4 * i + 0, 0);
                data.setParticleCorner(4 * i + 1, 1);
                data.setParticleCorner(4 * i + 2, 2);
                data.setParticleCorner(4 * i + 3, 3);
            }
            data.bumpVersion();
            return data;
        }
    }

    public Builder startBuild() {
        return new Builder(this);
    }

    @Override
    public int getVertexCount() {
        return 0;
    }

    @Override
    public AttributeSpec getAttributeSpec(String name) {
        return vertexAttributes.get(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        return vertexAttributes.containsKey(name.toLowerCase());
    }

    @Override
    public int getNumBytesPerVertex() {
        return 4 * 2;
    }

    public int getNumDWordsPerVertex() {
        return getNumBytesPerVertex() / 4;
    }

    public float getParticleNumber(int index) {
        return BufferUtil.getLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 0);
    }

    public float getParticleCorner(int index) {
    	return BufferUtil.getLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 1);
    }

    private void setParticleNumber(int index, float value) {
    	BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 0, value);
    }

    private void setParticleCorner(int index, float value) {
        BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 1, value);
    }

	@Override
	public void fromJson(JsonObject json, String directory) {
		// Sorry, doesn't do anything (our game doesn't use Json files)
	}

	@Override
	protected void fillJson(JsonObject json, String directory) {
		// Sorry, doesn't do anything (our game doesn't use Json files)
	}
}
