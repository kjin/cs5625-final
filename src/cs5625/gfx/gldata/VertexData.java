/*
 *
 *  * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  * Copyright (c) 2015, Department of Computer Science, Cornell University.
 *  *
 *  * This code repository has been authored collectively by:
 *  * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  * Pramook Khungurn (pk395), Steve Marschner (srm2), and Sean Ryan (ser99)
 *
 */

package cs5625.gfx.gldata;

import cs5625.gfx.glcache.GLResourceCache;
import cs5625.gfx.glcache.GLResourceProvider;
import cs5625.gfx.glcache.GLResourceRecord;
import cs5625.gfx.json.AbstractJsonSerializable;
import cs5625.gfx.json.JsonSerializable;
import cs5625.jogl.AttributeSpec;
import cs5625.jogl.Vbo;
import cs5625.jogl.VboTarget;

import javax.media.opengl.GL2;
import java.nio.ByteBuffer;

public abstract class VertexData extends AbstractJsonSerializable implements GLResourceProvider<Vbo> {
    protected ByteBuffer buffer;
    private int version = 0;
    private int vertexCount;

    public abstract AttributeSpec getAttributeSpec(String name);
    public abstract boolean hasAttribute(String name);

    @Override
    public void updateGLResource(GL2 gl, GLResourceRecord record) {
        Vbo vbo = null;
        boolean needUpdate = false;
        if (record.resource == null) {
            vbo = new Vbo(gl, VboTarget.ARRAY_BUFFER);
            record.resource = vbo;
            needUpdate = true;
        } else if (record.version != this.version) {
            vbo = (Vbo) record.resource;
            needUpdate = true;
        }
        if (needUpdate) {
            vbo.setData(buffer);
            record.version = version;
            record.sizeInBytes = buffer.capacity();
        }
    }

    public abstract int getNumBytesPerVertex();

    protected void allocate(int vertexCount) {
        buffer = ByteBuffer.allocateDirect(vertexCount * getNumBytesPerVertex());
        this.vertexCount = vertexCount;
    }

    public int getVersion() {
        return version;
    }

    public int getSizeInBytes() {
        return buffer.capacity();
    }

    public void bumpVersion() {
        version++;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Vbo getGLResource(GL2 gl) {
        return (Vbo)GLResourceCache.v().getGLResource(gl, this);
    }
}
