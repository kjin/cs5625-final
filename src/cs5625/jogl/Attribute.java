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

public class Attribute {
	private GL2 gl;
	private Program program;
	private String name;
	private int size;
	private int type;
	private int location;
	private boolean enabled;

	public Attribute(GL2 glContext, Program prog, int index) {
		this.program = prog;
		this.gl = glContext;

		byte[] attribName = new byte[512];

		int[] attribNameLength = new int[1];
		int[] attribSize = new int[1];
		int[] attribType = new int[1];

		// Get the uniform info (name, type, size)
		this.gl.glGetActiveAttrib(this.program.getId(), index,
				attribName.length, attribNameLength, 0, attribSize, 0,
				attribType, 0, attribName, 0);

		this.name = new String(attribName, 0, attribNameLength[0]);
		this.size = attribSize[0];
		this.type = attribType[0];

		// Get the uniform location within the program
		this.location = this.gl.glGetAttribLocation(this.program.getId(),
				this.name);

		this.enabled = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public Attribute enable() {
		gl.glEnableVertexAttribArray(location);
		enabled = true;
		return this;
	}

	public Attribute disable() {
		gl.glEnableVertexAttribArray(location);
		enabled = false;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Attribute setup(GL2 gl, int size, int type, boolean normalized, int stride, long pointer) {
		gl.glVertexAttribPointer(getLocation(), size, type, normalized, stride, pointer);
		return this;
	}

	public Attribute setup(GL2 gl, AttributeSpec spec) {
		setup(gl, spec.size, spec.type, spec.normalized, spec.stride, spec.pointer);
		return this;
	}
}
