/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 *
 * Currently a clone of Blinn-Phong.
 */

#version 120

const int SMOKE_PARTICLE_MATERIAL_ID = 5;

varying vec3 geom_position;
varying vec2 geom_texCoord;
varying vec3 geom_normal;

uniform vec4 mat_diffuseColor;
uniform bool mat_hasDiffuseTexture;
uniform sampler2D mat_diffuseTexture;

uniform vec3 mat_specularColor;
uniform bool mat_hasSpecularTexture;
uniform sampler2D mat_specularTexture;

uniform float mat_exponent;
uniform bool mat_hasExponentTexture;
uniform sampler2D mat_exponentTexture;

void main()
{
	vec4 diffuse = mat_diffuseColor;
	if (mat_hasDiffuseTexture) {
		vec4 tex = texture2D(mat_diffuseTexture, geom_texCoord);
		diffuse = diffuse * tex;
	}
	
	vec3 specular = mat_specularColor;
	if (mat_hasSpecularTexture) {
		vec3 tex = texture2D(mat_specularTexture, geom_texCoord).rgb;
		specular = specular * tex;
	}
	
	float exponent = mat_exponent;
	if (mat_hasExponentTexture) {
		float tex = texture2D(mat_exponentTexture, geom_texCoord).r;
		exponent = exponent * tex;
	}
	
	// Encoding: (matID, normal[3], color[4], position[3], exponent, specular, 0)
	gl_FragData[0] = vec4(float(SMOKE_PARTICLE_MATERIAL_ID), geom_normal);
	gl_FragData[1] = diffuse;
	gl_FragData[2] = vec4(geom_position, exponent);
	gl_FragData[3] = vec4(specular, 0.0);
}	
