/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  Copyright (c) 2015, Department of Computer Science, Cornell University.
 *
 *  This code repository has been authored collectively by:
 *  Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

package cs5625.gfx.mesh;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import cs5625.gfx.gldata.IndexData;
import cs5625.gfx.gldata.PosTexNorTanData;
import cs5625.gfx.gldata.VertexData;
import cs5625.gfx.material.Material;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.objcache.Holder;
import cs5625.util.VectorMathUtil;

import javax.media.opengl.GL2;
import javax.vecmath.*;
import java.util.ArrayList;

public class TriMesh extends AbstractMesh {
    private Value<VertexData> vertexData = new Value<VertexData>(new PosTexNorTanData());
    private Value<IndexData> indexData = new Value<IndexData>(new IndexData());
    private boolean hasTexCoords = false;
    private boolean hasNormals = false;
    private boolean hasTangents = false;

    public TriMesh setCastsShadow(boolean castsShadow) {
        this.doesCastShadow = castsShadow;
        return this;
    }

    public class Builder {
        TriMesh mesh;
        PosTexNorTanData.Builder vertexBuilder;
        IndexData.Builder indexBuilder;
        private ArrayList<MeshPart> parts = new ArrayList<MeshPart>();
        int vertexStart = 0;
        String name;

        public Builder(TriMesh mesh) {
            this.mesh = mesh;
            PosTexNorTanData vertexData = (PosTexNorTanData) mesh.vertexData.get();
            vertexBuilder = vertexData.startBuild();
            indexBuilder = mesh.indexData.get().startBuild();
            mesh.parts.clear();
        }

        public Builder addPosition(float x, float y, float z) {
            vertexBuilder.addPosition(new Point3f(x, y, z));
            return this;
        }

        public Builder addPosition(Tuple3f p) {
            vertexBuilder.addPosition(p.x, p.y, p.z);
            return this;
        }

        public Builder addNormal(float x, float y, float z) {
            vertexBuilder.addNormal(new Vector3f(x, y, z));
            return this;
        }

        public Builder addNormal(Tuple3f n) {
            vertexBuilder.addNormal(n.x, n.y, n.z);
            return this;
        }

        public Builder addTangent(float x, float y, float z, float w) {
            vertexBuilder.addTangent(new Vector4f(x, y, z, w));
            return this;
        }

        public Builder addTangent(Tuple4f t) {
            vertexBuilder.addTangent(t.x, t.y, t.z, t.w);
            return this;
        }

        public Builder addTexCoord(float x, float y) {
            vertexBuilder.addTexCoord(new Vector2f(x, y));
            return this;
        }

        public Builder addTexCoord(Tuple2f t) {
            vertexBuilder.addTexCoord(t.x, t.y);
            return this;
        }

        public Builder addTriangle(int v0, int v1, int v2) {
            indexBuilder.add(v0).add(v1).add(v2);
            return this;
        }

        public Builder addPart(Holder<Material> material, int triangleCount) {
            MeshPart meshPart = new MeshPart();
            meshPart.vertexStart = vertexStart;
            meshPart.vertexCount = 3 * triangleCount;
            meshPart.primitive = GL2.GL_TRIANGLES;
            meshPart.material = material;
            vertexStart += 3 * triangleCount;
            parts.add(meshPart);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public TriMesh endBuild() {
            mesh.hasNormals = vertexBuilder.hasNormal();
            mesh.hasTangents = vertexBuilder.hasTangents();
            mesh.hasTexCoords = vertexBuilder.hasTexCoords();

            vertexBuilder.endBuild();
            indexBuilder.endBuild();
            mesh.parts.clear();
            mesh.parts.addAll(parts);

            if (!hasNormals) {
                mesh.computeNormals();
            }
            if (!hasTangents) {
                mesh.computeTangents();
            }

            mesh.name = name;

            return mesh;
        }
    }

    public Builder startBuild() {
        return new Builder(this);
    }

    public int getTriangleCount() {
        return indexData.get().getIndexCount() / 3;
    }

    @Override
    public Holder<VertexData> getVertexData() {
        return vertexData;
    }

    @Override
    public Holder<IndexData> getIndexData() {
        return indexData;
    }

