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

public class IsotropicMicrofacetMaterial extends AbstractMaterial {
    private Color4f diffuseColor = new Color4f(1,1,1,1);
    private Holder<Texture2DData> diffuseTexture = null;
    private float indexOfRefraction = 1.33f; // default value based on water
    private float alpha = 0.4f;
    private Holder<Texture2DData> alphaTexture = null;

    public Color4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Color4f diffuseColor) {
        this.diffuseColor.set(diffuseColor);
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

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Holder<Texture2DData> getAlphaTexture() {
        return alphaTexture;
    }

    public void setAlphaTexture(Holder<Texture2DData> alphaTexture) {
        this.alphaTexture = alphaTexture;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("diffuseColor", JsonUtil.toJson(diffuseColor));
        JsonUtil.serializeThenAddAsProperty(json, "diffuseTexture", diffuseTexture, directory);
        json.addProperty("indexOfRefraction", indexOfRefraction);
        json.addProperty("alpha", alpha);
        JsonUtil.serializeThenAddAsProperty(json, "alphaTexture", alphaTexture, directory);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        JsonUtil.fromJson(json.getAsJsonArray("diffuseColor"), diffuseColor);
        diffuseTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("diffuseTexture"), directory);
        indexOfRefraction = json.get("indexOfRefraction").getAsFloat();
        alpha = json.get("alpha").getAsFloat();
        alphaTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("alphaTexture"), directory);
    }
}
