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
import cs5625.gfx.gldata.TextureCubeMapData;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.json.JsonUtil;
import cs5625.gfx.objcache.Holder;

import javax.vecmath.Matrix3f;

public class ReflectionMaterial extends AbstractMaterial {
    private Holder<TextureCubeMapData> cubeMap = null;
    private Matrix3f worldToCubeMap = new Matrix3f();

    public ReflectionMaterial() {
        worldToCubeMap.setIdentity();
    }

    public Holder<TextureCubeMapData> getCubeMap() {
        return cubeMap;
    }

    public ReflectionMaterial setCubeMap(Holder<TextureCubeMapData> cubeMap) {
        this.cubeMap = cubeMap;
        return this;
    }

    public Matrix3f getWorldToCubeMap() {
        return worldToCubeMap;
    }

    public void setWorldToCubeMap(Matrix3f worldToCubeMap) {
        this.worldToCubeMap.set(worldToCubeMap);
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
        JsonUtil.serializeThenAddAsProperty(json, "cubeMap", cubeMap, directory);
        json.add("worldToCubeMap", JsonUtil.toJson(worldToCubeMap));
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        cubeMap = (Holder<TextureCubeMapData>) JsonUtil.fromJson(json.get("cubeMap"), directory);
        if (json.has("worldToCubeMap")) {
            JsonUtil.fromJson(json.getAsJsonArray("worldToCubeMap"), worldToCubeMap);
        }
    }
}
