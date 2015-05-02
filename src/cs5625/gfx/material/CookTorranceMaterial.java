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

public class CookTorranceMaterial extends AbstractMaterial {
    /* Cook-Torrance material properties. */
    private Color4f diffuseColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
    private Color3f specularColor = new Color3f(1.0f, 1.0f, 1.0f);
    private float MVal = 0.5f;
    private float NVal = 1.4f;

    /* Optional textures for texture parameterized rendering. */
    private Holder<Texture2DData> diffuseTexture = null;
    private Holder<Texture2DData> specularTexture = null;
    private Holder<Texture2DData> MTexture = null;
    private Holder<Texture2DData> NTexture = null;

    public CookTorranceMaterial() {
        /* Default constructor */
    }

    public CookTorranceMaterial(Color4f diffuse) {
        diffuseColor.set(diffuse);
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

    public float getM() {
        return MVal;
    }

    public void setM(float m) {
        MVal = m;
    }

    public float getN() {
        return NVal;
    }

    public void setN(float n) {
        NVal = n;
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

    public Holder<Texture2DData> getMTexture() {
        return MTexture;
    }

    public void setMTexture(Holder<Texture2DData> texture) {
        MTexture = texture;
    }

    public Holder<Texture2DData> getNTexture() {
        return NTexture;
    }

    public void setNTexture(Holder<Texture2DData> texture) {
        NTexture = texture;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("diffuseColor", JsonUtil.toJson(diffuseColor));
        JsonUtil.serializeThenAddAsProperty(json, "diffuseTexture", diffuseTexture, directory);
        json.add("specularColor", JsonUtil.toJson(specularColor));
        JsonUtil.serializeThenAddAsProperty(json, "specularTexture", specularTexture, directory);
        json.addProperty("M", MVal);
        JsonUtil.serializeThenAddAsProperty(json, "MTexture", MTexture, directory);
        json.addProperty("N", NVal);
        JsonUtil.serializeThenAddAsProperty(json, "NTexture", NTexture, directory);
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
        MVal = json.getAsJsonPrimitive("M").getAsFloat();
        MTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("MTexture"), directory);
        NVal = json.getAsJsonPrimitive("N").getAsFloat();
        NTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("NTexture"), directory);
    }
}
