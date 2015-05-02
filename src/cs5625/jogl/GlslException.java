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

public class GlslException extends RuntimeException {
	private static final long serialVersionUID = 9174089499226177646L;

	public GlslException(String msg) {
		super("GLSL Error\n" + msg);
	}

	public GlslException(String msg, Throwable t) {
		super("GLSL Error\n" + msg, t);
	}
}
