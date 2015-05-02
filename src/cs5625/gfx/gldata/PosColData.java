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

package cs5625.gfx.gldata;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import cs5625.jogl.Attribute;
import cs5625.jogl.AttributeSpec;
import cs5625.util.BufferUtil;

import javax.media.opengl.GL2;
import javax.vecmath.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Vertex data where each vertex has a position and a color.
 */
public class PosColData extends VertexData {
    public static final AttributeSpec POSITION = new AttributeSpec("position", 3,
            GL2.GL_FLOAT, false, 4 * 8, 0);
    public static final AttributeSpec COLOR = new AttributeSpec("color", 3,
            GL2.GL_FLOAT, false, 4 * 8, 4 * 3);
    private static final HashMap<String, AttributeSpec> vertexAttributes = new HashMap<String, AttributeSpec>();

    static {
        vertexAttributes.put("position", POSITION);
        vertexAttributes.put("vert_position", POSITION);
        vertexAttributes.put("vertex_position", POSITION);
        vertexAttributes.put("color", COLOR);
        vertexAttributes.put("vert_color", COLOR);
        vertexAttributes.put("vertex_color", COLOR);
    }

    public static class Builder {
        public PosColData data;
        public ArrayList<Point3f> positions = new ArrayList<Point3f>();
        public ArrayList<Vector4f> colors = new ArrayList<Vector4f>();
        private Vector4f currentColor = new Vector4f(1, 1, 1, 1);

        Builder(PosColData data) {
            this.data = data;
        }

        public Builder setColor(float r, float g, float b, float a) {
            currentColor.set(r, g, b, a);
            return this;
        }

        public Builder addVertex(float x, float y, float z) {
            positions.add(new Point3f(x, y, z));
            colors.add(new Vector4f(currentColor));
            return this;
        }

        public Builder addVertex(Tuple3f p) {
            return addVertex(p.x, p.y, p.z);
        }

        public PosColData endBuild() {
            if (positions.size() != colors.size()) {
                throw new RuntimeException("positions's size is not equal to color's size");
            }

            data.allocate(positions.size());
            for (int i = 0; i < positions.size(); i++) {
                data.setPosition(i, positions.get(i));
                data.setColor(i, colors.get(i));
            }
            data.bumpVersion();

            return data;
        }
    }



    public Builder startBuild() {
        return new Builder(this);
    }

    @Override
    public int getVertexCount() {
        return 0;
    }

    @Override
    public AttributeSpec getAttributeSpec(String name) {
        return vertexAttributes.get(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        return vertexAttributes.containsKey(name.toLowerCase());
    }

    @Override
    public int getNumBytesPerVertex() {
        return 4*8;
    }

    public int getNumDWordsPerVertex() {
        return getNumBytesPerVertex() / 4;
    }

    public void getPosition(int index, Tuple3f p) {
        p.x = BufferUtil.getLittleEndianInt(buffer, getNumDWordsPerVertex() * index + 0);
        p.y = BufferUtil.getLittleEndianInt(buffer, getNumDWordsPerVertex() * index + 1);
        p.z = BufferUtil.getLittleEndianInt(buffer, getNumDWordsPerVertex() * index + 2);
    }

    public void getColor(int index, Tuple4f p) {
        p.x = BufferUtil.getLittleEndianInt(buffer, getNumDWordsPerVertex() * index + 3);
        p.y = BufferUtil.getLittleEndianInt(buffer, getNumDWordsPerVertex() * index + 4);
        p.z = BufferUtil.getLittleEndianInt(buffer, getNumDWordsPerVertex() * index + 5);
        p.w = BufferUtil.getLittleEndianInt(buffer, getNumDWordsPerVertex() * index + 6);
    }

    private void setPosition(int index, float x, float y, float z) {
        BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 0, x);
        BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 1, y);
        BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 2, z);
    }

    private void setColor(int index, float r, float g, float b, float a) {
        BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 3, r);
        BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 4, g);
        BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 5, b);
        BufferUtil.setLittleEndianFloat(buffer, getNumDWordsPerVertex() * index + 6, a);
    }

    private void setPosition(int index, Tuple3f p) {
        setPosition(index, p.x, p.y, p.z);
    }

    private void setColor(int index, Tuple4f p) {
        setColor(index, p.x, p.y, p.z, p.w);
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        JsonArray positions = new JsonArray();
        Point3f p = new Point3f();
        for (int i = 0; i < getVertexCount(); i++) {
            getPosition(i, p);
            positions.add(new JsonPrimitive(p.x));
            positions.add(new JsonPrimitive(p.y));
            positions.add(new JsonPrimitive(p.z));
        }
        json.add("positions", positions);

        JsonArray colors = new JsonArray();
        Color4f color = new Color4f();
        for (int i = 0; i < getVertexCount(); i++) {
            getColor(i, color);
            positions.add(new JsonPrimitive(color.x));
            positions.add(new JsonPrimitive(color.y));
            positions.add(new JsonPrimitive(color.z));
            positions.add(new JsonPrimitive(color.w));
        }
        json.add("colors", colors);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        JsonArray positions = json.getAsJsonArray("positions");
        JsonArray colors = json.getAsJsonArray("colors");
        if (positions.size() / 3 != colors.size() / 4) {
            throw new RuntimeException("number of vertex positions not equal to number of vertex colors");
        }
        allocate(positions.size() / 3);
        for (int i = 0; i < getVertexCount(); i++) {
            setPosition(i,
                    positions.get(3*i+0).getAsFloat(),
                    positions.get(3*i+1).getAsFloat(),
                    positions.get(3*i+2).getAsFloat());
            setColor(i,
                    colors.get(4*i+0).getAsFloat(),
                    colors.get(4*i+1).getAsFloat(),
                    colors.get(4*i+2).getAsFloat(),
                    colors.get(4*i+3).getAsFloat());
        }
        bumpVersion();
    }
}
