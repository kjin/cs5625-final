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

public class Vao implements GLResource {
    private int id;
    private GL2 gl;
    private boolean disposed = false;
    private boolean bound = false;

    public Vao(GL2 gl) {
        int[] ids = new int[1];
        gl.glGenVertexArrays(1, ids, 0);
        id = ids[0];
        this.gl = gl;
    }

    public void bind() {
        gl.glBindVertexArray(id);
        bound = true;
    }

    public void unbind() {
        gl.glBindVertexArray(0);
        bound = false;
    }

    public boolean isBound() {
        return bound;
    }


    @Override
    public void disposeGL() {
        if (!disposed) {
            if (isBound())
                unbind();

            int idv[] = new int[1];
            idv[0] = id;
            gl.glDeleteVertexArrays(1, idv, 0);
            disposed = true;
        }
    }
}
