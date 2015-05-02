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

public class Reference<T extends LoadableByKey> extends AbstractJsonSerializable implements Holder<T> {
    public String key;

    public Reference() {
        // NOP
    }

    public Reference(String key) {
        this.key = key;
    }

    public Reference(String protocol, String keyData) {
        this.key = ObjectCacheKey.makeKey(protocol, keyData);
    }

    public Reference(Class klass, String keyData) {
        this.key = ObjectCacheKey.makeKey(klass, keyData);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.addProperty("key", ObjectCacheKey.convertToRelativePathKey(key, directory));
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        key = ObjectCacheKey.convertToAbsolutePathKey(json.get("key").getAsString(), directory);
    }

    @Override
    public T get() {
        return (T)ObjectCache.v().get(this);
    }
}
