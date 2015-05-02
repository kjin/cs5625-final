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

import javax.media.opengl.GL2;
import javax.vecmath.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkeletalMeshVertexData extends VertexData {
    /**
     * Structure of a vertex record.
     * <p/>
     * 1) Position (3 DWORDS)
     * 2) Texture coordinates (2 DWORDS)
     * 3) Normal (3 DWORDS)
     * 4) Tangent (4 DWORDS)
     * 5) Bone indices (4 DWORDS)
     * 6) Bone weights (4 DWORDS)
     * 7) Start location to morph arrays. (1 DWORD)
     * 8) Number of morph a vertex is under influences. (1 DWORD)
     * 8) Filler (2 DWORDS)
     */
    private final static int VERTEX_DWORD_COUNT = 3 + 2 + 3 + 4 + 4 + 4 + 1 + 1 + 2;

    public static final AttributeSpec POSITION = new AttributeSpec("position", 3, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 0);
    public static final AttributeSpec TEXCOORD = new AttributeSpec("vert_texCoord", 2, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * 3);
    public static final AttributeSpec NORMAL = new AttributeSpec("normal", 3, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * (3 + 2));
    public static final AttributeSpec TANGENT = new AttributeSpec("tangent", 4, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * (3 + 2 + 3));
    public static final AttributeSpec BONE_INDICES = new AttributeSpec("boneIndices", 4, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * (3 + 2 + 3 + 4));
    public static final AttributeSpec BONE_WEIGHTS = new AttributeSpec("boneWeights", 4, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * (3 + 2 + 3 + 4 + 4));
    public static final AttributeSpec MORPH_START = new AttributeSpec("morphStart", 1, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * (3 + 2 + 3 + 4 + 4 + 4));
    public static final AttributeSpec MORPH_COUNT = new AttributeSpec("morphCount", 1, GL2.GL_FLOAT,
            false, 4 * VERTEX_DWORD_COUNT, 4 * (3 + 2 + 3 + 4 + 4 + 4 + 1));
    private static final Map<String, AttributeSpec> vertexAttributes = new HashMap<String, AttributeSpec>();

    private static void addAttributes(AttributeSpec attrib, String... names) {
        for (int i = 0; i < names.length; i++) {
            vertexAttributes.put(names[i], attrib);
        }
    }

    static {
        addAttributes(POSITION, "position", "vert_position", "vertex_position");
        addAttributes(TEXCOORD, "texcoord", "vert_texcoord", "vertex_texcoord");
        addAttributes(NORMAL, "normal", "vert_normal", "vertex_normal");
        addAttributes(TANGENT, "tangent", "vert_tangent", "vertex_tangent");
        addAttributes(BONE_INDICES, "boneindices", "vert_boneindices", "vertex_boneindices");
        addAttributes(BONE_WEIGHTS, "boneweights", "vert_boneweights", "vertex_boneweights");
        addAttributes(MORPH_START, "morphstart", "vert_morphstart", "vertex_morphstart");
        addAttributes(MORPH_COUNT, "morphcount", "vert_morphcount", "vertex_morphcount");
    }

    public static class Builder {
        SkeletalMeshVertexData data;
        public ArrayList<Point3f> positions = new ArrayList<Point3f>();
        public ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();
        public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
        public ArrayList<Vector4f> tangents = new ArrayList<Vector4f>();
        public ArrayList<Point4i> boneIndices = new ArrayList<Point4i>();
        public ArrayList<Vector4f> boneWeights = new ArrayList<Vector4f>();
        public ArrayList<Integer> morphStarts = new ArrayList<Integer>();
        public ArrayList<Integer> morphCounts = new ArrayList<Integer>();

        public Builder(SkeletalMeshVertexData data) {
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

        public Builder addBoneIndices(Tuple4i t) {
            return addBoneIndices(t.x, t.y, t.z, t.w);
        }

        public Builder addBoneIndices(int i0, int i1, int i2, int i3) {
            boneIndices.add(new Point4i(i0, i1, i2, i3));
            return this;
        }

        public Builder addBoneWeights(Tuple4f t) {
            addBoneWeights(t.x, t.y, t.z, t.w);
            return this;
        }

        public Builder addBoneWeights(float w0, float w1, float w2, float w3) {
            boneWeights.add(new Vector4f(w0, w1, w2, w3));
            return this;
        }

        public Builder addMorphStart(int i) {
            morphStarts.add(i);
            return this;
        }

        public Builder addMorphCount(int i) {
            morphCounts.add(i);
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

        public int getVertexCount() {
            return positions.size();
        }

        public SkeletalMeshVertexData endBuild() {
            int vertexCount = positions.size();
            if (!texCoords.isEmpty() && texCoords.size() != vertexCount) {
                throw new RuntimeException("invalid number of texture coordinates " +
                        "(either 0 or equal to number of vertices)");
            }
            if (!normals.isEmpty() && normals.size() != vertexCount) {
                throw new RuntimeException("invalid number of normals " +
                        "(either 0 or equal to number of vertices)");
            }
            if (!tangents.isEmpty() && tangents.size() != vertexCount) {
                throw new RuntimeException("invalid number of tangents " +
                        "(either 0 or equal to number of vertices)");
            }
            if (!boneIndices.isEmpty() && boneIndices.size() != vertexCount) {
                throw new RuntimeException("invalid number of bone index records " +
                        "(either 0 or equal to number of vertices)");
            }
            if (!boneWeights.isEmpty() && boneWeights.size() != vertexCount) {
                throw new RuntimeException("invalid number of bone weight records " +
                        "(either 0 or equal to number of vertices)");
            }
            if (!morphStarts.isEmpty() && morphStarts.size() != vertexCount) {
                throw new RuntimeException("invalid number of morph starts " +
                        "(either 0 or equal to number of vertices)");
            }
            if (!morphCounts.isEmpty() && morphCounts.size() != vertexCount) {
                throw new RuntimeException("invalid number of morph counts " +
                        "(either 0 or equal to number of vertices)");
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

                if (!boneIndices.isEmpty()) {
                    data.setBoneIndices(vertexIndex, boneIndices.get(vertexIndex));
                } else {
                    data.setBoneIndices(vertexIndex, -1, -1, -1, -1);
                }

                if (!boneWeights.isEmpty()) {
                    data.setBoneWeights(vertexIndex, boneWeights.get(vertexIndex));
                } else {
                    data.setBoneWeights(vertexIndex, 0, 0, 0, 0);
                }

                if (!morphStarts.isEmpty()) {
                    data.setMorphStartLocation(vertexIndex, morphStarts.get(vertexIndex));
                } else {
                    data.setMorphStartLocation(vertexIndex, -1);
                }

                if (!morphCounts.isEmpty()) {
                    data.setMorphCount(vertexIndex, morphCounts.get(vertexIndex));
                } else {
                    data.setMorphCount(vertexIndex, 0);
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
    public int getNumBytesPerVertex() {
        return VERTEX_DWORD_COUNT * 4;
    }

    public int getDwordsPerVertex() {
        return VERTEX_DWORD_COUNT;
    }

    @Override
    public AttributeSpec getAttributeSpec(String name) {
        return vertexAttributes.get(name.toLowerCase());
    }

    @Override
    public boolean hasAttribute(String name) {
        return vertexAttributes.containsKey(name.toLowerCase());
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


    public void setBoneIndices(int vertexIndex, int b0, int b1, int b2, int b3) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 12, b0);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 13, b1);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 14, b2);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 15, b3);
    }

    public void setBoneIndices(int vertexIndex, Tuple4i p) {
        setBoneIndices(vertexIndex, p.x, p.y, p.z, p.w);
    }

    public void getBoneIndices(int vertexIndex, Tuple4i p) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        p.x = (int) BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 12);
        p.y = (int) BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 13);
        p.z = (int) BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 14);
        p.w = (int) BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 15);
    }

    public void setBoneWeights(int vertexIndex, float x, float y, float z, float w) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 16, x);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 17, y);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 18, z);
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 19, w);
    }

    public void setBoneWeights(int vertexIndex, Tuple4f p) {
        setBoneWeights(vertexIndex, p.x, p.y, p.z, p.w);
    }

    public void getBoneWeights(int vertexIndex, Tuple4f p) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        p.x = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 16);
        p.y = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 17);
        p.z = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 18);
        p.w = BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 19);
    }

    public void setMorphStartLocation(int vertexIndex, int value) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 20, value);
    }

    public int getMorphStartLocation(int vertexIndex) {
        return (int) BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 20);
    }

    public void setMorphCount(int vertexIndex, int value) {
        int VERTEX_DWORD_COUNT = getDwordsPerVertex();
        BufferUtil.setLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 21, value);
    }

    public int getMorphCount(int vertexIndex) {
        return (int) BufferUtil.getLittleEndianFloat(buffer, vertexIndex * VERTEX_DWORD_COUNT + 21);
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
        {
            JsonArray boneIndices = new JsonArray();
            Point4i t = new Point4i();
            for (int i = 0; i < getVertexCount(); i++) {
                getBoneIndices(i, t);
                boneIndices.add(new JsonPrimitive(t.x));
                boneIndices.add(new JsonPrimitive(t.y));
                boneIndices.add(new JsonPrimitive(t.z));
                boneIndices.add(new JsonPrimitive(t.w));
            }
            json.add("boneIndices", boneIndices);
        }
        {
            JsonArray boneWeights = new JsonArray();
            Vector4f t = new Vector4f();
            for (int i = 0; i < getVertexCount(); i++) {
                getBoneWeights(i, t);
                boneWeights.add(new JsonPrimitive(t.x));
                boneWeights.add(new JsonPrimitive(t.y));
                boneWeights.add(new JsonPrimitive(t.z));
                boneWeights.add(new JsonPrimitive(t.w));
            }
            json.add("boneWeights", boneWeights);
        }
        {
            JsonArray morphStarts = new JsonArray();
            for (int i = 0; i < getVertexCount(); i++) {
                morphStarts.add(new JsonPrimitive(getMorphStartLocation(i)));
            }
            json.add("morphStarts", morphStarts);
        }
        {
            JsonArray morphCounts = new JsonArray();
            for (int i = 0; i < getVertexCount(); i++) {
                morphCounts.add(new JsonPrimitive(getMorphCount(i)));
            }
            json.add("morphCounts", morphCounts);
        }
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        JsonArray positions = json.getAsJsonArray("positions");
        JsonArray texCoords = json.getAsJsonArray("texCoords");
        JsonArray normals = json.getAsJsonArray("normals");
        JsonArray tangents = json.getAsJsonArray("tangents");
        JsonArray boneIndices = json.getAsJsonArray("boneIndices");
        JsonArray boneWeights = json.getAsJsonArray("boneWeights");
        JsonArray morphStarts = json.getAsJsonArray("morphStarts");
        JsonArray morphCounts = json.getAsJsonArray("morphCounts");

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
        if (vertexCount != boneIndices.size() / 4) {
            throw new RuntimeException("wrong number of bone indices");
        }
        if (vertexCount != boneWeights.size() / 4) {
            throw new RuntimeException("wrong number of bone weights");
        }
        if (vertexCount != morphStarts.size()) {
            throw new RuntimeException("wrong number of morph starts");
        }
        if (vertexCount != morphCounts.size()) {
            throw new RuntimeException("wrong number of morph counts");
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
            builder.addBoneIndices(
                    boneIndices.get(4 * i + 0).getAsInt(),
                    boneIndices.get(4 * i + 1).getAsInt(),
                    boneIndices.get(4 * i + 2).getAsInt(),
                    boneIndices.get(4 * i + 3).getAsInt());
            builder.addBoneWeights(
                    boneWeights.get(4 * i + 0).getAsFloat(),
                    boneWeights.get(4 * i + 1).getAsFloat(),
                    boneWeights.get(4 * i + 2).getAsFloat(),
                    boneWeights.get(4 * i + 3).getAsFloat());
            builder.addMorphStart(morphStarts.get(i).getAsInt());
            builder.addMorphCount(morphCounts.get(i).getAsInt());
        }
        builder.endBuild();
    }
}
