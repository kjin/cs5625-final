/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

const int SINGLE_COLOR_MATERIAL = 1;

uniform vec4 color;

varying vec3 geom_position;

void main()
{	
	gl_FragData[0] = vec4(float(SINGLE_COLOR_MATERIAL), 0.0, 0.0, 0.0);
	gl_FragData[1] = vec4(color.xyz, 0.5);
	gl_FragData[2] = vec4(geom_position, 0);
	gl_FragData[3] = vec4(0.0);
}