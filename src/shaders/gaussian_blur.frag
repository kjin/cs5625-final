/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120
#extension GL_ARB_texture_rectangle : enable

#define SQRT_2PI 2.50662827463

varying vec2 geom_texCoord;
uniform sampler2DRect texture;
uniform int size;
uniform float stdev;
uniform int axis;

void main() {

	vec2 unit_direction;
	if (axis == 0)
		unit_direction = vec2(1.0, 0.0);
	else if (axis == 1)
		unit_direction = vec2(0.0, 1.0);
		
	float a = 1/(stdev * SQRT_2PI);
	float total_weight = 0;
	
	gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
	
	for(int i = 0; i < size; i++)
	{
		int dist = size - i;
		float weight = a * exp(-dist*dist / (2*stdev*stdev));
		
		gl_FragColor += weight*texture2DRect(texture, geom_texCoord + dist*unit_direction);
		gl_FragColor += weight*texture2DRect(texture, geom_texCoord - dist*unit_direction);
		total_weight += 2*weight;
	}
	
	gl_FragColor += a * texture2DRect(texture, geom_texCoord);
	total_weight += a;
	
	gl_FragColor /= total_weight;	
}