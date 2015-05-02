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

import cs5625.gfx.json.JsonSerializable;
import cs5625.gfx.json.JsonUtil;

import java.util.HashMap;

/**
 * Implements a cache of object that can be loadable by giving a key.
 * Follows the singleton pattern.
 */
public class ObjectCache {
    private static class SingletonHolder {
        private static final ObjectCache instance = new ObjectCache();
    }

    /**
     * @return the singleton instance of this class.
     */
    public static ObjectCache v() {
        return SingletonHolder.instance;
    }

    private HashMap<String, LoadableByKey> cache = new HashMap<String, LoadableByKey>();

    private ObjectCache() {
        // NOP
    }

    public LoadableByKey get(Holder holder) {
        if (holder instanceof Value) {
            Value actual = (Value)holder;
            return actual.get();
        } else if (holder instanceof Reference) {
            Reference cachedHolder = (Reference) holder;
            String key = cachedHolder.getKey();
            return load(key);
        } else {
            throw new RuntimeException("invalid data holder type");
        }
    }

    public LoadableByKey load(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            String protocol = ObjectCacheKey.getProtocol(key);
            if (protocol.equals("jsonfile")) {
                String fileName = ObjectCacheKey.getKeyData(key);
                JsonSerializable o = JsonUtil.load(fileName);
                cache.put(key, o);
                return o;
            } else {
                LoadableByKey o = LoadSaveUtil.loadByKey(key);
                cache.put(key, o);
                return o;
            }
        }
    }

    public LoadableByKey load(Class klass, String keyData) {
        return load(ObjectCacheKey.makeKey(klass, keyData));
    }

    public LoadableByKey load(String protocol, String keyData) {
        return load(ObjectCacheKey.makeKey(protocol, keyData));
    }
}
