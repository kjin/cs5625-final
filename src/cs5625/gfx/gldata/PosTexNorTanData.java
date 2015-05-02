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
import cs5625.jogl.AttributeSpec;
import cs5625.util.BufferUtil;
import cs5625.util.VectorMathUtil;

import javax.media.opengl.GL2;
import javax.vecmath.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PosTexNorTanData extends VertexData {
    /**
     * Structure of a vertex record.
     * <p/>
     * 1) Position (3 DWORDS)
     * 2) Texture coordinates (2 DWORDS)
     * 3) Normal (3 DWORDS)
     * 4) Tangent (4 DWORDS)
     */
    private static int VERTEX_DWORD_COUNT = 3 + 2 + 3 + 4;

    public static final AttributeSpec POSITION = new AttributeSpec("position", 3, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 0);
    public static final AttributeSpec TEXCOORD = new AttributeSpec("texCoord", 2, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * 3);
    public static final AttributeSpec NORMAL = new AttributeSpec("normal", 3, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * (3 + 2));
    public static final AttributeSpec TANGENT = new AttributeSpec("tangent", 4, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * (3 + 2 + 3));
    private static final HashMap<String, AttributeSpec> vertexAttributes = new HashMap<String, AttributeSpec>();

    static {
        vertexAttributes.put("position", POSITION);
        vertexAttributes.put("vert_position", POSITION);
        vertexAttributes.put("vertex_position", POSITION);
        vertexAttributes.put("texcoord", TEXCOORD);
        vertexAttributes.put("vert_texcoord", TEXCOORD);
        vertexAttributes.put("vertex_texcoord", TEXCOORD);
        vertexAttributes.put("normal", NORMAL);
        vertexAttributes.put("vert_normal", NORMAL);
        vertexAttributes.put("vertex_normal", NORMAL);
        vertexAttributes.put("tangent", TANGENT);
        vertexAttributes.put("vert_tangent", TANGENT);
        vertexAttributes.put("vertex_tangent", TANGENT);
    }

    public static class Builder {
        PosTexNorTanData data;
        public ArrayList<Point3f> positions = new ArrayList<Point3f>();
        public ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();
        public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
        public ArrayList<Vector4f> tangents = new ArrayList<Vector4f>();

        public Builder(PosTexNorTanData data) {
            this.data = data;
        }

        public Builder addPosition(float x, float y, float z) {
            positions.add(new Point3f(x, y, z));
            return this;
        }

        public Builder addPosition(Tuple3f p) {
            addPosition(p.x, p.y, p.z);
            return this;
        }

        public Builder addNormal(float x, float y, float z) {
            normals.add(new Vector3f(x, y, z));
            return this;
        }

        public Builder addNormal(Tuple3f n) {
            addNormal(n.x, n.y, n.z);
            return this;
        }

        public Builder addTangent(float x, float y, float z, float w) {
            tangents.add(new Vector4f(x, y, z, w));
            return this;
        }

        public Builder addTangent(Tuple4f t) {
            addTangent(t.x, t.y, t.z, t.w);
            return this;
        }

        public Builder addTexCoord(float x, float y) {
            texCoords.add(new Vector2f(x, y));
            return this;
        }

        public Builder addTexCoord(Tuple2f t) {
            addTexCoord(t.x, t.y);
            return this;
        }

        public boolean hasNormal() {
            return !normals.isEmpty();
        }

        public boolean hasTangents() {
            return !tangents.isEmpty();
        }

        public boolean hasTexCoords() {
            return !texCoords.isEmpty();
        }

        public PosTexNorTanData endBuild() {
            int vertexCount = positions.size();
            if (!texCoords.isEmpty() && texCoords.size() != vertexCount) {
                throw new RuntimeException("invalid number of texture coordinates (either 0 or equal to number of vertices)");
            }
            if (!normals.isEmpty() && normals.size() != vertexCount) {
                throw new RuntimeException("invalid number of normals (either 0 or equal to number of vertices)");
            }
            if (!tangents.isEmpty() && tangents.size() != vertexCount) {
                throw new RuntimeException("invalid number of tangents (either 0 or equal to number of vertices)");
            }

            data.allocate(positions.size());

            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
                data.setPosition(vertexIndex, positions.get(vertexIndex));

                if (texCoords.isEmpty()) {
                    data.setTexCoord(vertexIndex, 0, 0);
                } else {
                    data.setTexCoord(vertexIndex, texCoords.get(vertexIndex));
                }

                if (!normals.isEmpty()) {
                    data.setNormal(vertexIndex, normals.get(vertexIndex));
                } else {
                    data.setNormal(vertexIndex, 0, 0, 1);
                }

                if (!tangents.isEmpty()) {
                    data.setTangent(vertexIndex, tangents.get(vertexIndex));
                } else {
                    data.setTangent(vertexIndex, 0, 0, 1, 1);
                }
            }

            data.bumpVersion();
            return data;
        }
    }

    public Builder startBuild() {
        return new Builder(this);
    }

    @Override
    public AttributeSpec getAttributeSpec(String name) {
        return vertexAttributes.get(name.toLowerCase());
    }

    @Override
    public boolean hasAttribute(String name) {
        return vertexAttributes.containsKey(name.toLowerCase());
    }

    @Override
    public int getNumBytesPerVertex() {
        return VERTEX_DWORD_COUNT * 4;
    }

    public int getDwordsPerVertex() {
        return VERTEX_DWORD_COUNT;
    }

    public void setPosition(int vertexIndex, float x, float y, float z) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 0, x);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 1, y);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 2, z);
    }

    public void setPosition(int vertexIndex, Tuple3f p) {
        setPosition(vertexIndex, p.x, p.y, p.z);
    }

    public void getPosition(int vertexIndex, Tuple3f p) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        p.x = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 0);
        p.y = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 1);
        p.z = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 2);
    }

    public void setTexCoord(int vertexIndex, float x, float y) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 3, x);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 4, y);
    }

    public void setTexCoord(int vertexIndex, Tuple2f p) {
        setTexCoord(vertexIndex, p.x, p.y);
    }

    public void getTexCoord(int vertexIndex, Tuple2f p) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        p.x = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 3);
        p.y = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 4);
    }

    public void setNormal(int vertexIndex, float x, float y, float z) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 5, x);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 6, y);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 7, z);
    }

    public void setNormal(int vertexIndex, Tuple3f p) {
        setNormal(vertexIndex, p.x, p.y, p.z);
    }

    public void getNormal(int vertexIndex, Tuple3f p) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        p.x = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 5);
        p.y = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 6);
        p.z = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 7);
    }

    public void setTangent(int vertexIndex, float x, float y, float z, float w) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 8, x);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 9, y);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 10, z);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 11, w);
    }

    public void setTangent(int vertexIndex, Tuple4f p) {
        setTangent(vertexIndex, p.x, p.y, p.z, p.w);
    }

    public void getTangent(int vertexIndex, Tuple4f p) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        p.x = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 8);
        p.y = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 9);
        p.z = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 10);
        p.w = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 11);
    }

    public void computeTangentsWithTriangles(IndexData triIndexData) {
        if (triIndexData.getIndexCount() % 3 != 0)
            throw new RuntimeException("The given index data's number of indices is not divisible by 3");
        int triangleCount = triIndexData.getIndexCount() / 3;

        int vertexCount = getVertexCount();
        Vector3f[] ts = new Vector3f[vertexCount];
        Vector3f[] bs = new Vector3f[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            ts[i] = new Vector3f(0, 0, 0);
            bs[i] = new Vector3f(0, 0, 0);
        }

        Point3i tri = new Point3i();
        Point3f v1 = new Point3f();
        Point3f v2 = new Point3f();
        Point3f v3 = new Point3f();
        Vector2f w1 = new Vector2f();
        Vector2f w2 = new Vector2f();
        Vector2f w3 = new Vector2f();
        Vector3f sdir = new Vector3f();
        Vector3f tdir = new Vector3f();
        Vector3f n = new Vector3f();
        Vector3f t = new Vector3f();
        Vector3f b = new Vector3f();
        Vector3f v12 = new Vector3f();
        Vector3f v13 = new Vector3f();

        for (int triIndex = 0; triIndex < triangleCount; triIndex++) {
            tri.x = triIndexData.getIndex(3 * triIndex + 0);
            tri.y = triIndexData.getIndex(3 * triIndex + 1);
            tri.z = triIndexData.getIndex(3 * triIndex + 2);

            getPosition(tri.x, v1);
            getPosition(tri.y, v2);
            getPosition(tri.z, v3);

            getTexCoord(tri.x, w1);
            getTexCoord(tri.y, w2);
            getTexCoord(tri.z, w3);

            float x1 = v2.x - v1.x;
            float x2 = v3.x - v1.x;
            float y1 = v2.y - v1.y;
            float y2 = v3.y - v1.y;
            float z1 = v2.z - v1.z;
            float z2 = v3.z - v1.z;

            float s1 = w2.x - w1.x;
            float s2 = w3.x - w1.x;
            float t1 = w2.y - w1.y;
            float t2 = w3.y - w1.y;

            float det = s1 * t2 - s2 * t1;
            if (Math.abs(det) < 1e-9) {
                v12.sub(v2, v1);
                v13.sub(v3, v1);
                n.cross(v12, v13);
                VectorMathUtil.coordinateSystem(n, sdir, tdir);
            } else {
                float r = 1.0f / det;
                sdir.set((t2 * x1 - t1 * x2) * r,
                        (t2 * y1 - t1 * y2) * r,
                        (t2 * z1 - t1 * z2) * r);
                tdir.set((s1 * x2 - s2 * x1) * r,
                        (s1 * y2 - s2 * y1) * r,
                        (s1 * z2 - s2 * z1) * r);

            }

            ts[tri.x].add(sdir);
            ts[tri.y].add(sdir);
            ts[tri.z].add(sdir);

            bs[tri.x].add(tdir);
            bs[tri.y].add(tdir);
            bs[tri.z].add(tdir);
        }

        for (int i = 0; i < vertexCount; i++) {
            getNormal(i, n);
            t.set(ts[i]);

            tdir.scaleAdd(-n.dot(t), n, t);
            tdir.normalize();

            b.cross(n, tdir);
            float dot = b.dot(bs[i]);
            float w = (dot < 0) ? -1 : 1;

            setTangent(i, tdir.x, tdir.y, tdir.z, w);
        }
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        {
            JsonArray positions = new JsonArray();
            Point3f p = new Point3f();
            for (int i = 0; i < getVertexCount(); i++) {
                getPosition(i, p);
                positions.add(new JsonPrimitive(p.x));
                positions.add(new JsonPrimitive(p.y));
                positions.add(new JsonPrimitive(p.z));
            }
            json.add("positions", positions);
        }
        {
            JsonArray texCoords = new JsonArray();
            Point2f t = new Point2f();
            for (int i = 0; i < getVertexCount(); i++) {
                getTexCoord(i, t);
                texCoords.add(new JsonPrimitive(t.x));
                texCoords.add(new JsonPrimitive(t.y));
            }
            json.add("texCoords", texCoords);
        }
        {
            JsonArray normals = new JsonArray();
            Vector3f n = new Vector3f();
            for (int i = 0; i < getVertexCount(); i++) {
                getNormal(i, n);
                normals.add(new JsonPrimitive(n.x));
                normals.add(new JsonPrimitive(n.y));
                normals.add(new JsonPrimitive(n.z));
            }
            json.add("normals", normals);
        }
        {
            JsonArray tangents = new JsonArray();
            Vector4f t = new Vector4f();
            for (int i = 0; i < getVertexCount(); i++) {
                getTangent(i, t);
                tangents.add(new JsonPrimitive(t.x));
                tangents.add(new JsonPrimitive(t.y));
                tangents.add(new JsonPrimitive(t.z));
                tangents.add(new JsonPrimitive(t.w));
            }
            json.add("tangents", tangents);
        }
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        JsonArray positions = json.getAsJsonArray("positions");
        JsonArray texCoords = json.getAsJsonArray("texCoords");
        JsonArray normals = json.getAsJsonArray("normals");
        JsonArray tangents = json.getAsJsonArray("tangents");

        int vertexCount = positions.size() / 3;
        if (vertexCount != texCoords.size() / 2) {
            throw new RuntimeException("wrong number of texture coordinates");
        }
        if (vertexCount != normals.size() / 3) {
            throw new RuntimeException("wrong number of normals");
        }
        if (vertexCount != tangents.size() / 4) {
            throw new RuntimeException("wrong number of tangents");
        }

        Builder builder = startBuild();
        for (int i = 0; i < vertexCount; i++) {
            builder.addPosition(
                    positions.get(3 * i + 0).getAsFloat(),
                    positions.get(3 * i + 1).getAsFloat(),
                    positions.get(3 * i + 2).getAsFloat());
            builder.addTexCoord(
                    texCoords.get(2 * i + 0).getAsFloat(),
                    texCoords.get(2 * i + 1).getAsFloat());
            builder.addNormal(
                    normals.get(3 * i + 0).getAsFloat(),
                    normals.get(3 * i + 1).getAsFloat(),
                    normals.get(3 * i + 2).getAsFloat());
            builder.addTangent(
                    tangents.get(4 * i + 0).getAsFloat(),
                    tangents.get(4 * i + 1).getAsFloat(),
                    tangents.get(4 * i + 2).getAsFloat(),
                    tangents.get(4 * i + 3).getAsFloat());
        }
        builder.endBuild();
    }
}