    public boolean getHasTexCoords() {
        return hasTexCoords;
    }

    public boolean getHasNormals() {
        return hasNormals;
    }

    public boolean getHasTangents() {
        return hasTangents;
    }

    private void computeNormals() {
        PosTexNorTanData vertices = (PosTexNorTanData) vertexData.get();
        IndexData indices = indexData.get();
        int vertexCount = vertices.getVertexCount();
        for (int i = 0; i < vertexCount; i++) {
            vertices.setNormal(i, 0, 0, 0);
        }

        Vector3f p01 = new Vector3f();
        Vector3f p02 = new Vector3f();
        Vector3f n = new Vector3f();
        Point3f p0 = new Point3f();
        Point3f p1 = new Point3f();
        Point3f p2 = new Point3f();
        Vector3f nn = new Vector3f();
        int[] v = new int[3];

        for (int i = 0; i < getTriangleCount(); i++) {
            for (int j = 0; j < 3; j++) {
                v[j] = indices.getIndex(3 * i + j);
            }

            vertices.getPosition(v[0], p0);
            vertices.getPosition(v[1], p1);
            vertices.getPosition(v[2], p2);

            p01.sub(p1, p0);
            p02.sub(p2, p0);
            n.cross(p01, p02);
            n.normalize();

            for (int j = 0; j < 3; j++) {
                vertices.getNormal(v[j], nn);
                nn.add(n);
                vertices.setNormal(v[j], nn);
            }
        }
        for (int i = 0; i < vertexCount; i++) {
            vertices.getNormal(i, nn);
            nn.normalize();
            vertices.setNormal(i, nn);
        }
        vertexData.get().bumpVersion();
    }

    private void computeTangents() {
        if (!hasTexCoords) {
            return;
        }
        PosTexNorTanData vertices = (PosTexNorTanData) vertexData.get();
        IndexData indices = indexData.get();
        int vertexCount = vertices.getVertexCount();
        int triangleCount = getTriangleCount();

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
            tri.x = indices.getIndex(3 * triIndex + 0);
            tri.y = indices.getIndex(3 * triIndex + 1);
            tri.z = indices.getIndex(3 * triIndex + 2);

            vertices.getPosition(tri.x, v1);
            vertices.getPosition(tri.y, v2);
            vertices.getPosition(tri.z, v3);

            vertices.getTexCoord(tri.x, w1);
            vertices.getTexCoord(tri.y, w2);
            vertices.getTexCoord(tri.z, w3);

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
            vertices.getNormal(i, n);
            t.set(ts[i]);

            tdir.scaleAdd(-n.dot(t), n, t);
            tdir.normalize();

            b.cross(n, tdir);
            float dot = b.dot(bs[i]);
            float w = (dot < 0) ? -1 : 1;

            vertices.setTangent(i, tdir.x, tdir.y, tdir.z, w);
        }

        vertexData.get().bumpVersion();
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        PosTexNorTanData vertices = (PosTexNorTanData) vertexData.get();
        IndexData indices = indexData.get();

