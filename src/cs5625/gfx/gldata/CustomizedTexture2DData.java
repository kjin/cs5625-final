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

import com.google.gson.JsonObject;
import cs5625.gfx.glcache.GLResourceCache;
import cs5625.gfx.glcache.GLResourceRecord;
import cs5625.gfx.json.AbstractJsonSerializable;
import cs5625.gfx.objcache.Reference;
import cs5625.jogl.Texture2D;

import javax.media.opengl.GL2;

public class CustomizedTexture2DData extends AbstractJsonSerializable implements Texture2DData {
    private Reference<Texture2DData> source = null;
    private int minFilter = GL2.GL_NEAREST;
    private int magFilter = GL2.GL_NEAREST;
    private int wrapS = GL2.GL_REPEAT;
    private int wrapT = GL2.GL_REPEAT;
    private boolean srgb = false;
    private int version = 0;

    public int getVersion() {
        return version;
    }

    public void bumpVersion() {
        version++;
    }

    public int getMinFilter() {
        return minFilter;
    }

    public CustomizedTexture2DData setMinFilter(int minFilter) {
        this.minFilter = minFilter;
        bumpVersion();
        return this;
    }

    public int getMagFilter() {
        return magFilter;
    }

    public CustomizedTexture2DData setMagFilter(int magFilter) {
        this.magFilter = magFilter;
        bumpVersion();
        return this;
    }

    public int getWrapS() {
        return wrapS;
    }

    public CustomizedTexture2DData setWrapS(int wrapS) {
        this.wrapS = wrapS;
        bumpVersion();
        return this;
    }

    public int getWrapT() {
        return wrapT;
    }

    public CustomizedTexture2DData setWrapT(int wrapT) {
        this.wrapT = wrapT;
        bumpVersion();
        return this;
    }

    public boolean isSrgb() {
        return srgb;
    }

    public CustomizedTexture2DData setSrgb(boolean srgb) {
        this.srgb = srgb;
        bumpVersion();
        return this;
    }

    public Reference<Texture2DData> getSource() {
        return source;
    }

    public CustomizedTexture2DData setSource(Reference<Texture2DData> source) {
        this.source = source;
        bumpVersion();
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.add("source", source.toJson(directory));

    }

    @Override
    public void fromJson(JsonObject json, String directory) {

    }

    @Override
    public int getWidth() {
        return source.get().getWidth();
    }

    @Override
    public int getHeight() {
        return source.get().getHeight();
    }

    @Override
    public void updateTexture2D(Texture2D texture) {
        source.get().updateTexture2D(texture);
    }

    @Override
    public void updateGLResource(GL2 gl, GLResourceRecord record) {
        Texture2D texture = null;
        boolean needUpdate = false;
        if (record.resource == null) {
            texture = new Texture2D(gl, srgb ? GL2.GL_SRGB8_ALPHA8 : GL2.GL_RGBA8);
            record.resource = texture;
            needUpdate = true;
        } else if (record.version != this.version) {
            texture = (Texture2D) record.resource;
            needUpdate = true;
        }
        if (needUpdate) {
            updateTexture2D(texture);
            texture.wrapS = wrapS;
            texture.wrapT = wrapT;
            texture.minFilter = minFilter;
            texture.magFilter = magFilter;
            record.version = version;
            record.sizeInBytes = getWidth() * getHeight() * 4;
        }
    }

    @Override
    public Texture2D getGLResource(GL2 gl) {
        return (Texture2D) GLResourceCache.v().getGLResource(gl, this);
    }
}
