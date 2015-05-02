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

public class IsotropicWardMaterial extends AbstractMaterial {
    /* Isotropic Ward material properties. */
    private Color4f diffuseColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
    private Color3f specularColor = new Color3f(1.0f, 1.0f, 1.0f);
    private float alpha = 0.4f;

    /* Optional textures for texture parameterized rendering. */
    private Holder<Texture2DData> diffuseTexture = null;
    private Holder<Texture2DData> specularTexture = null;
    private Holder<Texture2DData> alphaTexture = null;

    public IsotropicWardMaterial() {
        /* Default constructor */
    }

    public IsotropicWardMaterial(Color4f diffuse) {
        diffuseColor.set(diffuse);
    }

    public IsotropicWardMaterial(Color4f diffuse, Color3f specular, float alpha) {
        diffuseColor.set(diffuse);
        specularColor.set(specular);
        this.alpha = alpha;
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
        specularColor.set(specular);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float m) {
        alpha = m;
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

    public Holder<Texture2DData> getAlphaTexture() {
        return alphaTexture;
    }

    public void setAlphaTexture(Holder<Texture2DData> texture) {
        alphaTexture = texture;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("diffuseColor", JsonUtil.toJson(diffuseColor));
        JsonUtil.serializeThenAddAsProperty(json, "diffuseTexture", diffuseTexture, directory);
        json.add("specularColor", JsonUtil.toJson(specularColor));
        JsonUtil.serializeThenAddAsProperty(json, "specularTexture", specularTexture, directory);
        json.addProperty("alpha", alpha);
        JsonUtil.serializeThenAddAsProperty(json, "alphaTexture", alphaTexture, directory);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        diffuseTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("diffuseTexture"), directory);
        JsonUtil.fromJson(json.getAsJsonArray("specularColor"), specularColor);
        specularTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("specularTexture"), directory);
        alpha = json.getAsJsonPrimitive("alpha").getAsFloat();
        alphaTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("alphaTexture"), directory);
    }
}
