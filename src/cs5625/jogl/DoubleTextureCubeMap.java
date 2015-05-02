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

public class DoubleTextureCubeMap implements GLResource {
    private TextureCubeMap[] buffers;
    private int readIndex = 0;

    public DoubleTextureCubeMap(GL2 gl) {
        this(gl, GL2.GL_RGBA);
    }

    public DoubleTextureCubeMap(GL2 gl, int internalFormat) {
        buffers = new TextureCubeMap[2];
        for (int i = 0; i < 2; i++) {
            buffers[i] = new TextureCubeMap(gl, internalFormat);
        }
    }

    public void allocate(int size, int format, int type) {
        for (int i = 0; i < 2; i++) {
            buffers[i].allocate(size, format, type);
        }
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

    public TextureCubeMap getReadBuffer() {
        return buffers[readIndex];
    }

    public TextureCubeMap getWriteBuffer() {
        return buffers[(readIndex+1)%2];
    }

    public int getSize() {
        return buffers[0].getSize();
    }
}
