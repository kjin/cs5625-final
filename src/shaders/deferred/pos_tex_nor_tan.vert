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
attribute vec3 vert_normal;
attribute vec2 vert_texCoord;
attribute vec4 vert_tangent;

uniform mat4 sys_modelViewMatrix;
uniform mat4 sys_projectionMatrix;
uniform mat3 sys_normalMatrix;

varying vec3 geom_position;
varying vec3 geom_normal;
varying vec2 geom_texCoord;
varying vec3 geom_tangent;
varying vec3 geom_bitangent;

void main()
{
	gl_Position = sys_projectionMatrix *
			(sys_modelViewMatrix * vec4(vert_position,1));	

	geom_position = (sys_modelViewMatrix * vec4(vert_position,1)).xyz;	
	geom_texCoord = vert_texCoord;

	vec3 N = normalize(vert_normal);
	vec3 T = normalize(vert_tangent.xyz);
	vec3 B = normalize(cross(N, T) * vert_tangent.w);
	geom_normal = normalize(sys_normalMatrix * N);	
	geom_tangent = normalize(sys_modelViewMatrix * vec4(T,0)).xyz;
	geom_bitangent = normalize(sys_modelViewMatrix * vec4(B,0)).xyz;
}
