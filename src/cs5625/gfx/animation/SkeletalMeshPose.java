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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cs5625.gfx.json.AbstractJsonSerializable;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.Map;

public class SkeletalMeshPose extends AbstractJsonSerializable {
    public HashMap<String, BonePose> bonePoses = new HashMap<String, BonePose>();
    public HashMap<String, Float> morphWeights = new HashMap<String, Float>();
    
    public void clear() {
        for (BonePose bonePose : bonePoses.values()) {
            bonePose.displacement.set(0,0,0);
            bonePose.orientation.set(0,0,0,1);
        }
        for (String name : morphWeights.keySet()) {
            morphWeights.put(name, 0.0f);
        }
    }

    public void getBonePose(String boneName, Vector3f displacement, Quat4f orientation) {
        if (bonePoses.containsKey(boneName)) {
            BonePose bonePose = bonePoses.get(boneName);
            displacement.set(bonePose.displacement);
            orientation.set(bonePose.orientation);
        } else {
            displacement.set(0, 0, 0);
            orientation.set(0, 0, 0, 1);
        }
    }

    public float getMorphWeight(String morphName) {
        if (morphWeights.containsKey(morphName)) {
            return morphWeights.get(morphName);
        } else {
            return 0;
        }
    }

    public SkeletalMeshPose setBonePose(String boneName, Vector3f displacement, Quat4f orientation) {
        BonePose bonePose = null;
        if (bonePoses.containsKey(boneName)) {
            bonePose = bonePoses.get(boneName);
        } else {
            bonePose = new BonePose();
            bonePoses.put(boneName, bonePose);
        }
        bonePose.displacement.set(displacement);
        bonePose.orientation.set(orientation);
        return this;
    }

    public SkeletalMeshPose setMorphWeight(String morphName, float value) {
        morphWeights.put(morphName, value);
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        {
            JsonObject bonePosesObj = new JsonObject();
            for (Map.Entry<String, BonePose> entry : bonePoses.entrySet()) {
                JsonArray bonePoseObj = entry.getValue().toJson();
                bonePosesObj.add(entry.getKey(), bonePoseObj);
            }
            json.add("bonePoses", bonePosesObj);
        }
        {
            JsonObject morphPosesObj = new JsonObject();
            for (Map.Entry<String, Float> entry : morphWeights.entrySet()) {
                morphPosesObj.addProperty(entry.getKey(), entry.getValue());
            }
            json.add("morphWeights", morphPosesObj);
        }
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        {
            bonePoses.clear();
            JsonObject bonePosesObj = json.get("bonePoses").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : bonePosesObj.entrySet()) {
                JsonArray obj = entry.getValue().getAsJsonArray();
                BonePose bonePose = BonePose.createFromJson(obj);
                bonePoses.put(entry.getKey(), bonePose);
            }
        }
        {
            morphWeights.clear();
            JsonObject morphPosesObj = json.get("morphWeights").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : morphPosesObj.entrySet()) {
                float value = entry.getValue().getAsFloat();
                morphWeights.put(entry.getKey(), value);
            }
        }
    }
}
