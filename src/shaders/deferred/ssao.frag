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

varying vec2 geom_texCoord;

uniform sampler2DRect gbuf_materialParams1;
uniform sampler2DRect gbuf_materialParams2;
uniform sampler2DRect gbuf_materialParams3;
uniform sampler2DRect gbuf_materialParams4;

uniform int gbuf_width;
uniform int gbuf_height;

uniform float ssao_radius;
uniform float ssao_depthBias;
uniform int ssao_sampleCount;
uniform mat4 sys_projectionMatrix;

vec2 rotate(vec2 point, float angle)
{
	float c = cos(angle);
	float s = sin(angle);
	return vec2(c * point.x + s * point.y, c * point.y - s * point.x);
}

vec3 getAnyPerpendicularVector(vec3 v)
{
	if (abs(v.z) >= 0.0005)
	{
		return vec3(v.z, v.z, -v.x-v.y);
	}
	else
	{
		return vec3(-v.y-v.z, v.x, v.x);
	}
}

void main() {
	vec4 materialParams1 = texture2DRect(gbuf_materialParams1, geom_texCoord);
	vec4 materialParams2 = texture2DRect(gbuf_materialParams2, geom_texCoord);
	vec4 materialParams3 = texture2DRect(gbuf_materialParams3, geom_texCoord);
	vec4 materialParams4 = texture2DRect(gbuf_materialParams4, geom_texCoord);
	
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
	int k = int(min(ssao_sampleCount, 40.0));
	
	// Get random rotation:
	float angle = 2*PI*fract(sin(dot(geom_texCoord,vec2(12.9898,78.233))) * 43758.5453);
	
	// Get some reference vectors
	vec3 normal = materialParams1.yzw;
	normal = normalize(normal);
	
	vec3 tangent = getAnyPerpendicularVector(normal);
	tangent = normalize(tangent);
	
	vec3 bitangent = cross(normal, tangent);
	bitangent = normalize(bitangent);
	
	vec3 position = materialParams3.xyz;
	
	float numer = 0;
	float denom = 0;
	
	for (int i = 0; i < k; i++)
	{
		float angle1 = float(i) / float(k) * 2 * PI + angle;
		float angle2 = poissonSamples[2 * i] * PI / 2;
		float r = poissonSamples[2 * i + 1];
		vec3 alpha = vec3(r * cos(angle1) * sin(angle2), r * sin(angle1) * sin(angle2), r * cos(angle2));
		
		vec3 direction;
		direction = alpha.x * tangent + alpha.y * bitangent + alpha.z * normal;

		vec4 p = vec4(position + ssao_radius * direction, 1.0);
		vec4 p_ss = sys_projectionMatrix * p;
		p_ss /= p_ss.w;
		
		direction = normalize(direction);
		
		vec2 texCoord = vec2(((p_ss.x + 1)/2) * gbuf_width, ((p_ss.y + 1)/2) * gbuf_height);
		vec3 p_prime = texture2DRect(gbuf_materialParams3, texCoord).xyz;
		denom += dot(normal,direction); // Piazza @111
		if(abs(p.z) <= abs(p_prime.z) + ssao_depthBias || abs(p.z) > abs(p_prime.z) + ssao_depthBias + 5 * ssao_radius)
		{
			numer += dot(normal,direction);
		}
	}
	float visibility = 1;
	// for outdoor scene, not doing this check results in a black sky.
	if (denom != 0.0 && length(materialParams1.yzw) > 0)
	{
		visibility = numer / denom;
	}
	gl_FragColor = vec4(visibility);
}