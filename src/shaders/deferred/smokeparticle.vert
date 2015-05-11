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
const int TOP_LEFT_CORNER = 2;
const int TOP_RIGHT_CORNER = 3;

uniform mat4 sys_modelViewMatrix;
uniform mat4 sys_projectionMatrix;
uniform float particleLocations[600]; // should be FancyParticleSystem::NUM_PARTICLES

varying vec3 geom_position;
varying vec2 geom_texCoord;

void main()
{
	vec3 vertexOffset;
	if (vert_particle_corner == BOTTOM_LEFT_CORNER)
	{
		vertexOffset = vec3(-1, -1, 0);
		geom_texCoord = vec2(0, 0);
	}
	else if (vert_particle_corner == BOTTOM_RIGHT_CORNER)
	{
		vertexOffset = vec3(1, -1, 0);
		geom_texCoord = vec2(1, 0);
	}
	else if (vert_particle_corner == TOP_LEFT_CORNER)
	{
		vertexOffset = vec3(-1, 1, 0);
		geom_texCoord = vec2(0, 1);
	}
	else if (vert_particle_corner == TOP_RIGHT_CORNER)
	{
		vertexOffset = vec3(1, 1, 0);
		geom_texCoord = vec2(1, 1);
	}
	vec3 particleLocation = vec3(particleLocations[int(3 * vert_particle_index)],
								 particleLocations[int(3 * vert_particle_index + 1)],
							 	 particleLocations[int(3 * vert_particle_index + 2)]);
	vec4 position = sys_modelViewMatrix * vec4(particleLocation + 0.5 * vertexOffset,1);
	geom_position = position.xyz;
	gl_Position = sys_projectionMatrix * position;
}
