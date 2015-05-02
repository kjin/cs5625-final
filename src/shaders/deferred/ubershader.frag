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
	if (spotLight_shadowMode == NO_SHADOWS) {
		return 1.0;		
	} else if (spotLight_shadowMode == SIMPLE_SHADOW_MAP) {
		vec4 p = vec4(position, 1.0);
		p = spotLight_viewMatrix * sys_inverseViewMatrix * p;
		p = spotLight_projectionMatrix * p;
		p = p / p.w;
		
		vec2 texCoord = vec2(((p.x + 1)/2)*spotLight_shadowMapWidth, ((p.y + 1)/2)*spotLight_shadowMapHeight);
		vec4 q = texture2DRect(spotLight_shadowMap, texCoord);
		float bias = spotLight_shadowMapBiasScale*max(abs(q.x),abs(q.y)) + spotLight_shadowMapConstantBias;
		
		if(p.z > q.z / q.w + bias) {
			return 0.0;
		} else {
			return 1.0;
		}
			
	} else if (spotLight_shadowMode == PERCENTAGE_CLOSER_FILTERING || spotLight_shadowMode == PERCENTAGE_CLOSER_SOFT_SHADOW) {		
		float poissonSamples[80] = float[80](0.0340212, 0.674301, 0.0858099, 0.496943, 
		0.0694185, 0.860058, 0.0406619, 0.218842, 0.0735586, 0.0448695, 0.15123, 0.339518, 
		0.271311, 0.469369, 0.200576, 0.774924, 0.204111, 0.154617, 0.20539, 0.948375, 
		0.185354, 0.612016, 0.321298, 0.0390153, 0.304494, 0.28675, 0.326761, 0.667022, 
		0.426756, 0.385165, 0.43075, 0.539734, 0.35753, 0.855105, 0.472576, 0.713726, 
		0.599869, 0.436843, 0.531687, 0.274545, 0.413117, 0.167727, 0.53426, 0.849135, 
		0.472694, 0.998865, 0.641721, 0.957124, 0.579323, 0.597005, 0.589336, 0.120575, 
		0.745134, 0.148836, 0.688499, 0.293683, 0.785452, 0.00750335, 0.65954, 0.738719, 
		0.916359, 0.112317, 0.829323, 0.681274, 0.773597, 0.848216, 0.731165, 0.561037,
		0.795832, 0.41844, 0.931794, 0.949323, 0.863814, 0.265922, 0.97478, 0.38034, 
		0.928406, 0.792087, 0.917633, 0.543024);
		// Get random rotation:
		float angle = 2*PI*fract(sin(dot(geom_texCoord,vec2(12.9898,78.233))) * 43758.5453);
		
		vec4 p = vec4(position, 1.0);
		p = spotLight_viewMatrix * sys_inverseViewMatrix * p;
		float d = -p.z / p.w;
		p = spotLight_projectionMatrix * p;
		vec4 p_orig = p;
		p = p / p.w;
		
		vec2 baseTexCoord = vec2(((p.x + 1)/2)*spotLight_shadowMapWidth, ((p.y + 1)/2)*spotLight_shadowMapHeight);
		
		if (spotLight_shadowMode == PERCENTAGE_CLOSER_FILTERING)
		{
			float numPoissonSamples = min(float(spotLight_pcfKernelSampleCount), 40.0);
			float occludedSamples = 0.0;
			for (int i = 0; i < numPoissonSamples; i++)
			{
				vec2 texCoord = baseTexCoord + spotLight_pcfWindowWidth * rotate(vec2(poissonSamples[2 * i] - 0.5, poissonSamples[2 * i + 1] - 0.5), angle);
				vec4 q = texture2DRect(spotLight_shadowMap, texCoord);
				float bias = spotLight_shadowMapBiasScale*max(abs(q.x),abs(q.y)) + spotLight_shadowMapConstantBias;
				if(p.z <= q.z / q.w + bias) {
					occludedSamples += 1.0;
				}
			}
			return occludedSamples / numPoissonSamples;
		}
		else //spotLight_shadowMode == PERCENTAGE_CLOSER_SOFT_SHADOW
		{
			// s is defined in task 5.
			float nearPlaneSize = 2.0 * spotLight_near * tan(spotLight_fov / 2.0);
			vec2 s = ((d - spotLight_near) / d * spotLight_lightWidth) / nearPlaneSize * vec2(spotLight_shadowMapWidth, spotLight_shadowMapHeight);
			
			// evaluate poisson points in s x s window.
			float numPoissonSamples = min(float(spotLight_pcssBlockerKernelSampleCount), 40.0);
			float occludedSamples = 0.0;
			float averageDepth = 0.0;
			for (int i = 0; i < numPoissonSamples; i++)
			{
				vec2 texCoord = baseTexCoord + s * rotate(vec2(poissonSamples[2 * i] - 0.5, poissonSamples[2 * i + 1] - 0.5), angle);
				vec4 q = texture2DRect(spotLight_shadowMap, texCoord);
				if(p.z > q.z / q.w) {
					occludedSamples += 1.0;
					averageDepth += q.w;
				}
			}
			averageDepth /= occludedSamples;
			vec2 penumbraSize = vec2(0,0);
			if (occludedSamples > 0)
			{
				penumbraSize = (p_orig.w - averageDepth) / averageDepth * s;
			}
			
			// re-evaluate poisson points, this time in penumbra.
			numPoissonSamples = min(float(spotLight_pcssPenumbraKernelSampleCount), 40.0);
			occludedSamples = 0.0;
			for (int i = 0; i < numPoissonSamples; i++)
			{
				vec2 texCoord = baseTexCoord + penumbraSize * rotate(vec2(poissonSamples[2 * i] - 0.5, poissonSamples[2 * i + 1] - 0.5), angle);
				vec4 q = texture2DRect(spotLight_shadowMap, texCoord);
				float bias = spotLight_shadowMapBiasScale*max(abs(q.x),abs(q.y)) + spotLight_shadowMapConstantBias;
				if(p.z <= q.z / q.w + bias) {
					occludedSamples += 1.0;
				}
			}
			return occludedSamples / numPoissonSamples;
		}
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
		
			float factor = getShadowFactor(position);
			
			vec3 l = spotLight_eyePosition - position;
			float d = sqrt(dot(l, l));
			l = normalize(l);
			float attenuation = dot(spotLight_attenuation, vec3(1, d, d*d));
			float dotProd = max(dot(normal,l), 0);
			gl_FragColor.xyz += factor * color * dotProd * spotLight_color / attenuation;
			
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
		
			float factor = getShadowFactor(position);
			
			vec3 l = spotLight_eyePosition - position;
			float d = sqrt(dot(l, l));
			float attenuation = dot(spotLight_attenuation, vec3(1, d, d*d));
			l = normalize(l);
			
			//diffuse calculation
			float nlDotProd = max(dot(normal, l), 0);
			
			//specular calculation
			vec3 h = normalize(l + v);
			float nhDotProd = max(dot(normal, h), 0);
			
			gl_FragColor.xyz += factor * (color * nlDotProd + specular * pow(nhDotProd, exponent)) * spotLight_color / attenuation;				
		}
	} else {
		if (!spotLight_enabled) {
			gl_FragColor.xyz += color;
		}
	}		
}