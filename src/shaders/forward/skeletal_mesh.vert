/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

attribute vec3 vert_position;
attribute vec3 vert_normal;
attribute vec2 vert_texCoord;
attribute vec4 vert_tangent;
attribute vec4 vert_boneIndices;
attribute vec4 vert_boneWeights;
attribute float vert_morphStart;
attribute float vert_morphCount;

uniform mat4 sys_modelViewMatrix;
uniform mat4 sys_projectionMatrix;
uniform mat3 sys_normalMatrix;

varying vec3 geom_position;
varying vec3 geom_normal;
varying vec2 geom_texCoord;
varying vec3 geom_tangent;
varying vec3 geom_bitangent;

uniform sampler2D vert_morphDisplacements;
uniform int vert_morphDisplacementsWidth;
uniform int vert_morphDisplacementsHeight;
uniform sampler2D vert_morphWeights;
uniform int vert_morphWeightsWidth;
uniform int vert_morphWeightsHeight;
uniform sampler2D vert_boneXforms;
uniform int vert_boneXformsWidth;
uniform int vert_boneXformsHeight;

varying vec4 geom_color;

mat4 getBoneXform(float boneIndex) {
	mat4 result = mat4(0.0);
	float w = float(vert_boneXformsWidth);
	float h = float(vert_boneXformsHeight);
	float x = (boneIndex+0.5)/w;
	result[0] = texture2DLod(vert_boneXforms, vec2(x,0.5/h), 0.0);
	result[1] = texture2DLod(vert_boneXforms, vec2(x,1.5/h), 0.0);
	result[2] = texture2DLod(vert_boneXforms, vec2(x,2.5/h), 0.0);
	result[3] = texture2DLod(vert_boneXforms, vec2(x,3.5/h), 0.0);
	return result;
}

vec4 getMorphDisplacementInfo(int i) {
	float w = float(vert_morphDisplacementsWidth);
	float row = floor(float(i) / w);
	float col = float(i) - row * w;
	float x = (col + 0.5) / float(vert_morphDisplacementsWidth);
	float y = (row + 0.5) / float(vert_morphDisplacementsHeight);
	vec4 morphInfo = texture2DLod(vert_morphDisplacements, vec2(x,y), 0.0);
	return morphInfo;			
}

float getMorphWeight(int morphIndex) {
	float x = (float(morphIndex)+0.5) / float(vert_morphWeightsWidth);
	float y = 0.5 / float(vert_morphWeightsHeight);
	return texture2DLod(vert_morphWeights, vec2(x,y), 0.0).r;
}

void main()
{
	geom_color = vec4(0,0,1,1);

	vec3 position = vert_position;
	int start = int(vert_morphStart);
	int count = int(vert_morphCount);	
	if (count > 0) {
		for (int i=start; i<start+count; i++) {					
			vec4 morphInfo = getMorphDisplacementInfo(i);
			vec3 displacement = morphInfo.xyz;
			float morphIndex = morphInfo.w;
			float morphWeight = getMorphWeight(int(morphIndex));
			position += morphWeight * displacement;			
		}		
	}

	vec3 N = normalize(vert_normal);
	vec3 T = normalize(vert_tangent.xyz);
	vec3 B = normalize(cross(N, T));
	
	vec3 p = position;
	position = vec3(0);
	vec3 tangent = vec3(0.0);
	vec3 bitangent = vec3(0.0);
	for (int i=0;i<4;i++) {
		float boneIndex = vert_boneIndices[i];
		if (boneIndex >= 0) {
			float boneWeight = vert_boneWeights[i];
			if (boneWeight > 0) {
				mat4 M = getBoneXform(boneIndex);
				position += boneWeight*(M*vec4(p,1)).xyz;
				tangent += boneWeight*(M*vec4(T,0)).xyz;
				bitangent += boneWeight*(M*vec4(B,0)).xyz;
			}
		}
	}
	tangent = normalize(tangent);
	bitangent = normalize(bitangent);
	vec3 normal = normalize(cross(tangent, bitangent));
	bitangent = normalize(cross(normal, tangent)) * vert_tangent.w;

	gl_Position = sys_projectionMatrix *
			(sys_modelViewMatrix * vec4(position,1));	

	geom_position = (sys_modelViewMatrix * vec4(position,1)).xyz;	
	geom_texCoord = vert_texCoord;

	geom_normal = normalize(sys_normalMatrix * normal);	
	geom_tangent = normalize(sys_modelViewMatrix * vec4(tangent,0)).xyz;
	geom_bitangent = normalize(sys_modelViewMatrix * vec4(bitangent,0)).xyz;		
}
