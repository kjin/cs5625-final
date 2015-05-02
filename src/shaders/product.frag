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

varying vec2 geom_texCoord;
uniform sampler2DRect texture0;
uniform sampler2DRect texture1;

void main() {
	gl_FragColor = texture2DRect(texture0, geom_texCoord)*texture2DRect(texture1, geom_texCoord);
}