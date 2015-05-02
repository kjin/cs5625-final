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
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.util.IOUtil;

import javax.vecmath.Matrix3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import java.io.File;

public class JsonUtil {
    public static JsonArray toJson(Tuple3f t) {
        JsonArray a = new JsonArray();
        a.add(new JsonPrimitive(t.x));
        a.add(new JsonPrimitive(t.y));
        a.add(new JsonPrimitive(t.z));
        return a;
    }

    public static void fromJson(JsonArray json, Tuple3f t) {
        t.x = json.get(0).getAsFloat();
        t.y = json.get(1).getAsFloat();
        t.z = json.get(2).getAsFloat();
    }

    public static JsonArray toJson(Tuple4f t) {
        JsonArray a = new JsonArray();
        a.add(new JsonPrimitive(t.x));
        a.add(new JsonPrimitive(t.y));
        a.add(new JsonPrimitive(t.z));
        a.add(new JsonPrimitive(t.w));
        return a;
    }

    public static void fromJson(JsonArray json, Tuple4f t) {
        t.x = json.get(0).getAsFloat();
        t.y = json.get(1).getAsFloat();
        t.z = json.get(2).getAsFloat();
        t.w = json.get(3).getAsFloat();
    }

    public static JsonArray toJson(Tuple2f t) {
        JsonArray a = new JsonArray();
        a.add(new JsonPrimitive(t.x));
        a.add(new JsonPrimitive(t.y));
        return a;
    }

    public static void fromJson(JsonArray json, Tuple2f t) {
        t.x = json.get(0).getAsFloat();
        t.y = json.get(1).getAsFloat();
    }

    public static JsonArray toJson(Matrix3f m) {
        JsonArray a = new JsonArray();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float x = m.getElement(i,j);
                a.add(new JsonPrimitive(x));
            }
        }
        return a;
    }

    public static void fromJson(JsonArray json, Matrix3f m) {
        for (int k = 0; k < 9; k++) {
            float x = json.get(k).getAsFloat();
            m.setElement(k/3, k%3, x);
        }
    }

    public static JsonSerializable load(String fileName) {
        String content = IOUtil.readTextFile(fileName);
        String directory = new File(fileName).getParentFile().getAbsolutePath();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(content);
        if (!(element instanceof JsonObject)) {
            throw new RuntimeException("content of JSON file is not an object!");
        } else {
            return fromJson((JsonObject)element, directory);
        }
    }

    public static JsonSerializable fromJson(JsonElement json, String directory) {
        if (json == null)
            return null;
        if (json instanceof JsonNull) {
            return null;
        }
        if (!(json instanceof JsonObject)) {
            throw new RuntimeException("json element is not an instanceof JsonObject");
        }
        JsonObject obj = (JsonObject)json;

        String className = obj.getAsJsonPrimitive("class").getAsString();
        Class klass = null;
        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class '" + className + "' does not exists", e);
        }
        if (!JsonSerializable.class.isAssignableFrom(klass)) {
            throw new RuntimeException("the class being deserialized from JSON is not an instance of " +
                    "JsonSerializeable");
        }
        try {
            JsonSerializable jsonSerializable = (JsonSerializable)klass.newInstance();
            jsonSerializable.fromJson(obj, directory);
            return jsonSerializable;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void serializeThenAddAsProperty(JsonObject json,
                                                  String propertyName,
                                                  JsonSerializable obj,
                                                  String directory) {
        if (obj == null) {
            json.add(propertyName, null);
        } else {
            json.add(propertyName, obj.toJson(directory));
        }
    }
}
