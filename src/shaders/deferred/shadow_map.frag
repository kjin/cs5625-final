/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

varying vec3 geom_position;
uniform mat4 sys_projectionMatrix;
uniform int shadowMapWidth;
uniform int shadowMapHeight;

void main()
{	
	// TODO: (Task 2) Edit this file to write appropriate data to the shadow map.
	vec4 p = sys_projectionMatrix * vec4(geom_position,1);
	
	vec4 q;
	q.x = dFdx(p.z/p.w) * shadowMapWidth;
	q.y = dFdy(p.z/p.w) * shadowMapHeight;
	q.z = p.z;
	q.w = p.w;
	
	gl_FragData[0] = q;
}