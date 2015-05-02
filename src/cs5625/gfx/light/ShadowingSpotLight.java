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

package cs5625.gfx.light;

import com.google.gson.JsonObject;
import cs5625.gfx.camera.PerspectiveCamera;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.json.JsonUtil;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public class ShadowingSpotLight extends AbstractNamedObject {
    public static final int NO_SHADOWS = 1;
    public static final int SIMPLE_SHADOW_MAP = 1;
    public static final int PERCENTAGE_CLOSER_FILTERING = 2;
    public static final int PERCENTAGE_CLOSER_SOFT_SHADOW = 3;

    private Color3f color = new Color3f(1.0f, 1.0f, 1.0f);
    PerspectiveCamera camera = new PerspectiveCamera(0.1f, 100.0f, 45.0f);
    private float constantAttenuation = 1.0f;
    private float linearAttenuation = 0.0f;
    private float quadraticAttenuation = .0f;
    private int shadowMapResolution = 512;
    private float shadowMapConstantBias = 0.1f;
    private float shadowMapBiasScale = 0.0f;
    private int shadowMapMode = SIMPLE_SHADOW_MAP;
    private float lightWidth = 0;

    public Color3f getColor() {
        return color;
    }

    public ShadowingSpotLight setColor(Tuple3f color) {
        this.color.set(color);
        return this;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Point3f getPosition() {
        return camera.eye;
    }

    public ShadowingSpotLight setPosition(Tuple3f eye) {
        camera.eye.set(eye);
        camera.updateFrame();
        return this;
    }

    public Point3f getTarget() {
        return camera.target;
    }

    public ShadowingSpotLight setTarget(Tuple3f target) {
        camera.target.set(target);
        camera.updateFrame();
        return this;
    }

    public float getFov() {
        return camera.fovy;
    }

    public ShadowingSpotLight setFov(float fov) {
        this.camera.fovy = fov;
        return this;
    }

    public float getNear() {
        return this.camera.near;
    }

    public ShadowingSpotLight setNear(float near) {
        this.camera.near = near;
        return this;
    }

    public float getFar() {
        return this.camera.far;
    }

    public ShadowingSpotLight setFar(float far) {
        this.camera.far = far;
        return this;
    }

    public float getConstantAttenuation() {
        return constantAttenuation;
    }

    public ShadowingSpotLight setConstantAttenuation(float constantAttenuation) {
        this.constantAttenuation = constantAttenuation;
        return this;
    }

    public float getLinearAttenuation() {
        return linearAttenuation;
    }

    public ShadowingSpotLight setLinearAttenuation(float linearAttenuation) {
        this.linearAttenuation = linearAttenuation;
        return this;
    }

    public float getQuadraticAttenuation() {
        return quadraticAttenuation;
    }

    public ShadowingSpotLight setQuadraticAttenuation(float quadraticAttenuation) {
        this.quadraticAttenuation = quadraticAttenuation;
        return this;
    }

    public int getShadowMapResolution() {
        return shadowMapResolution;
    }

    public ShadowingSpotLight setShadowMapResolution(int shadowMapResolution) {
        this.shadowMapResolution = shadowMapResolution;
        return this;
    }

    public float getShadowMapConstantBias() {
        return shadowMapConstantBias;
    }

    public ShadowingSpotLight setShadowMapConstantBias(float shadowMapConstantBias) {
        this.shadowMapConstantBias = shadowMapConstantBias;
        return this;
    }

    public float getShadowMapBiasScale() { return shadowMapBiasScale; }

    public ShadowingSpotLight setShadowMapBiasScale(float shadowMapBiasScale) {
        this.shadowMapBiasScale = shadowMapBiasScale;
        return this;
    }

    public int getShadowMapMode() {
        return shadowMapMode;
    }

    public ShadowingSpotLight setShadowMapMode(int value) {
        this.shadowMapMode = value;
        return this;
    }

    public float getLightWidth() {
        return lightWidth;
    }

    public ShadowingSpotLight setLightWidth(float value) {
        this.lightWidth = value;
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.addProperty("fov", getFov());
        json.addProperty("near", getNear());
        json.addProperty("far", getFar());
        json.add("position", JsonUtil.toJson(getPosition()));
        json.add("target", JsonUtil.toJson(getTarget()));
        json.add("color", JsonUtil.toJson(color));
        json.add("attenuation", JsonUtil.toJson(new Vector3f(
                constantAttenuation, linearAttenuation, quadraticAttenuation)));
        json.addProperty("shadowMapResolution", shadowMapResolution);
        json.addProperty("shadowMapConstantBias", shadowMapConstantBias);
        json.addProperty("shadowMapBiasScale", shadowMapBiasScale);
        json.addProperty("shadowMapMode", shadowMapMode);
        json.addProperty("lightWidth", lightWidth);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        setFov(json.get("fov").getAsFloat());
        setNear(json.get("near").getAsFloat());
        setFar(json.get("far").getAsFloat());
        JsonUtil.fromJson(json.get("position").getAsJsonArray(), camera.eye);
        JsonUtil.fromJson(json.get("target").getAsJsonArray(), camera.target);
        camera.updateFrame();
        JsonUtil.fromJson(json.get("color").getAsJsonArray(), color);
        Vector3f attenuation = new Vector3f();
        JsonUtil.fromJson(json.get("attenuation").getAsJsonArray(), attenuation);
        constantAttenuation = attenuation.x;
        linearAttenuation = attenuation.y;
        quadraticAttenuation = attenuation.z;
        shadowMapResolution = json.get("shadowMapResolution").getAsInt();
        shadowMapConstantBias = json.get("shadowMapConstantBias").getAsFloat();
        shadowMapBiasScale = json.get("shadowMapBiasScale").getAsFloat();
        shadowMapMode = json.get("shadowMapMode").getAsInt();
        lightWidth = json.get("lightWidth").getAsFloat();
    }
}
