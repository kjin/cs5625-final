/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  Copyright (c) 2015, Department of Computer Science, Cornell University.
 *
 *  This code repository has been authored collectively by:
 *  Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

package cs5625.gfx.animation;

import com.google.gson.JsonArray;
import cs5625.gfx.json.JsonUtil;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class BonePose {
    public Vector3f displacement = new Vector3f();
    public Quat4f orientation = new Quat4f();

    public JsonArray toJson() {
        JsonArray result = new JsonArray();
        result.add(JsonUtil.toJson(displacement));
        result.add(JsonUtil.toJson(orientation));
        return result;
    }

    public void fromJson(JsonArray array) {
        JsonUtil.fromJson(array.get(0).getAsJsonArray(), displacement);
        JsonUtil.fromJson(array.get(1).getAsJsonArray(), orientation);
    }

    public static BonePose createFromJson(JsonArray array) {
        BonePose bonePose = new BonePose();
        bonePose.fromJson(array);
        return bonePose;
    }
}