        {
            JsonArray positions = new JsonArray();
            Point3f p = new Point3f();
            for (int i = 0; i < vertices.getVertexCount(); i++) {
                vertices.getPosition(i, p);
                positions.add(new JsonPrimitive(p.x));
                positions.add(new JsonPrimitive(p.y));
                positions.add(new JsonPrimitive(p.z));
            }
            json.add("positions", positions);
        }
        if (hasTexCoords) {
            JsonArray texCoords = new JsonArray();
            Point2f t = new Point2f();
            for (int i = 0; i < vertices.getVertexCount(); i++) {
                vertices.getTexCoord(i, t);
                texCoords.add(new JsonPrimitive(t.x));
                texCoords.add(new JsonPrimitive(t.y));
            }
            json.add("texCoords", texCoords);
        }
        if (hasNormals) {
            JsonArray normals = new JsonArray();
            Vector3f n = new Vector3f();
            for (int i = 0; i < vertices.getVertexCount(); i++) {
                vertices.getNormal(i, n);
                normals.add(new JsonPrimitive(n.x));
                normals.add(new JsonPrimitive(n.y));
                normals.add(new JsonPrimitive(n.z));
            }
            json.add("normals", normals);
        }
        if (hasTangents) {
            JsonArray tangents = new JsonArray();
            Vector4f t = new Vector4f();
            for (int i = 0; i < vertices.getVertexCount(); i++) {
                vertices.getTangent(i, t);
                tangents.add(new JsonPrimitive(t.x));
                tangents.add(new JsonPrimitive(t.y));
                tangents.add(new JsonPrimitive(t.z));
                tangents.add(new JsonPrimitive(t.w));
            }
            json.add("tangents", tangents);
        }
        {
            JsonArray indexArray = new JsonArray();
            Point3f p = new Point3f();
            for (int i = 0; i < indices.getIndexCount(); i++) {
                int index = indices.getIndex(i);
                indexArray.add(new JsonPrimitive(index));
            }
            json.add("indices", indexArray);
        }
        {
            JsonArray jsonParts = new JsonArray();
            for (int i = 0; i < parts.size(); i++) {
                JsonObject jsonPart = parts.get(i).toJson(directory);
                jsonParts.add(jsonPart);
            }
            json.add("parts", jsonParts);
        }
        json.addProperty("castsShadow", doesCastShadow);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);

        JsonArray positions = json.getAsJsonArray("positions");
        JsonArray texCoords = json.has("texCoords") ? json.getAsJsonArray("texCoords") : null;
        JsonArray normals = json.has("normals") ? json.getAsJsonArray("normals") : null;
        JsonArray tangents = json.has("tangents") ? json.getAsJsonArray("tangents") : null;
        JsonArray indices = json.getAsJsonArray("indices");
        JsonArray jsonParts = json.get("parts").getAsJsonArray();

        int vertexCount = positions.size() / 3;
        if (texCoords != null && texCoords.size() / 2 != vertexCount) {
            throw new RuntimeException("incorrect number of texCoords");
        }
        if (normals != null && normals.size() / 3 != vertexCount) {
            throw new RuntimeException("incorrect number of normals");
        }
        if (tangents != null && tangents.size() / 4 != vertexCount) {
            throw new RuntimeException("incorrect number of tangents");
        }
        if (indices.size() % 3 != 0) {
            throw new RuntimeException("number of indices not divisible by 3");
        }

        Builder builder = startBuild();

        for (int i = 0; i < vertexCount; i++) {
            builder.addPosition(
                    positions.get(3 * i + 0).getAsFloat(),
                    positions.get(3 * i + 1).getAsFloat(),
                    positions.get(3 * i + 2).getAsFloat());
            if (texCoords != null) {
                builder.addTexCoord(
                        texCoords.get(2 * i + 0).getAsFloat(),
                        texCoords.get(2 * i + 1).getAsFloat());
            }
            if (normals != null) {
                builder.addNormal(
                        normals.get(3 * i + 0).getAsFloat(),
                        normals.get(3 * i + 1).getAsFloat(),
                        normals.get(3 * i + 2).getAsFloat());
            }
            if (tangents != null) {
                builder.addTangent(
                        tangents.get(4 * i + 0).getAsFloat(),
                        tangents.get(4 * i + 1).getAsFloat(),
                        tangents.get(4 * i + 2).getAsFloat(),
                        tangents.get(4 * i + 3).getAsFloat());
            }
        }
        for (int i = 0; i < indices.size() /3; i++) {
            builder.addTriangle(
                    indices.get(3*i+0).getAsInt(),
                    indices.get(3*i+1).getAsInt(),
                    indices.get(3*i+2).getAsInt());
        }
        for (int i = 0; i < jsonParts.size(); i++) {
            JsonObject jsonObj = jsonParts.get(i).getAsJsonObject();
            MeshPart meshPart = new MeshPart();
            meshPart.fromJson(jsonObj, directory);
            builder.addPart(meshPart.material, meshPart.vertexCount / 3);
        }

        builder.endBuild();

        if (json.has("castsShadow"))
            doesCastShadow = json.get("castsShadow").getAsBoolean();
    }

    public void addPart(MeshPart part) {
        this.parts.add(part);
    }
}
