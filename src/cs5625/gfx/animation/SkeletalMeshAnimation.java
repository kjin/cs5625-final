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
import com.google.gson.JsonPrimitive;
import cs5625.gfx.json.AbstractNamedObject;

import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import java.util.*;

public class SkeletalMeshAnimation extends AbstractNamedObject {
    public float maxTime;
    public float minTime;
    public HashMap<String, TreeMap<Float, BonePose>> boneAnimations =
            new HashMap<String, TreeMap<Float, BonePose>>();
    public HashMap<String, TreeMap<Float, Float>> morphAnimations =
            new HashMap<String, TreeMap<Float, Float>>();

    public float getMinTime() {
        return minTime;
    }

    public float getMaxTime() {
        return maxTime;
    }

    public SkeletalMeshAnimation addBoneFrame(String boneName, float time, Tuple3f displacement, Quat4f orientation) {
        if (!boneAnimations.containsKey(boneName)) {
            boneAnimations.put(boneName, new TreeMap<Float, BonePose>());
        }
        TreeMap<Float, BonePose> boneAnim = boneAnimations.get(boneName);
        BonePose bonePose = new BonePose();
        bonePose.displacement.set(displacement);
        bonePose.orientation.set(orientation);
        boneAnim.put(time, bonePose);
        return this;
    }

    public SkeletalMeshAnimation addMorphFrame(String morphName, float time, float weight) {
        if (!morphAnimations.containsKey(morphName)) {
            morphAnimations.put(morphName, new TreeMap<Float, Float>());
        }
        TreeMap<Float, Float> morphAnim = morphAnimations.get(morphName);
        morphAnim.put(time, weight);
        return this;
    }

    public void cleanUp() {
        ArrayList<String> boneAnimToRemove = new ArrayList<String>();
        for (String boneName : boneAnimations.keySet()) {
            TreeMap<Float, BonePose> anim = boneAnimations.get(boneName);
            boolean allIdentity = true;
            for (BonePose bonePose : anim.values()) {
                if (bonePose.displacement.x != 0 || bonePose.displacement.y != 0 || bonePose.displacement.z != 0) {
                    allIdentity = false;
                    break;
                }
                if (bonePose.orientation.x != 0 || bonePose.orientation.y != 0 || bonePose.orientation.z != 0 ||
                        bonePose.orientation.w != 1) {
                    allIdentity = false;
                    break;
                }
            }
            if (allIdentity)
                boneAnimToRemove.add(boneName);
        }
        for(String boneName : boneAnimToRemove) {
            boneAnimations.remove(boneName);
        }

        ArrayList<String> morphAnimToRemove = new ArrayList<String>();
        for (String morphName : morphAnimations.keySet()) {
            TreeMap<Float, Float> anim = morphAnimations.get(morphName);
            boolean allIdentity = true;
            for (Float weight : anim.values()) {
                if (weight != 0) {
                    allIdentity = false;
                    break;
                }
            }
            if (allIdentity) {
                morphAnimToRemove.add(morphName);
            }
        }
        for(String morphName : morphAnimToRemove) {
            morphAnimations.remove(morphName);
        }
    }

