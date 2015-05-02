/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  Copyright (c) 2015, Department of Computer Science, Cornell University.
 *
 *  This code repository has been authored collectively by:
 *  Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

package cs5625.gfx.mesh;

import cs5625.gfx.glcache.GLResourceCache;
import cs5625.gfx.gldata.IndexData;
import cs5625.gfx.gldata.VertexData;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.objcache.ObjectCache;
import cs5625.jogl.Vbo;

import java.util.ArrayList;

public abstract class AbstractMesh extends AbstractNamedObject implements Mesh{
    protected ArrayList<MeshPart> parts = new ArrayList<MeshPart>();
    protected boolean doesCastShadow = true;

    @Override
    public boolean castsShadow() {
        return doesCastShadow;
    }

    @Override
    public MeshPart getPart(int index) {
        return parts.get(index);
    }

    @Override
    public int getPartCount() {
        return parts.size();
    }
}
