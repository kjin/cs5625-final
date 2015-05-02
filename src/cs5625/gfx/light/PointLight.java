/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  Copyright (c) 2015, Department of Computer Science, Cornell University.
 *
 *  This code repository has been authored collectively by:
 *  Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

package cs5625.gfx.light;

import com.google.gson.JsonObject;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.json.JsonUtil;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class PointLight extends AbstractNamedObject {
    /* Attenuation defaults to quadratic for realism plus constant for non-blowing-up near the light. */
    private Point3f position = new Point3f(0, 0, 0);
    private Color3f color = new Color3f(1.0f, 1.0f, 1.0f);
    private float constantAttenuation = 1.0f;
    private float linearAttenuation = 0.0f;
    private float quadraticAttenuation = 1.0f;

    public Point3f getPosition() {
        return position;
    }

    public PointLight setPosition(Point3f position) {
        this.position.set(position);
        return this;
    }

    public PointLight setPosition(float x, float y, float z) {
        this.position.set(x,y,z);
        return this;
    }

    public Color3f getColor() {
        return color;
    }

    public PointLight setColor(float x, float y, float z) {
        this.color.set(x,y,z);
        return this;
    }

    public PointLight setColor(Color3f color) {
        this.color.set(color);
        return this;
    }

    public float getConstantAttenuation() {
        return constantAttenuation;
    }

    public PointLight setConstantAttenuation(float atten) {
        constantAttenuation = atten;
        return this;
    }

    public float getLinearAttenuation() {
        return linearAttenuation;
    }

    public PointLight setLinearAttenuation(float atten) {
        linearAttenuation = atten;
        return this;
    }

    public float getQuadraticAttenuation() {
        return quadraticAttenuation;
    }

    public PointLight setQuadraticAttenuation(float atten) {
        quadraticAttenuation = atten;
        return this;
    }

    public PointLight setAttenuation(float constant, float linear, float quadratic) {
        constantAttenuation = constant;
        linearAttenuation = linear;
        quadraticAttenuation = quadratic;
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.add("position", JsonUtil.toJson(position));
        json.add("color", JsonUtil.toJson(color));
        json.add("attenuation", JsonUtil.toJson(new Vector3f(
                constantAttenuation, linearAttenuation, quadraticAttenuation)));
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        JsonUtil.fromJson(json.get("position").getAsJsonArray(), position);
        JsonUtil.fromJson(json.get("color").getAsJsonArray(), color);
        Vector3f attenuation = new Vector3f();
        JsonUtil.fromJson(json.get("attenuation").getAsJsonArray(), attenuation);
        constantAttenuation = attenuation.x;
        linearAttenuation = attenuation.y;
        quadraticAttenuation = attenuation.z;
    }
}
