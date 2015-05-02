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

uniform sampler2DRect image;

varying vec2 geom_texCoord;

float sRGBSingle(float c) {
	float a = 0.055;	
	if (c <= 0) 
		return 0;		
	else if (c < 0.0031308) {
		return 12.92*c;
	} else {
		if (c >= 1.0)
			return 1.0;
		else 
			return (1.0+a)*pow(c, 1.0/2.4)-a;
	}
}

vec4 sRGB(vec4 c) {
	return vec4(sRGBSingle(c.r), sRGBSingle(c.g), sRGBSingle(c.b), c.a);
}

void main() {		
	vec4 color = texture2DRect(image, geom_texCoord);	
	gl_FragColor = sRGB(color);	
}