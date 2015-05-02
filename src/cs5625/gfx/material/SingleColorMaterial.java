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
import cs5625.gfx.json.AbstractJsonSerializable;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.json.JsonUtil;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

public class SingleColorMaterial extends AbstractMaterial {
    private Color4f color = new Color4f(1, 1, 1, 1);

    public SingleColorMaterial() {
        // NOP
    }

    public SingleColorMaterial(Color4f color) {
        this.color.set(color);
    }

    public SingleColorMaterial(float x, float y, float z, float w) {
        this.color.set(x,y,z,w);
    }

    public Color4f getColor() {
        return color;
    }

    public void setColor(Color4f color) {
        this.color.set(color);
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        json.add("color", JsonUtil.toJson(color));
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        JsonUtil.fromJson(json.getAsJsonArray("color"), color);
    }
}
