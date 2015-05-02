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

public class LambertianMaterial extends AbstractMaterial {
    private Color4f diffuseColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
    private Holder<Texture2DData> diffuseTexture = null;

    public LambertianMaterial() {
        /* Default constructor */
    }

    public LambertianMaterial(Color4f diffuse) {
        diffuseColor.set(diffuse);
    }

    public Color4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Color4f diffuse) {
        diffuseColor.set(diffuse);
    }

    public Holder<Texture2DData> getDiffuseTexture() {
        return diffuseTexture;
    }

    public void setDiffuseTexture(Holder<Texture2DData> texture) {
        diffuseTexture = texture;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("diffuseColor", JsonUtil.toJson(diffuseColor));
        JsonUtil.serializeThenAddAsProperty(json, "diffuseTexture", diffuseTexture, directory);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        JsonUtil.fromJson(json.getAsJsonArray("diffuseColor"), diffuseColor);
        diffuseTexture = (Holder<Texture2DData>) JsonUtil.fromJson(
                json.get("diffuseTexture"), directory);
    }
}
