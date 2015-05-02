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

import cs5625.jogl.DoubleTextureCubeMap;
import cs5625.jogl.DoubleTextureRect;
import cs5625.jogl.Fbo;
import cs5625.jogl.TextureRect;

import javax.media.opengl.GL2;

public class TextureCubeMapBufferCollection {
    private static int INTERNAL_FORMAT = GL2.GL_RGBA32F;
    public DoubleTextureCubeMap[] colorBuffers;
    public TextureRect depthBuffer;
    public boolean hasDepthBuffer;

    public TextureCubeMapBufferCollection(int colorBufferCount, boolean hasDepthBuffer) {
        colorBuffers = new DoubleTextureCubeMap[colorBufferCount];
        if (hasDepthBuffer)
            this.hasDepthBuffer = hasDepthBuffer;
    }

    public void allocate(GL2 gl, int size) {
        for (int i = 0; i < colorBuffers.length; i++) {
            if (colorBuffers[i] == null) {
                colorBuffers[i] = new DoubleTextureCubeMap(gl, INTERNAL_FORMAT);
            }
            if (colorBuffers[i].getSize() != size) {
                colorBuffers[i].allocate(size, GL2.GL_RGBA, GL2.GL_FLOAT);
            }
        }
        if (hasDepthBuffer) {
            if (depthBuffer == null) {
                depthBuffer = new TextureRect(gl, GL2.GL_DEPTH_COMPONENT32);
            }
            if (depthBuffer.getWidth() != size || depthBuffer.getHeight() != size) {
                depthBuffer.allocate(size, size, GL2.GL_DEPTH_COMPONENT, GL2.GL_UNSIGNED_INT);
            }
        }
    }

    public void attachTo(Fbo fbo, int side) {
        for (int i = 0; i < colorBuffers.length; i++) {
            fbo.attachColorBuffer(i, GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, colorBuffers[i].getWriteBuffer());
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
