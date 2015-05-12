/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

#define NUM_PARTICLES 400
#define NUM_SIDES 8
#define PI 3.14159265359

attribute float vert_particle_index;
attribute float vert_particle_corner;

uniform mat4 sys_modelViewMatrix;
uniform mat4 sys_projectionMatrix;
uniform mat4 sys_viewMatrix;
uniform vec4 particlePositionScale[NUM_PARTICLES]; // should be FancyParticleSystem::NUM_PARTICLES


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
	geom_texCoord = vec2(0.5, 0.5);
	if (vert_particle_corner < NUM_SIDES)
	{
		float c = cos(2 * PI * vert_particle_corner / NUM_SIDES);
		float s = sin(2 * PI * vert_particle_corner / NUM_SIDES);
		vertexOffset = radius*(c * rightVector + s * upVector);
		geom_texCoord += vec2(0.5 * c, 0.5 * s);
	}
	else
	{
		vertexOffset = vec3(0, 0, depthVector);
		geom_texCoord = vec2(0.5, 0.5);
	}
	
	int particleIndex = int(vert_particle_index);
	
	// there's four types of particles. only get 1
	geom_texCoord /= 2;
	// based on the particle index we get what type of particle we want
	int particleType = int(mod(particleIndex, 4));
	if (particleType == 1 || particleType == 3)
	{
		geom_texCoord.x += 0.5;
	}
	if (particleType == 2 || particleType == 3)
	{
		geom_texCoord.y += 0.5;
	}
	
	vec3 particlePosition = particlePositionScale[particleIndex].xyz;
	float particleScale = particlePositionScale[particleIndex].w;
	vec4 position = sys_modelViewMatrix * vec4(particlePosition + particleScale * 0.5f * vertexOffset,1);
	geom_position = position.xyz;
	gl_Position = sys_projectionMatrix * position;
}
