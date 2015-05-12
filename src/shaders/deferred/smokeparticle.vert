/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

attribute float vert_particle_index;
attribute float vert_particle_corner;

const int BOTTOM_LEFT_CORNER = 0;
const int BOTTOM_RIGHT_CORNER = 1;
const int TOP_RIGHT_CORNER = 2;
const int TOP_LEFT_CORNER = 3;
const int CENTER = 4;

uniform mat4 sys_modelViewMatrix;
uniform mat4 sys_projectionMatrix;
uniform mat4 sys_viewMatrix;
uniform vec3 particleLocations[200]; // should be FancyParticleSystem::NUM_PARTICLES

varying vec3 geom_position;
varying vec2 geom_texCoord;

void main()
{
	float radius = 1.0f;
	
	vec3 rightVector = vec3(sys_viewMatrix[0][0], sys_viewMatrix[1][0], sys_viewMatrix[2][0]);
	vec3 upVector = vec3(sys_viewMatrix[0][1], sys_viewMatrix[1][1], sys_viewMatrix[2][1]);
	
	vec3 depthVector = cross(rightVector,upVector);
	//rightVector = cross(upVector,depthVector);

	vec3 vertexOffset;
	if (vert_particle_corner == BOTTOM_LEFT_CORNER)
	{
		vertexOffset = radius*(-upVector - rightVector);
		geom_texCoord = vec2(0, 0);
	}
	else if (vert_particle_corner == BOTTOM_RIGHT_CORNER)
	{
		vertexOffset = radius*(-upVector + rightVector);
		geom_texCoord = vec2(1, 0);
	}
	else if (vert_particle_corner == TOP_RIGHT_CORNER)
	{
		vertexOffset = radius*(upVector + rightVector);
		geom_texCoord = vec2(1, 1);
	}
	else if (vert_particle_corner == TOP_LEFT_CORNER)
	{
		vertexOffset = radius*(upVector - rightVector);
		geom_texCoord = vec2(0, 1);
	}
	else if (vert_particle_corner == CENTER)
	{
		vertexOffset = vec3(0, 0, depthVector);
		geom_texCoord = vec2(0.5, 0.5);
	}
	
	geom_texCoord /= 2;
	vec3 particleLocation = particleLocations[int(vert_particle_index)];
	vec4 position = sys_modelViewMatrix * vec4(particleLocation + 0.5 * vertexOffset,1);
	geom_position = position.xyz;
	gl_Position = sys_projectionMatrix * position;
}
