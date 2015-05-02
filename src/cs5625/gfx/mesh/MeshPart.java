/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  Copyright (c) 2015, Department of Computer Science, Cornell University.
 *
 *  This code repository has been authored collectively by:
 *  Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

package cs5625.gfx.mesh;


import com.google.gson.JsonObject;
import cs5625.gfx.json.AbstractJsonSerializable;
import cs5625.gfx.json.JsonUtil;
import cs5625.gfx.material.Material;
import cs5625.gfx.objcache.Holder;

import javax.media.opengl.GL2;

public class MeshPart extends AbstractJsonSerializable {
    public int primitive = GL2.GL_TRIANGLES;
    public int vertexStart = 0;
    public int vertexCount = 0;
    public Holder<Material> material = null;

    public MeshPart() {
        // NOP
    }

    public MeshPart(int primitive, int vertexStart, int vertexCount, Holder<Material> material) {
        this.primitive = primitive;
        this.vertexStart = vertexStart;
        this.vertexCount = vertexCount;
        this.material = material;
    }

    public String getPrimitiveString() {
        switch (primitive) {
            case GL2.GL_POINTS:
                return "points";
            case GL2.GL_LINES:
                return "lines";
            case GL2.GL_TRIANGLES:
                return "triangles";
            default:
                throw new RuntimeException("invalid primitive type");
        }
    }

    public static int toPrimitiveConstant(String s) {
        s = s.toLowerCase();
        if (s.equals("points")) {
            return GL2.GL_POINTS;
        } else if (s.equals("lines")) {
            return GL2.GL_LINES;
        } else if (s.equals("triangles")) {
            return GL2.GL_TRIANGLES;
        } else {
            throw new RuntimeException("invalid primitive string");
        }
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.addProperty("primitive", getPrimitiveString());
        json.addProperty("vertexStart", vertexStart);
        json.addProperty("vertexCount", vertexCount);
        json.add("material", material.toJson(directory));
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        primitive = toPrimitiveConstant(json.get("primitive").getAsString());
        vertexStart = json.get("vertexStart").getAsInt();
        vertexCount = json.get("vertexCount").getAsInt();
        material = (Holder<Material>) JsonUtil.fromJson(json.get("material").getAsJsonObject(), directory);
    }
}
