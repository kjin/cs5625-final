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

package cs5625.gfx.glcache;

import javax.media.opengl.GL2;

/**
 * Represents an object that provides and updates an OpenGL resource such as
 * a vertex buffer, a texture object, a program, etc.
 */
public interface GLResourceProvider<T> {
    public void updateGLResource(GL2 gl, GLResourceRecord record);
    public T getGLResource(GL2 gl);
}
