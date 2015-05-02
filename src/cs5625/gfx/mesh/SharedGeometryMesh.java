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
import cs5625.gfx.gldata.IndexData;
import cs5625.gfx.gldata.VertexData;
import cs5625.gfx.json.JsonUtil;
import cs5625.gfx.material.Material;
import cs5625.gfx.objcache.Holder;

import javax.media.opengl.GL2;
import java.util.ArrayList;

public class SharedGeometryMesh extends AbstractMesh {
    private Holder<Mesh> sourceMesh;

    public class Builder {
        SharedGeometryMesh mesh;
        private Holder<Mesh> sourceMesh;
        private ArrayList<MeshPart> parts = new ArrayList<MeshPart>();
        private String name;

        public Builder(SharedGeometryMesh mesh) {
            this.mesh = mesh;
        }

        public Builder setMesh(Holder<Mesh> mesh) {
            sourceMesh = mesh;
            return this;
        }

        public Builder addPart(MeshPart meshPart) {
            parts.add(meshPart);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public SharedGeometryMesh endBuild() {
            mesh.sourceMesh = sourceMesh;
            mesh.parts.clear();
            mesh.parts.addAll(parts);
            mesh.name = name;
            return mesh;
        }
    }

    public Holder<Mesh> getSourceMesh() {
        return sourceMesh;
    }

    public SharedGeometryMesh setSourceMesh(Holder<Mesh> mesh) {
        this.sourceMesh = mesh;
        return this;
    }

    @Override
    public Holder<VertexData> getVertexData() {
        return sourceMesh.get().getVertexData();
    }

    @Override
    public Holder<IndexData> getIndexData() {
        return sourceMesh.get().getIndexData();
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.add("sourceMesh", sourceMesh.toJson(directory));
        JsonArray jsonParts = new JsonArray();
        for (int i = 0; i < parts.size(); i++) {
            JsonObject jsonPart = parts.get(i).toJson(directory);
            jsonParts.add(jsonPart);
        }
        json.add("parts", jsonParts);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        sourceMesh = (Holder<Mesh>) JsonUtil.fromJson(json.get("sourceMesh").getAsJsonObject(), directory);
        JsonArray jsonParts = json.get("parts").getAsJsonArray();
        parts.clear();
        for (int i = 0; i < jsonParts.size(); i++) {
            JsonObject jsonObj = jsonParts.get(i).getAsJsonObject();
            MeshPart meshPart = new MeshPart();
            meshPart.fromJson(jsonObj, directory);
            parts.add(meshPart);
        }
    }

    public SharedGeometryMesh addPart(MeshPart part) {
        this.parts.add(part);
        return this;
    }
}
