/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

const int LAMBERTIAN_MATERIAL_ID = 2;

varying vec3 geom_position;
varying vec2 geom_texCoord;
varying vec3 geom_normal;

uniform vec4 mat_diffuseColor;
uniform bool mat_hasDiffuseTexture;
uniform sampler2D mat_diffuseTexture;

void main()
{
	vec4 diffuse = mat_diffuseColor;
	if (mat_hasDiffuseTexture) {
		vec4 tex = texture2D(mat_diffuseTexture, geom_texCoord);
		diffuse = diffuse * tex;
	}
	
	// Encoding: (matID, normal[3], color[4], position[3], 0[5])
	gl_FragData[0] = vec4(float(LAMBERTIAN_MATERIAL_ID), geom_normal);
	gl_FragData[1] = diffuse;
	gl_FragData[2] = vec4(geom_position, 0.0);
	gl_FragData[3] = vec4(0.0, 0.0, 0.0, 0.0);
}
