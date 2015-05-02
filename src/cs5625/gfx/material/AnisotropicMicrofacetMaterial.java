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

import javax.vecmath.Color4f;

public class AnisotropicMicrofacetMaterial extends AbstractMaterial {
    private Color4f diffuseColor = new Color4f(1,1,1,1);
    private Holder<Texture2DData> diffuseTexture = null;
    private float indexOfRefraction = 1.33f; // default value based on water
    private float alphaX = 0.15f;
    private Holder<Texture2DData> alphaXTexture = null;
    private float alphaY = 0.5f;
    private Holder<Texture2DData> alphaYTexture = null;

    public Color4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Color4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Holder<Texture2DData> getDiffuseTexture() {
        return diffuseTexture;
    }

    public void setDiffuseTexture(Holder<Texture2DData> diffuseTexture) {
        this.diffuseTexture = diffuseTexture;
    }

    public float getIndexOfRefraction() {
        return indexOfRefraction;
    }

    public void setIndexOfRefraction(float indexOfRefraction) {
        this.indexOfRefraction = indexOfRefraction;
    }

    public float getAlphaX() {
        return alphaX;
    }

    public void setAlphaX(float alphaX) {
        this.alphaX = alphaX;
    }

    public Holder<Texture2DData> getAlphaXTexture() {
        return alphaXTexture;
    }

    public void setAlphaXTexture(Holder<Texture2DData> alphaXTexture) {
        this.alphaXTexture = alphaXTexture;
    }

    public float getAlphaY() {
        return alphaY;
    }

    public void setAlphaY(float alphaY) {
        this.alphaY = alphaY;
    }

    public Holder<Texture2DData> getAlphaYTexture() {
        return alphaYTexture;
    }

    public void setAlphaYTexture(Holder<Texture2DData> alphaYTexture) {
        this.alphaYTexture = alphaYTexture;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("diffuseColor", JsonUtil.toJson(diffuseColor));
        JsonUtil.serializeThenAddAsProperty(json, "diffuseTexture", diffuseTexture, directory);
        json.addProperty("indexOfRefraction", indexOfRefraction);
        json.addProperty("alphaX", alphaX);
        JsonUtil.serializeThenAddAsProperty(json, "alphaXTexture", alphaXTexture, directory);
        json.addProperty("alphaY", alphaY);
        JsonUtil.serializeThenAddAsProperty(json, "alphaYTexture", alphaYTexture, directory);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        JsonUtil.fromJson(json.getAsJsonArray("diffuseColor"), diffuseColor);
        diffuseTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("diffuseTexture"), directory);
        indexOfRefraction = json.get("indexOfRefraction").getAsFloat();
        alphaX = json.get("alphaX").getAsFloat();
        alphaXTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("alphaXTexture"), directory);
        alphaY = json.get("alphaY").getAsFloat();
        alphaYTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("alphaYTexture"), directory);
    }
}
