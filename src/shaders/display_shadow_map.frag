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
uniform sampler2DRect shadowMap;
uniform float minZ;
uniform float maxZ;

void main() {
	vec4 value = texture2DRect(shadowMap, geom_texCoord);
	//vec4 value1 = texture2DRect(shadowMap, geom_texCoord + vec2(1,0));
	//vec4 value2 = texture2DRect(shadowMap, geom_texCoord + vec2(0,1));
	float theMinZ = 5;
	float theMaxZ = 80;
	if (minZ > maxZ) {
		theMinZ = maxZ;
		theMaxZ = minZ;
	}	
	if (value.w != 0) {
		float theZ = value.w;
		if (theZ < theMinZ) theZ = theMinZ;
		if (theZ > theMaxZ) theZ = theMaxZ;
		theZ = (theZ - theMinZ) / (theMaxZ - theMinZ);		
		gl_FragColor = vec4(theZ, theZ, theZ, 1);		
	} else {
		gl_FragColor = vec4(1);
	}
	//float dfdx = abs(value1.z / value1.w - value.z / value.w);
	//float dfdy = abs(value2.z / value2.w - value.z / value.w);
	//gl_FragColor = vec4(max(dfdx, dfdy), 0, 0, 1) * 100;	
}