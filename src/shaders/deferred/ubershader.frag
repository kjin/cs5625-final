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

const float PI = 3.14159265358979323846264;

#define MAX_LIGHTS 40

const int SINGLE_COLOR_MATERIAL_ID = 1;
const int LAMBERTIAN_MATERIAL_ID = 2;
const int BLINNPHONG_MATERIAL_ID = 3;
const int XTOON_MATERIAL_ID = 4;
const int SMOKE_PARTICLE_MATERIAL_ID = 5;

const int NO_SHADOWS = 0;
const int SIMPLE_SHADOW_MAP = 1;
const int PERCENTAGE_CLOSER_FILTERING = 2;
const int PERCENTAGE_CLOSER_SOFT_SHADOW = 3;

uniform int pointLight_count;
uniform vec3 pointLight_eyePosition[MAX_LIGHTS];
uniform vec3 pointLight_attenuation[MAX_LIGHTS];
uniform vec3 pointLight_color[MAX_LIGHTS];
					  
uniform sampler2DRect gbuf_materialParams1;
uniform sampler2DRect gbuf_materialParams2;
uniform sampler2DRect gbuf_materialParams3;
uniform sampler2DRect gbuf_materialParams4;

varying vec2 geom_texCoord;

uniform mat4 sys_inverseViewMatrix;

uniform bool spotLight_enabled;
uniform mat4 spotLight_viewMatrix;
uniform mat4 spotLight_projectionMatrix;
uniform sampler2DRect spotLight_shadowMap;
uniform int spotLight_shadowMapWidth;
uniform int spotLight_shadowMapHeight;
uniform int spotLight_shadowMode;
uniform float spotLight_pcfWindowWidth;
uniform int spotLight_pcfKernelSampleCount;
uniform float spotLight_shadowMapBiasScale;
uniform float spotLight_shadowMapConstantBias;
uniform float spotLight_near;
uniform float spotLight_lightWidth;
uniform vec3 spotLight_eyePosition;
uniform vec3 spotLight_attenuation;
uniform vec3 spotLight_color;
uniform int spotLight_pcssBlockerKernelSampleCount;
uniform int spotLight_pcssPenumbraKernelSampleCount;
uniform float spotLight_fov;

uniform vec3 backgroundColor = vec3(0.06, 0.3, 1.0);

vec2 rotate(vec2 point, float angle)
{
	float c = cos(angle);
	float s = sin(angle);
	return vec2(c * point.x + s * point.y, c * point.y - s * point.x);
}

float getShadowFactor(vec3 position) {	
	vec4 p = vec4(position, 1.0);
	p = spotLight_viewMatrix * sys_inverseViewMatrix * p;
	p = spotLight_projectionMatrix * p;
	p = p / p.w;
	
	// using just simple shadow mapping
	vec2 texCoord = vec2(((p.x + 1)/2)*spotLight_shadowMapWidth, ((p.y + 1)/2)*spotLight_shadowMapHeight);
	vec4 q = texture2DRect(spotLight_shadowMap, texCoord);
	float bias = spotLight_shadowMapBiasScale*max(abs(q.x),abs(q.y)) + spotLight_shadowMapConstantBias;
	
	if(p.z > q.z / q.w + bias) {
		return 0.0;
	} else {
		return 1.0;
	}
}

void main() {		
	vec4 materialParams1 = texture2DRect(gbuf_materialParams1, geom_texCoord);
	vec4 materialParams2 = texture2DRect(gbuf_materialParams2, geom_texCoord);
	vec4 materialParams3 = texture2DRect(gbuf_materialParams3, geom_texCoord);
	vec4 materialParams4 = texture2DRect(gbuf_materialParams4, geom_texCoord);

	vec3 normal = normalize(materialParams1.yzw);
	vec3 color = materialParams2.xyz;
	vec3 position = materialParams3.xyz;
	int materialID = int(materialParams1.x);	
	gl_FragColor = vec4(0,0,0,1);
	
	// from Piazza @98
	if (dot(position, normal) > 0)
	{
		normal = -normal;
	}
	
	/************************************************/
	if (materialID == 0) {
		gl_FragColor.xyz = backgroundColor;
	} 
	/*Single Color***********************************/
	else if (materialID == SINGLE_COLOR_MATERIAL_ID) {
		if (!spotLight_enabled)
			gl_FragColor.xyz += color;
	} 
	/*Lambertian*************************************/
	else if (materialID == LAMBERTIAN_MATERIAL_ID) {
		if (!spotLight_enabled)	{
		
			for (int i=0; i<pointLight_count; i++) {
				vec3 l = pointLight_eyePosition[i] - position;
				float d = sqrt(dot(l, l));
				l = normalize(l);
				float attenuation = dot(pointLight_attenuation[i], vec3(1, d, d*d));
				float dotProd = max(dot(normal,l), 0);
				gl_FragColor.xyz += color * dotProd * pointLight_color[i] / attenuation;
			}
		
		} else {
			
			vec3 l = spotLight_eyePosition - position;
			float d = sqrt(dot(l, l));
			l = normalize(l);
			float attenuation = dot(spotLight_attenuation, vec3(1, d, d*d));
			float dotProd = max(dot(normal,l), 0);
			gl_FragColor.xyz += color * dotProd * spotLight_color / attenuation;
			
		}
	}
	/*Blinn-Phong************************************/
	else if (materialID == BLINNPHONG_MATERIAL_ID) {
		// Encoding: (matID, normal[3], color[4], position[3], exponent, specular, 0)
		float exponent = materialParams3.w;
		vec3 specular = materialParams4.xyz;
		vec3 v = normalize(-position);
		
		if (!spotLight_enabled) {	
			for (int i=0; i<pointLight_count; i++) {
			
				vec3 l = pointLight_eyePosition[i] - position;
				float d = sqrt(dot(l, l));
				float attenuation = dot(pointLight_attenuation[i], vec3(1, d, d*d));
				l = normalize(l);
				
				//diffuse calculation
				float nlDotProd = max(dot(normal, l), 0);
				
				//specular calculation
				vec3 h = normalize(l + v);
				float nhDotProd = max(dot(normal, h), 0);
				
				gl_FragColor.xyz += (color * nlDotProd + specular * pow(nhDotProd, exponent)) * pointLight_color[i] / attenuation;
			}			
		} else {
			
			vec3 l = spotLight_eyePosition - position;
			float d = sqrt(dot(l, l));
			float attenuation = dot(spotLight_attenuation, vec3(1, d, d*d));
			l = normalize(l);
			
			//diffuse calculation
			float nlDotProd = max(dot(normal, l), 0);
			
			//specular calculation
			vec3 h = normalize(l + v);
			float nhDotProd = max(dot(normal, h), 0);
			
			gl_FragColor.xyz += (color * nlDotProd + specular * pow(nhDotProd, exponent)) * spotLight_color / attenuation;				
		}
	}
	/*XToon************************************/
	else if (materialID == XTOON_MATERIAL_ID) {
		vec2 dz = materialParams4.xy;
		
		gl_FragColor.xyz += color;
		
		/* outline option: (doesn't look good)
		if(length(dz) > 1.8)
			gl_FragColor.xyz = vec3(0,0,0);
		*/
	}
	/*Particle************************************/
	else if (materialID == SMOKE_PARTICLE_MATERIAL_ID) {

	}
	else {
		if (!spotLight_enabled) {
			gl_FragColor.xyz += color;
		}
	}
	
	gl_FragColor.xyz += (materialParams4.xyz);
	if(materialParams4.w > 0.1)
	{
		gl_FragColor.xyz /= 3;
	}	
}