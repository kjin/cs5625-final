/*
 * Written for Cornell CS 5625 final project.
 * Daniel Carpenter, 5/5/2015
 */

#version 120

attribute vec3 vert_position;
attribute vec2 vert_texCoord;

varying vec2 geom_texCoord;

void main()
{
	gl_Position = vec4(vert_position,1);
	geom_texCoord = vert_texCoord;
}