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
import javax.media.opengl.GLException;

public abstract class Texture implements GLResource {
    public int minFilter;
    public int magFilter;
    public int wrapS;
    public int wrapT;
    public int wrapR;
    private boolean disposed;
    protected int id;
    protected int target;
    protected GL2 gl;
    protected TextureUnit boundTextureUnit;
    protected int internalFormat;

    public Texture(GL2 gl, int target, int internalFormat) {
        int idv[] = new int[1];
        gl.glGenTextures(1, idv, 0);
        this.gl = gl;
        this.id = idv[0];
        this.target = target;
        this.boundTextureUnit = null;
        this.internalFormat = internalFormat;
        this.disposed = false;

        minFilter = GL2.GL_NEAREST;
        magFilter = GL2.GL_NEAREST;
        wrapS = GL2.GL_REPEAT;
        wrapT = GL2.GL_REPEAT;
        wrapR = GL2.GL_REPEAT;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public boolean isBound() {
        return boundTextureUnit != null;
    }

    public int getId() {
        return id;
    }

    public int getTarget() {
        return target;
    }
    
    public int getInternalFormat() {
    	return internalFormat;
    }

    public void bind() {
        bindTo(TextureUnit.getActiveTextureUnit(gl));
    }

    public void bindTo(TextureUnit textureUnit) {
        if (isDisposed()) {
            throw new GLException("program tries to bind a disposed texture");
        }

        textureUnit.bindTexture(this);
        boundTextureUnit = textureUnit;
    }

    public void unbind() {
        if (isBound()) {
            if (isDisposed()) {
                throw new GLException("program tries to unbind a disposed texture");
            }

            boundTextureUnit.unbindTexture(this);
            boundTextureUnit = null;
        }
    }

    protected void setTextureParameters() {
        gl.glTexParameteri(target, GL2.GL_TEXTURE_MAG_FILTER, magFilter);
        gl.glTexParameteri(target, GL2.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_S, wrapS);
        gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_T, wrapT);
        gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_R, wrapR);
    }

    public void useWith(TextureUnit unit) {
        enable();
        unit.activate();
        bindTo(unit);
        setTextureParameters();
    }

    public void use() {
        enable();
        bind();
        setTextureParameters();
    }

    public void unuse() {
        unbind();
        disable();
    }

    public void disposeGL() {
        if (!disposed) {
            if (isBound()) {
                unbind();
            }

            int idv[] = new int[1];
            idv[0] = id;
            gl.glDeleteTextures(1, idv, 0);
            disposed = true;
        }
    }

    public void enable() {
        gl.glEnable(target);
    }

    public void disable() {
        gl.glDisable(target);
    }
}
