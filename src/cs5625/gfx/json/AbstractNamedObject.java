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

import com.google.gson.JsonObject;
import cs5625.gfx.objcache.LoadableByKey;
import cs5625.gfx.objcache.Value;

public abstract class AbstractNamedObject extends AbstractJsonSerializable implements NamedObject {
    protected String name = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public JsonObject toJson(String directory) {
        JsonObject json = new JsonObject();
        json.addProperty("class", getClass().getName());
        //System.out.println(getClass().getName());
        //System.out.println(name);
        json.addProperty("name", name);
        fillJson(json, directory);
        return json;
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        name = json.has("name") ? json.get("name").getAsString() : null;
    }
}
