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

varying vec2 geom_texCoord;
uniform sampler2DRect texture;
uniform float brightness;

void main() {

	vec4 fragColor = texture2DRect(texture, geom_texCoord);
	
	if(0.299*fragColor.r + 0.587*fragColor.g + 0.114*fragColor.b >= brightness)
	{
		gl_FragColor = 0.25 * fragColor;
	}
	else
	{
		gl_FragColor = vec4(0,0,0,1);
	}
}