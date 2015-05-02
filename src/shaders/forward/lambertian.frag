/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

#define MAX_LIGHTS 40

varying vec3 geom_position;
varying vec2 geom_texCoord;
varying vec3 geom_normal;

uniform vec4 mat_diffuseColor;
uniform bool mat_hasDiffuseTexture;
uniform sampler2D mat_diffuseTexture;

uniform int light_count;
uniform vec3 light_eyePosition[MAX_LIGHTS];
uniform vec3 light_attenuation[MAX_LIGHTS];
uniform vec3 light_color[MAX_LIGHTS];

void main()
{
	vec4 diffuse = mat_diffuseColor;
	if (mat_hasDiffuseTexture) {
		vec4 tex = texture2D(mat_diffuseTexture, geom_texCoord);
		diffuse = diffuse * tex;
	}

	vec4 result = vec4(0,0,0,diffuse.a);	
	vec3 n = normalize(geom_normal);
	for (int i=0; i<light_count; i++) {
		vec3 l = light_eyePosition[i] - geom_position;
		float d = sqrt(dot(l, l));
		l = normalize(l);
		float attenuation = dot(light_attenuation[i], vec3(1, d, d*d));
		float dotProd = max(dot(n,l), 0);
		result.xyz += diffuse.xyz * dotProd * light_color[i] / attenuation;
	}
	
	gl_FragColor = result;
}
