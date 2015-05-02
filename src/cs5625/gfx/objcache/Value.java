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

package cs5625.gfx.objcache;

import com.google.gson.JsonObject;
import cs5625.gfx.json.AbstractJsonSerializable;
import cs5625.gfx.json.JsonSerializable;
import cs5625.gfx.json.JsonUtil;

public class Value<T extends LoadableByKey> extends AbstractJsonSerializable implements Holder<T> {
    private T object;

    public Value() {
        // NOP
    }

    public Value(T object) {
        this.object = object;
    }

    @Override
    public T get() {
        return object;
    }

    public void set(T obj) {
        this.object = obj;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        if (!(object instanceof JsonSerializable)) {
            throw new RuntimeException("the stored object cannot be serialized to JSON");
        } else {
            JsonSerializable obj = (JsonSerializable)object;
            json.add("object", obj.toJson(directory));
        }
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        object = (T) JsonUtil.fromJson(json.get("object"), directory);
    }
}
