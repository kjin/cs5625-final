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
import cs5625.gfx.objcache.Holder;

public class UnstructuredMesh extends AbstractMesh {
    private Holder<VertexData> vertexData = null;
    private Holder<IndexData> indexData = null;

    public UnstructuredMesh() {
        // NOP
    }

    public UnstructuredMesh setCastsShadow(boolean castsShadow) {
        this.doesCastShadow = castsShadow;
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.add("vertexData", vertexData.toJson(directory));
        json.add("indexData", indexData.toJson(directory));

        JsonArray jsonParts = new JsonArray();
        for (int i = 0; i < parts.size(); i++) {
            JsonObject jsonPart = parts.get(i).toJson(directory);
            jsonParts.add(jsonPart);
        }
        json.add("parts", jsonParts);

        json.addProperty("castsShadow", doesCastShadow);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        vertexData = (Holder<VertexData>) JsonUtil.fromJson(json.get("vertexData"), directory);
        indexData = (Holder<IndexData>) JsonUtil.fromJson(json.get("indexData"), directory);
        JsonArray jsonParts = json.get("parts").getAsJsonArray();
        parts.clear();
        for (int i = 0; i < jsonParts.size(); i++) {
            JsonObject jsonObj = jsonParts.get(i).getAsJsonObject();
            MeshPart meshPart = new MeshPart();
            meshPart.fromJson(jsonObj, directory);
            parts.add(meshPart);
        }
        if (json.has("castsShadow"))
            doesCastShadow = json.get("castsShadow").getAsBoolean();
    }

    @Override
    public Holder<VertexData> getVertexData() {
        return vertexData;
    }

    @Override
    public Holder<IndexData> getIndexData() {
        return indexData;
    }

    public UnstructuredMesh addPart(MeshPart part) {
        this.parts.add(part);
        return this;
    }

    public UnstructuredMesh setVertexData(Holder<VertexData> vertexData) {
        this.vertexData = vertexData;
        return this;
    }

    public UnstructuredMesh setIndexData(Holder<IndexData> indexData) {
        this.indexData = indexData;
        return this;
    }
}
