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
import com.google.gson.JsonPrimitive;
import cs5625.gfx.json.AbstractNamedObject;

import javax.media.opengl.GL2;

public class AbstractMaterial extends AbstractNamedObject implements Material {
    protected boolean blendingEnabled = false;
    protected int sourceBlendFunc = GL2.GL_SRC_ALPHA;
    protected int destBlendFunc = GL2.GL_ONE_MINUS_SRC_ALPHA;

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.addProperty("blendingEnabled", blendingEnabled);
        json.addProperty("sourceBlendFunc", MaterialUtil.blendFuncName(sourceBlendFunc));
        json.addProperty("destBlendFunc", MaterialUtil.blendFuncName(destBlendFunc));
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        if (json.has("blendingEnabled")) {
            blendingEnabled = json.get("blendingEnabled").getAsBoolean();
        }
        if (json.has("sourceBlendFunc")) {
            String s = json.get("sourceBlendFunc").getAsString();
            sourceBlendFunc = MaterialUtil.blendFuncConstant(s);
        }
        if (json.has("destBlendFunc")) {
            String s = json.get("destBlendFunc").getAsString();
            destBlendFunc = MaterialUtil.blendFuncConstant(s);
        }
    }

    @Override
    public boolean isBlendingEnabled() {
        return blendingEnabled;
    }

    @Override
    public int getSourceBlendFunc() {
        return sourceBlendFunc;
    }

    @Override
    public int getDestBlendFunc() {
        return destBlendFunc;
    }
}
