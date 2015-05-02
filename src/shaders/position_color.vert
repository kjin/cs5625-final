/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

attribute vec3 vert_position;
attribute vec4 vert_color;

varying vec4 geom_color;

uniform mat4 sys_modelViewMatrix;
uniform mat4 sys_projectionMatrix;

void main()
{
	gl_Position = sys_projectionMatrix * 		
		(sys_modelViewMatrix * vec4(vert_position,1));	
	geom_color = vert_color;	
}