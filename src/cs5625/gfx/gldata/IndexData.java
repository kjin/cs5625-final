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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import cs5625.gfx.glcache.GLResourceCache;
import cs5625.gfx.glcache.GLResourceProvider;
import cs5625.gfx.glcache.GLResourceRecord;
import cs5625.gfx.json.AbstractJsonSerializable;
import cs5625.gfx.json.JsonSerializable;
import cs5625.jogl.GLResource;
import cs5625.jogl.Vbo;
import cs5625.jogl.VboTarget;
import cs5625.util.BufferUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.media.opengl.GL2;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class IndexData extends AbstractJsonSerializable implements GLResourceProvider<Vbo> {
    protected ByteBuffer buffer;
    private int version = 0;
    private int indexCount;

    public static class Builder {
        public ArrayList<Integer> indices = new ArrayList<Integer>();
        public IndexData data;

        Builder(IndexData data) {
            this.data = data;
        }

        public Builder add(int index) {
            indices.add(index);
            return this;
        }

        public IndexData endBuild() {
            int indexCount = indices.size();
            data.allocate(indexCount);
            for (int i = 0; i < indexCount; i++) {
                data.setIndex(i, indices.get(i));
            }
            data.bumpVersion();
            return data;
        }
    }

    public Builder startBuild() {
        return new Builder(this);
    }

    private void allocate(int indexCount) {
        buffer = ByteBuffer.allocateDirect(4 * indexCount);
        this.indexCount = indexCount;
    }

    @Override
    public void updateGLResource(GL2 gl, GLResourceRecord record) {
        Vbo vbo = null;
        boolean needUpdate = false;
        if (record.resource == null) {
            vbo = new Vbo(gl, VboTarget.ELEMENT_ARRAY_BUFFER);
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

    @Override
    public Vbo getGLResource(GL2 gl) {
        return (Vbo) GLResourceCache.v().getGLResource(gl, this);
    }

    public void setIndex(int index, int value) {
        BufferUtil.setLittleEndianInt(buffer, index, value);
    }

    public int getIndex(int index) {
        return BufferUtil.getLittleEndianInt(buffer, index);
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

    public int getIndexCount() {
        return indexCount;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        JsonArray indices = new JsonArray();
        Point3f p = new Point3f();
        for (int i = 0; i < getIndexCount(); i++) {
            int index = getIndex(i);
            indices.add(new JsonPrimitive(index));
        }
        json.add("indices", indices);
    }


    @Override
    public void fromJson(JsonObject json, String directory) {
        JsonArray indices = json.getAsJsonArray("indices");
        allocate(indices.size());
        for (int i = 0; i < getIndexCount(); i++) {
            setIndex(i, indices.get(i).getAsInt());
        }
        bumpVersion();
    }
}
