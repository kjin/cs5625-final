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
import cs5625.gfx.gldata.Texture2DData;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.json.JsonUtil;
import cs5625.gfx.objcache.Holder;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

public class SmokeParticleMaterial extends AbstractMaterial {
    /* Blinn-Phong material properties. */
    private Color4f diffuseColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
    private Color3f specularColor = new Color3f(1.0f, 1.0f, 1.0f);
    private float exponent = 50.0f;

    /* Optional textures for texture parameterized rendering. */
    private Holder<Texture2DData> diffuseTexture = null;
    private Holder<Texture2DData> specularTexture = null;
    private Holder<Texture2DData> exponentTexture = null;

    public SmokeParticleMaterial() {
        /* Default constructor. */
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

    public Color3f getSpecularColor() {
        return specularColor;
    }

    public SmokeParticleMaterial setSpecularColor(Color3f specular) {
        specularColor.set(specular);
        return this;
    }

    public float getExponent() {
        return exponent;
    }

    public SmokeParticleMaterial setExponent(float exponent) {
        this.exponent = exponent;
        return this;
    }

    public Holder<Texture2DData> getDiffuseTexture() {
        return diffuseTexture;
    }

    public SmokeParticleMaterial setDiffuseTexture(Holder<Texture2DData> texture) {
        diffuseTexture = texture;
        return this;
    }

    public Holder<Texture2DData> getSpecularTexture() {
        return specularTexture;
    }

    public SmokeParticleMaterial setSpecularTexture(Holder<Texture2DData> texture) {
        specularTexture = texture;
        return this;
    }

    public Holder<Texture2DData> getExponentTexture() {
        return exponentTexture;
    }

    public SmokeParticleMaterial setExponentTexture(Holder<Texture2DData> texture) {
        exponentTexture = texture;
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("diffuseColor", JsonUtil.toJson(diffuseColor));
        JsonUtil.serializeThenAddAsProperty(json, "diffuseTexture", diffuseTexture, directory);
        json.add("specularColor", JsonUtil.toJson(specularColor));
        JsonUtil.serializeThenAddAsProperty(json, "specularTexture", specularTexture, directory);
        json.addProperty("exponent", exponent);
        JsonUtil.serializeThenAddAsProperty(json, "exponentTexture", exponentTexture, directory);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        JsonUtil.fromJson(json.getAsJsonArray("diffuseColor"), diffuseColor);
        diffuseTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("diffuseTexture"), directory);
        JsonUtil.fromJson(json.getAsJsonArray("specularColor"), specularColor);
        specularTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("specularTexture"), directory);
        exponent = json.getAsJsonPrimitive("exponent").getAsFloat();
        exponentTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("exponentTexture"), directory);
    }
}
