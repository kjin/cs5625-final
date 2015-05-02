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

package cs5625.gfx.json;

import com.google.gson.*;
import cs5625.gfx.objcache.LoadableByKey;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Value;
import cs5625.util.IOUtil;

import java.io.File;

public abstract class AbstractJsonSerializable implements JsonSerializable {
    @Override
    public JsonObject toJson(String directory) {
        JsonObject json = new JsonObject();
        json.addProperty("class", getClass().getName());
        fillJson(json, directory);
        return json;
    }

    protected abstract void fillJson(JsonObject json, String directory);

    public void loadByKey(String key) {
        String fileName = ObjectCacheKey.getKeyData(key);
        String content = IOUtil.readTextFile(fileName);
        String directory = new File(fileName).getParentFile().getAbsolutePath();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(content);
        if (!(element instanceof JsonObject)) {
            throw new RuntimeException("content of JSON file is not an object!");
        } else {
            fromJson((JsonObject)element, directory);
        }
    }

    public void saveByKey(String key) {
        String fileName = ObjectCacheKey.getKeyData(key);
        save(fileName);
    }

    public void save(String fileName) {
        save(fileName, false);
    }

    public void save(String fileName, boolean prettyPrint) {
        String directory = new File(fileName).getParentFile().getAbsolutePath();
        JsonObject obj = toJson(directory);
        Gson gson = null;
        if (prettyPrint) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        } else {
            gson = new Gson();
        }
        String content = gson.toJson(obj);
        IOUtil.writeTextFile(fileName, content);
    }

    public <T extends LoadableByKey> Value<T> wrap(Class<T> klass) {
        return new Value<T>((T)this);
    }
}
