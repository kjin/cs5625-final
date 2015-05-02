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
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.jogl.TextureCubeMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.media.opengl.GL2;

public class RenderedTextureCubeMapData extends AbstractNamedObject implements TextureCubeMapData {
    private String proxyName = null;

    public String getProxyName() {
        return proxyName;
    }

    public RenderedTextureCubeMapData setProxyName(String proxyName) {
        this.proxyName = proxyName;
        return this;
    }

    @Override
    public void updateGLResource(GL2 gl, GLResourceRecord record) {
        throw new NotImplementedException();
    }

    @Override
    public TextureCubeMap getGLResource(GL2 gl) {
        return (TextureCubeMap) GLResourceCache.v().getGLResource(gl, this);
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.addProperty("proxyName", proxyName);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        proxyName = json.get("proxyName").getAsString();
    }
}
