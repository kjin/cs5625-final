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

public class AnisotropicWardMaterial extends AbstractMaterial {
    /* Isotropic Ward material properties. */
    private Color4f diffuseColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
    private Color3f specularColor = new Color3f(1.0f, 1.0f, 1.0f);
    private float alphaX = 0.15f;
    private float alphaY = 0.5f;

    /* Optional textures for texture parameterized rendering. */
    private Holder<Texture2DData> diffuseTexture = null;
    private Holder<Texture2DData> specularTexture = null;
    private Holder<Texture2DData> alphaXTexture = null;
    private Holder<Texture2DData> alphaYTexture = null;

    public AnisotropicWardMaterial() {
        /* Default constructor */
    }

    public AnisotropicWardMaterial(Color4f diffuse) {
        diffuseColor.set(diffuse);
    }

    public AnisotropicWardMaterial(Color4f diffuse, Color3f specular, float alphaX, float alphaY) {
        diffuseColor.set(diffuse);
        specularColor.set(specular);
        this.alphaX = alphaX;
        this.alphaY = alphaY;
    }

    public Color4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Color4f diffuse) {
        diffuseColor.set(diffuse);
    }

    public Color3f getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Color3f specular) {
        specularColor = specular;
    }

    public float getAlphaX() {
        return alphaX;
    }

    public void setAlphaX(float m) {
        alphaX = m;
    }

    public float getAlphaY() {
        return alphaY;
    }

    public void setAlphaY(float m) {
        alphaY = m;
    }

    public Holder<Texture2DData> getDiffuseTexture() {
        return diffuseTexture;
    }

    public void setDiffuseTexture(Holder<Texture2DData> texture) {
        diffuseTexture = texture;
    }

    public Holder<Texture2DData> getSpecularTexture() {
        return specularTexture;
    }

    public void setSpecularTexture(Holder<Texture2DData> texture) {
        specularTexture = texture;
    }

    public Holder<Texture2DData> getAlphaXTexture() {
        return alphaXTexture;
    }

    public void setAlphaXTexture(Holder<Texture2DData> texture) {
        alphaXTexture = texture;
    }

    public Holder<Texture2DData> getAlphaYTexture() {
        return alphaYTexture;
    }

    public void setAlphaYTexture(Holder<Texture2DData> texture) {
        alphaYTexture = texture;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("diffuseColor", JsonUtil.toJson(diffuseColor));
        JsonUtil.serializeThenAddAsProperty(json, "diffuseTexture", diffuseTexture, directory);
        json.add("specularColor", JsonUtil.toJson(specularColor));
        JsonUtil.serializeThenAddAsProperty(json, "specularTexture", specularTexture, directory);
        json.addProperty("alphaX", alphaX);
        JsonUtil.serializeThenAddAsProperty(json, "alphaXTexture", alphaXTexture, directory);
        json.addProperty("alphaY", alphaY);
        JsonUtil.serializeThenAddAsProperty(json, "alphaYTexture", alphaYTexture, directory);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        diffuseTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("diffuseTexture"), directory);
        JsonUtil.fromJson(json.getAsJsonArray("specularColor"), specularColor);
        specularTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("specularTexture"), directory);
        alphaX = json.getAsJsonPrimitive("alphaX").getAsFloat();
        alphaXTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("alphaXTexture"), directory);
        alphaY = json.getAsJsonPrimitive("alphaY").getAsFloat();
        alphaYTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("alphaYTexture"), directory);
    }
}
