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
varying vec3 geom_tangent;
varying vec3 geom_bitangent;

uniform vec4 mat_diffuseColor;
uniform bool mat_hasDiffuseTexture;
uniform sampler2D mat_diffuseTexture;

uniform vec3 mat_specularColor;
uniform bool mat_hasSpecularTexture;
uniform sampler2D mat_specularTexture;

uniform float mat_exponent;
uniform bool mat_hasExponentTexture;
uniform sampler2D mat_exponentTexture;

uniform bool mat_hasNormalTexture;
uniform sampler2D mat_normalTexture;

uniform int light_count;
uniform vec3 light_eyePosition[MAX_LIGHTS];
uniform vec3 light_attenuation[MAX_LIGHTS];
uniform vec3 light_color[MAX_LIGHTS];

void main()
{
	vec4 diffuse = mat_diffuseColor;
	if (mat_hasDiffuseTexture) {
		vec4 tex = texture2D(mat_diffuseTexture, geom_texCoord);
		diffuse *= tex;
	}

	vec3 specular = mat_specularColor;
	if (mat_hasSpecularTexture) {
		specular *= texture2D(mat_specularTexture, geom_texCoord).xyz;
	}

	float exponent = mat_exponent;
	if (mat_hasExponentTexture) {
		exponent = texture2D(mat_exponentTexture, geom_texCoord).x * exponent;
	}

	vec4 result = vec4(0,0,0,diffuse.a);	
	vec3 v = -normalize(geom_position);

	vec3 n = normalize(geom_normal);
	vec3 t = normalize(geom_tangent);
	vec3 b = normalize(cross(n, t));
	t = normalize(cross(b,n));
	if (dot(b, geom_bitangent) < 0)
		b *= -1;
	
	if (mat_hasNormalTexture) {
		vec3 texN = normalize(texture2D(mat_normalTexture, geom_texCoord).xyz - vec3(0.5));
		n = normalize(t*texN.x + b*texN.y + n*texN.z);
	}

	for (int i=0; i<light_count; i++) {
		vec3 l = light_eyePosition[i] - geom_position;
		float d = length(l);
		l = normalize(l);
		vec3 h = normalize(l + v);

		float ndotl = max(0.0, dot(n,l));
		float ndoth = max(0.0, dot(n,h));

		float pow_ndoth = ((ndotl > 0.0 && ndoth > 0.0) ? pow(ndoth, exponent) : 0.0);		
		
		float attenuation = dot(light_attenuation[i], vec3(1, d, d*d));
		
		result.xyz += (diffuse.xyz * ndotl + specular * pow_ndoth) * light_color[i] / attenuation;
	}
	
	gl_FragColor = result;	
}
