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

public class FragmentShader extends Shader {
	public FragmentShader(GL2 glContext, String src) throws GlslException {
		this(glContext, src, null);
	}

    public FragmentShader(GL2 glContext, String src, String srcFile) throws GlslException {
        super(GL2.GL_FRAGMENT_SHADER, glContext, src, srcFile);
    }
}
