/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  Copyright (c) 2015, Department of Computer Science, Cornell University.
 *
 *  This code repository has been authored collectively by:
 *  Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

package cs5625.gfx.mesh;

import cs5625.gfx.gldata.IndexData;
import cs5625.gfx.gldata.VertexData;
import cs5625.gfx.json.NamedObject;
import cs5625.gfx.objcache.Holder;

public interface Mesh extends NamedObject {
    public Holder<VertexData> getVertexData();
    public Holder<IndexData> getIndexData();
    public int getPartCount();
    public MeshPart getPart(int index);
    public boolean castsShadow();
}
