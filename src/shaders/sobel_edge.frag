/*
 * Written for Cornell CS 5625 final project.
 * Daniel Carpenter, 5/5/2015
 */

#version 120
#extension GL_ARB_texture_rectangle : enable

#define SQRT_2PI 2.50662827463

varying vec2 geom_texCoord;
uniform sampler2DRect texture;

uniform int offset;
uniform float contrast;
uniform float threshold;

void main() {
	
	//see https://udn.epicgames.com/Three/DevelopmentKitGemsSobelEdgeDetection.html#Sobel Edge Detection Post Process Effect
	vec4 uv02 = texture2DRect(texture, geom_texCoord + vec2(offset,-offset));
	vec4 uv12 = texture2DRect(texture, geom_texCoord + vec2(offset,0));
	vec4 uv22 = texture2DRect(texture, geom_texCoord + vec2(offset,offset));
	vec4 uv01 = texture2DRect(texture, geom_texCoord + vec2(0,-offset));
	vec4 uv21 = texture2DRect(texture, geom_texCoord + vec2(0,offset));
	vec4 uv00 = texture2DRect(texture, geom_texCoord + vec2(-offset,-offset));
	vec4 uv10 = texture2DRect(texture, geom_texCoord + vec2(-offset, 0));
	vec4 uv20 = texture2DRect(texture, geom_texCoord + vec2(-offset,offset));
	
	float x = length(-uv00 - 2*uv01 - uv02 + uv20 + 2*uv21 + uv22);
	float y = length(-uv00 + uv02 - 2*uv10 + 2*uv12 - uv20 + uv22);
	
	float d = x*x + y*y;
	d = pow(d, contrast);
	
	//is it an edge?
	if(d < threshold) {
		gl_FragColor = texture2DRect(texture, geom_texCoord);
	} else {
		gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
	}
	
	//gl_FragColor = vec4(1-d,1-d,1-d,1.0);
}