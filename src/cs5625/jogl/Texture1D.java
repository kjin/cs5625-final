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
import java.nio.Buffer;

public class Texture1D extends Texture {    
    private int width;
    private boolean allocated = false;

    public Texture1D(GL2 gl) {
        super(gl, GL2.GL_TEXTURE_1D, GL2.GL_RGBA);
    }

    public Texture1D(GL2 gl, int internalFormat) {
        super(gl, GL2.GL_TEXTURE_1D, internalFormat);
    }

    public void setData(int width, int format, int type, Buffer buffer) {
        this.width = width;

        Texture oldTexture = TextureUnit.getActiveTextureUnit(gl).getBoundTexture();
        if (oldTexture != this) {
            bind();
        }        

        if (buffer != null) {
            buffer.rewind();            
        }
        gl.glTexImage1D(target, 0, internalFormat, width, 0, format, type, buffer);

        if (oldTexture == null) {
            unbind();
        } else if (oldTexture != this) {
            oldTexture.bind();
        }

        allocated = true;
    }

    public void allocate(int width, int format, int type) {
        setData(width, format, type, null);
    }

    public void allocate(int width, int height) { setData(width, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, null); }
    
    public boolean isAllocated() {
    	return allocated;
    }
    
    public int getWidth() {
    	return width;
    }
}
