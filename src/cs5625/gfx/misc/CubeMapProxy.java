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

package cs5625.gfx.misc;

import com.google.gson.JsonObject;
import cs5625.gfx.json.AbstractNamedObject;

public class CubeMapProxy extends AbstractNamedObject {
    private int bufferSize = 512;
    private float farClip = 100;
    private int gaussianKernelSize = 15;
    private float gaussianKernelStdev = 3.0f;

    public int getGaussianKernelSize() {
        return gaussianKernelSize;
    }

    public CubeMapProxy setGaussianKernelSize(int gaussianKernelSize) {
        this.gaussianKernelSize = gaussianKernelSize;
        return this;
    }

    public float getGaussianKernelStdev() {
        return gaussianKernelStdev;
    }

    public CubeMapProxy setGaussianKernelStdev(float gaussianKernelStdev) {
        this.gaussianKernelStdev = gaussianKernelStdev;
        return this;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public CubeMapProxy setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public float getFarClip() {
        return farClip;
    }

    public CubeMapProxy setFarClip(float farClip) {
        this.farClip = farClip;
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.addProperty("bufferSize", bufferSize);
        json.addProperty("farClip", farClip);
        json.addProperty("gaussianKernelSize", gaussianKernelSize);
        json.addProperty("gaussianKernelStdev", gaussianKernelStdev);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        if (json.has("bufferSize"))
            bufferSize = json.get("bufferSize").getAsInt();
        if (json.has("farClip"))
            farClip = json.get("farClip").getAsFloat();
        if (json.has("gaussianKernelSize"))
            gaussianKernelSize = json.get("gaussianKernelSize").getAsInt();
        if (json.has("gaussianKernelStdev"))
            gaussianKernelStdev = json.get("gaussianKernelStdev").getAsFloat();
    }
}
