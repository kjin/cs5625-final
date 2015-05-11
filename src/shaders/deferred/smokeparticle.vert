/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

attribute int vert_particle_index;
attribute int vert_particle_corner;

uniform mat4 sys_modelViewMatrix;
uniform mat4 sys_projectionMatrix;

varying vec3 geom_position;
varying vec4 geom_color;

void main()
{
	//gl_Position = sys_projectionMatrix *
	//		(sys_modelViewMatrix * vec4(vert_position,1));	

	geom_position = (sys_modelViewMatrix * vec4(0,0,0,1)).xyz;	
	geom_color = vec4(0,0,0,1);
}
