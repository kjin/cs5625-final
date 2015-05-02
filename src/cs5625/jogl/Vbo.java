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

/*
 */
package cs5625.jogl;

import java.nio.ByteBuffer;
import javax.media.opengl.GL2;

public class Vbo implements GLResource {
    private int id;
    private GL2 gl;
    private VboTarget target;
    private boolean disposed;

    public Vbo(GL2 gl, VboTarget target) {
        this.gl = gl;
        this.target = target;
        int[] idv = new int[1];
        gl.glGenBuffers(1, idv, 0);
        id = idv[0];
    }

    public GL2 getGL() {
        return gl;
    }

    public void bind() {
        if (target.getBoundVbo() != null) {
            target.getBoundVbo().unbind();
        }
        gl.glBindBuffer(target.getConstant(), id);
        target.setBoundVbo(this);
    }

    public void unbind() {
        if (target.getBoundVbo() == this) {
            gl.glBindBuffer(target.getConstant(), 0);
            target.setBoundVbo(null);
        }
    }

    public void use() {
        bind();
    }

    public void unuse() {
        unbind();
    }

    public boolean isBound() {
        return target.getBoundVbo() == this;
    }

    public int getId() {
        return id;
    }

    public void setData(ByteBuffer buffer) {
        setData(buffer.capacity(), buffer);
    }

    public void setData(int width, ByteBuffer buffer) {
        bind();
        buffer.rewind();
        gl.glBufferData(target.getConstant(), width, buffer, GL2.GL_STATIC_DRAW);
        unbind();
    }

    public void disposeGL() {
        if (!disposed) {
            if (isBound())
                unbind();

            int idv[] = new int[1];
            idv[0] = id;
            gl.glDeleteBuffers(1, idv, 0);
            disposed = true;
        }
    }
}
