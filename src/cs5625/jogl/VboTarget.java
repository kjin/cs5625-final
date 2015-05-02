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

import javax.media.opengl.GL2;

public class VboTarget {
    private final int constant;
    private static Vbo boundVbo = null;

    public static VboTarget ARRAY_BUFFER = new VboTarget(GL2.GL_ARRAY_BUFFER);
    public static VboTarget ELEMENT_ARRAY_BUFFER = new VboTarget(GL2.GL_ELEMENT_ARRAY_BUFFER);

    private VboTarget(int constant) {
        this.constant = constant;
    }

    public int getConstant() {
        return constant;
    }

    public Vbo getBoundVbo() {
        return boundVbo;
    }

    public void setBoundVbo(Vbo vbo) {
        boundVbo = vbo;
    }

    public void unbindVbo() {
        if (boundVbo != null) {
            boundVbo.unbind();
        }
    }
}
