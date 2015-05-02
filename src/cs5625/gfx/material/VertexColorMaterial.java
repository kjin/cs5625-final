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

package cs5625.gfx.material;

import com.google.gson.JsonObject;
import cs5625.gfx.json.AbstractJsonSerializable;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.json.JsonUtil;

public class VertexColorMaterial extends AbstractMaterial {
    @Override
    protected void fillJson(JsonObject json, String directory) {
        super.fillJson(json, directory);
    }
}