    public SkeletalMeshAnimation updateMinMaxTime() {
        maxTime = 0;
        minTime = Float.MAX_VALUE;
        for (TreeMap<Float, BonePose> boneAnim : boneAnimations.values()) {
            maxTime = Math.max(maxTime, boneAnim.lastKey());
            minTime = Math.min(minTime, boneAnim.firstKey());
        }
        for (TreeMap<Float, Float> morphAnim : morphAnimations.values()) {
            maxTime = Math.max(maxTime, morphAnim.lastKey());
            minTime = Math.min(minTime, morphAnim.firstKey());
        }
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        {
            JsonObject boneAnimObj = new JsonObject();
            for (Map.Entry<String, TreeMap<Float, BonePose>> entry : boneAnimations.entrySet()) {
                JsonArray animObj = new JsonArray();
                for (Map.Entry<Float, BonePose> frame : entry.getValue().entrySet()) {
                    JsonArray frameObj = new JsonArray();
                    frameObj.add(new JsonPrimitive(frame.getKey()));
                    frameObj.add(frame.getValue().toJson());
                    animObj.add(frameObj);
                }
                boneAnimObj.add(entry.getKey(), animObj);
            }
            json.add("boneAnimations", boneAnimObj);
        }
        {
            JsonObject morphAnimObj = new JsonObject();
            for (Map.Entry<String, TreeMap<Float, Float>> entry : morphAnimations.entrySet()) {
                JsonArray animObj = new JsonArray();
                for (Map.Entry<Float, Float> frame : entry.getValue().entrySet()) {
                    JsonArray frameObj = new JsonArray();
                    frameObj.add(new JsonPrimitive(frame.getKey()));
                    frameObj.add(new JsonPrimitive(frame.getValue()));
                    animObj.add(frameObj);
                }
                morphAnimObj.add(entry.getKey(), animObj);
            }
            json.add("morphAnimations", morphAnimObj);
        }
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        {
            boneAnimations.clear();
            JsonObject boneAnimObj = json.get("boneAnimations").getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : boneAnimObj.entrySet()) {
                String name = entry.getKey();
                JsonArray animObj = entry.getValue().getAsJsonArray();
                TreeMap<Float, BonePose> anim = new TreeMap<Float, BonePose>();
                for (int i = 0; i < animObj.size(); i++) {
                    JsonArray frameArray = animObj.get(i).getAsJsonArray();
                    float time = frameArray.get(0).getAsFloat();
                    BonePose bonePose = BonePose.createFromJson(frameArray.get(1).getAsJsonArray());
                    anim.put(time, bonePose);
                }
                boneAnimations.put(name, anim);
            }
        }
        {
            morphAnimations.clear();
            JsonObject morphAnimObj = json.get("morphAnimations").getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : morphAnimObj.entrySet()) {
                String name = entry.getKey();
                JsonArray animObj = entry.getValue().getAsJsonArray();
                TreeMap<Float, Float> anim = new TreeMap<Float, Float>();
                for (int i = 0; i < animObj.size(); i++) {
                    JsonArray frameArray = animObj.get(i).getAsJsonArray();
                    float time = frameArray.get(0).getAsFloat();
                    float value = frameArray.get(1).getAsFloat();
                    anim.put(time, value);
                }
                morphAnimations.put(name, anim);
            }
        }
        updateMinMaxTime();
    }

    public void getPose(float time, SkeletalMeshPose pose) {
        pose.clear();
        Vector3f displacement = new Vector3f();
        Quat4f orientation = new Quat4f();
        for (Map.Entry<String, TreeMap<Float, BonePose>> entry : boneAnimations.entrySet()) {
            String boneName = entry.getKey();
            TreeMap<Float, BonePose> anim = entry.getValue();
            if (!anim.isEmpty()) {
                float t = time;
                if (t < anim.firstKey()) t = anim.firstKey();
                if (t > anim.lastKey()) t = anim.lastKey();

                Float t0 = anim.floorKey(t);
                Float t1 = anim.higherKey(t);
                if (t1 == null) {
                    BonePose value = anim.get(t0);
                    pose.setBonePose(boneName, value.displacement, value.orientation);
                } else {
                    float alpha = (t - t0) / (t1 - t0);
                    BonePose v0 = anim.get(t0);
                    BonePose v1 = anim.get(t1);

                    displacement.scale(1-alpha, v0.displacement);
                    displacement.scaleAdd(alpha, v1.displacement, displacement);

                    orientation.interpolate(v0.orientation, v1.orientation, alpha);

                    pose.setBonePose(boneName, displacement, orientation);
                }
            }
        }
        for (Map.Entry<String, TreeMap<Float, Float>> entry : morphAnimations.entrySet()) {
            String morphName = entry.getKey();
            TreeMap<Float, Float> anim = entry.getValue();
            if (!anim.isEmpty()) {
                float t = time;
                if (t < anim.firstKey()) t = anim.firstKey();
                if (t > anim.lastKey()) t = anim.lastKey();

                Float t0 = anim.floorKey(t);
                Float t1 = anim.higherKey(t);
                if (t1 == null) {
                    Float value = anim.get(t0);
                    pose.setMorphWeight(morphName, value);
                } else {
                    float alpha = (t - t0) / (t1 - t0);
                    float v0 = anim.get(t0);
                    float v1 = anim.get(t1);
                    //System.out.println("morphName = " + morphName);
                    //System.out.println("t0 = " + t0 + ";v0 = " + v0);
                    //System.out.println("t1 = " + t1 + ";v1 = " + v1);
                    float v = (1-alpha)*v0 + alpha*v1;
                    pose.setMorphWeight(morphName, v);
                }
            }
        }
    }
}
