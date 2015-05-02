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

package cs5625.jogl;

import cs5625.jogl.DoubleTextureRect;
import cs5625.jogl.Fbo;
import cs5625.jogl.TextureRect;

import javax.media.opengl.GL2;

public class TextureRectBufferCollection {
    private static int INTERNAL_FORMAT = GL2.GL_RGBA32F;
    public DoubleTextureRect[] colorBuffers;
    public TextureRect depthBuffer;
    public boolean hasDepthBuffer;

    public TextureRectBufferCollection(int colorBufferCount, boolean hasDepthBuffer) {
        colorBuffers = new DoubleTextureRect[colorBufferCount];
        if (hasDepthBuffer)
            this.hasDepthBuffer = hasDepthBuffer;
    }

    public void allocate(GL2 gl, int width, int height, int format, int type) {
        for (int i = 0; i < colorBuffers.length; i++) {
            if (colorBuffers[i] == null) {
                colorBuffers[i] = new DoubleTextureRect(gl, INTERNAL_FORMAT);
            }
            if (colorBuffers[i].getWidth() != width || colorBuffers[i].getHeight() != height) {
                colorBuffers[i].allocate(width, height, format, type);
            }
        }
        if (hasDepthBuffer) {
            if (depthBuffer == null) {
                depthBuffer = new TextureRect(gl, GL2.GL_DEPTH_COMPONENT32);
            }
            if (depthBuffer.getWidth() != width || depthBuffer.getHeight() != height) {
                depthBuffer.allocate(width, height, GL2.GL_DEPTH_COMPONENT, GL2.GL_UNSIGNED_INT);
            }
        }
    }

    public void allocate(GL2 gl, int width, int height) {
    	allocate(gl, width, height, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE);
    }

    public void attachTo(Fbo fbo) {
        for (int i = 0; i < colorBuffers.length; i++) {
            fbo.attachColorBuffer(i, colorBuffers[i].getWriteBuffer());
        }
        if (hasDepthBuffer) {
            fbo.attachDepthBuffer(depthBuffer);
        }
    }

    public void swap() {
        for (int i = 0; i < colorBuffers.length; i++) {
            colorBuffers[i].swap();
        }
    }

    public void disposeGL() {
        for (int i = 0; i < colorBuffers.length; i++) {
            if (colorBuffers[i] != null)
                colorBuffers[i].disposeGL();
        }
        if (hasDepthBuffer && depthBuffer != null)
            depthBuffer.disposeGL();
    }
}
