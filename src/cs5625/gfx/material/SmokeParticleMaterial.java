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

package cs5625.gfx.material;

import com.google.gson.JsonObject;

import cs5625.gfx.gldata.FileTexture2DData;
import cs5625.gfx.gldata.Texture2DData;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.json.JsonUtil;
import cs5625.gfx.objcache.Holder;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Reference;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

public class SmokeParticleMaterial extends AbstractMaterial {
    /* Blinn-Phong material properties. */
    private Color4f diffuseColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);

    /* Optional textures for texture parameterized rendering. */
    private Holder<Texture2DData> normalTexture = null;
    
    /* Optional texture for 1D toon shading. */
    private Holder<Texture2DData> toonTexture = null;

    public SmokeParticleMaterial() {
    	String key = ObjectCacheKey.makeKey(FileTexture2DData.class, "data/textures/fancy-cloud-normal.png");
    	normalTexture = new Reference<Texture2DData>(key);
    }

    public SmokeParticleMaterial(Color4f diffuse) {
        diffuseColor.set(diffuse);
    }

    public Color4f getDiffuseColor() {
        return diffuseColor;
    }

    public SmokeParticleMaterial setDiffuseColor(Color4f diffuse) {
        diffuseColor.set(diffuse);
        return this;
    }

    public Holder<Texture2DData> getNormalTexture() {
        return normalTexture;
    }

    public SmokeParticleMaterial setNormalTexture(Holder<Texture2DData> texture) {
        normalTexture = texture;
        return this;
    }
    
    public Holder<Texture2DData> getToonTexture() {
        return toonTexture;
    }

    public SmokeParticleMaterial setToonTexture(Holder<Texture2DData> texture) {
        toonTexture = texture;
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("diffuseColor", JsonUtil.toJson(diffuseColor));
        JsonUtil.serializeThenAddAsProperty(json, "diffuseTexture", normalTexture, directory);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        JsonUtil.fromJson(json.getAsJsonArray("diffuseColor"), diffuseColor);
        normalTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("diffuseTexture"), directory);
    }
}
