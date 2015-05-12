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
#define MAX_LIGHTS 40

const int SMOKE_PARTICLE_MATERIAL_ID = 5;

varying vec3 geom_position;
varying vec2 geom_texCoord;

uniform vec4 mat_diffuseColor;
uniform bool mat_hasNormalTexture;
uniform sampler2D mat_normalTexture;

uniform int pointLight_count;
uniform vec3 pointLight_eyePosition[MAX_LIGHTS];
uniform vec3 pointLight_attenuation[MAX_LIGHTS];
uniform vec3 pointLight_color[MAX_LIGHTS];

void main()
{
	vec4 diffuse = mat_diffuseColor;
	
	// normal stuffs
	vec3 normal = vec3(0,0,1);
	float alpha = 0;
	if (mat_hasNormalTexture) {
		vec4 tex = texture2D(mat_normalTexture, geom_texCoord);
		
		normal = tex.xyz;
		normal = normalize(normal);
		
		alpha = tex.w;
	}
	vec3 color = vec3(0.0, 0.0, 0.0);
	for (int i=0; i<pointLight_count; i++) {
		vec3 l = pointLight_eyePosition[i] - geom_position;
		float d = sqrt(dot(l, l));
		l = normalize(l);
		float attenuation = dot(pointLight_attenuation[i], vec3(1, d, d*d));
		float dotProd = max(dot(normal,l), 0);
		color = diffuse.xyz * dotProd * pointLight_color[i];
	}
	
	// Only store smoke in the last buffer
	gl_FragData[0] = vec4(0.0, 0.0, 0.0, 0.0);
	gl_FragData[1] = vec4(0.0, 0.0, 0.0, 0.0);
	gl_FragData[2] = vec4(0.0, 0.0, 0.0, 0.0);
	gl_FragData[3] = vec4(1 - color.x, 1 - color.y, 1 - color.z, alpha);
	
	/*gl_FragData[0] = vec4(float(SMOKE_PARTICLE_MATERIAL_ID), normal);
	gl_FragData[1] = vec4(color, 1);
	gl_FragData[2] = vec4(geom_position, 0);
	gl_FragData[3] = vec4(0.0);*/
}	
