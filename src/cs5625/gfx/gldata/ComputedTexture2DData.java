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
import cs5625.gfx.glcache.GLResourceRecord;
import cs5625.gfx.objcache.LoadableByKey;
import cs5625.gfx.objcache.Value;
import cs5625.jogl.Texture2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.media.opengl.GL2;
import java.nio.ByteBuffer;

public class ComputedTexture2DData implements Texture2DData {
    private static Logger logger = LoggerFactory.getLogger(ComputedTextureRectData.class);
    private int width;
    private int height;
    private int glFormat = Integer.MIN_VALUE;
    private int glType = Integer.MIN_VALUE;
    private ByteBuffer buffer = null;
    private int version;
    private int internalFormat = GL2.GL_RGBA32F;
    private boolean hasMipmap = true;

    public ComputedTexture2DData() {
    }

    public ComputedTexture2DData(int internalFormat) {
        this(internalFormat, true);
    }

    public ComputedTexture2DData(int internalFormat, boolean hasMipmap) {
        this.internalFormat = internalFormat;
        this.hasMipmap = hasMipmap;
    }

    public ComputedTexture2DData setInternalFormat(int internalFormat) {
        this.internalFormat = internalFormat;
        return this;
    }

    public void setData(int width, int height, int glFormat, int glType, ByteBuffer buffer) {
        this.width = width;
        this.height = height;
        this.glFormat = glFormat;
        this.glType = glType;
        this.buffer = buffer;
        bumpVersion();
    }
    
    public int getHeight() {
    	return height;
    }

    @Override
    public void updateTexture2D(Texture2D texture) {
        texture.setImage(width, height, glFormat, glType, buffer);
    }

    public int getWidth() {
    	return width;
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

    @Override
    public void updateGLResource(GL2 gl, GLResourceRecord record) {
        if (width == 0 || height == 0 || glFormat == Integer.MIN_VALUE || glType == Integer.MAX_VALUE) {
            throw new RuntimeException("data not initialized!");
        } else {
            Texture2D texture = null;
            boolean needUpdate = false;
            if (record.resource == null) {
                texture = new Texture2D(gl, internalFormat, hasMipmap);
                texture.minFilter = GL2.GL_NEAREST;
                texture.magFilter = GL2.GL_NEAREST;
                record.resource = texture;
                needUpdate = true;
            } else if (record.version != this.version) {
                texture = (Texture2D) record.resource;
                needUpdate = true;
            }
            if (needUpdate) {
                updateTexture2D(texture);
                record.version = version;
                record.sizeInBytes = buffer.capacity();
            }
        }
    }

    @Override
    public Texture2D getGLResource(GL2 gl) {
        return (Texture2D) GLResourceCache.v().getGLResource(gl, this);
    }

    @Override
    public void loadByKey(String key) {
        throw new RuntimeException("loadByKey not supported by this class");
    }

    @Override
    public <T extends LoadableByKey> Value<T> wrap(Class<T> klass) {
        return new Value<T>((T) this);
    }
}
