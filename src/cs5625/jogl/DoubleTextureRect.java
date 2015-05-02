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

import javax.media.opengl.GL2;

public class DoubleTextureRect implements GLResource {
    private TextureRect[] buffers;
    private int readIndex = 0;

    public DoubleTextureRect(GL2 gl) {
        this(gl, GL2.GL_RGBA);
    }

    public DoubleTextureRect(GL2 gl, int internalFormat) {
        buffers = new TextureRect[2];
        for (int i = 0; i < 2; i++) {
            buffers[i] = new TextureRect(gl, internalFormat);
        }
    }

    public void allocate(int width, int height, int format, int type) {
        for (int i = 0; i < 2; i++) {
            buffers[i].allocate(width, height, format, type);
        }
    }

    public void allocate(int width, int height) {
    	allocate(width, height, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE);
    }

    	@Override
    public void disposeGL() {
        for (int i = 0; i < 2; i++) {
            buffers[i].disposeGL();
        }
    }

    public void swap() {
        readIndex = (readIndex + 1) % 2;
    }

    public TextureRect getReadBuffer() {
        return buffers[readIndex];
    }

    public TextureRect getWriteBuffer() {
        return buffers[(readIndex+1)%2];
    }

    public int getWidth() {
        return buffers[0].getWidth();
    }

    public int getHeight() {
        return buffers[0].getHeight();
    }
}
