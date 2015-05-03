/*
 * Written for Cornell CS 5625 final project.
 * Daniel Carpenter, 5/3/2015
 */

#version 120

#define MAX_LIGHTS 40

const int XTOON_MATERIAL_ID = 4;

const int zmin = 15;
const int zmax = 400;

varying vec3 geom_position;
varying vec3 geom_normal;

uniform mat4 sys_projectionMatrix;

uniform bool mat_hasXToonTexture;
uniform sampler2D mat_xtoonTexture;

uniform bool spotLight_enabled;
uniform vec3 spotLight_eyePosition;
uniform int pointLight_count;
uniform vec3 pointLight_eyePosition[MAX_LIGHTS];

void main()
{
	//see http://dl.acm.org/citation.cfm?id=1124749
	vec2 texCoord;

	//first tex coordinate is based on angle of light (traditional toon shading)
	vec3 l;
	if (!spotLight_enabled) {
		vec3 lightPosition;
		for (int i=0; i<pointLight_count; i++) {
			lightPosition += pointLight_eyePosition[i];
		}
		
		lightPosition = pointLight_eyePosition[0];
		l = lightPosition - geom_position;
	} else {
		vec3 l = spotLight_eyePosition - geom_position;
	}
	
	vec3 n = normalize(geom_normal);
	l = normalize(l);
	
	texCoord.x = max(dot(n,l), 0);
	
	//second coordinate is based on distance from view
	float z = (sys_projectionMatrix * vec4(geom_position, 1)).z;
	texCoord.y = 1-log(z/zmin)/log(zmax/zmin);
	
	/*
	//need to set texture to clamp so we don't have this mess:
	if(texCoord.y >= 0.95)
		texCoord.y = 0.95;
		*/
	
	//now we can get the tex coordinate
	vec4 color = vec4(0.2, 0.6, 0.6, 1.0);
	if(mat_hasXToonTexture) {
		color = texture2D(mat_xtoonTexture, texCoord);
	}
	
	//color = vec4(texCoord.y, texCoord.y, texCoord.y, 1.0);
	
	// Encoding: (matID, normal[3], color[4], position[3], 0[5])
	gl_FragData[0] = vec4(float(XTOON_MATERIAL_ID), geom_normal);
	gl_FragData[1] = color;
	gl_FragData[2] = vec4(geom_position, 0.0);
	gl_FragData[3] = vec4(0.0, 0.0, 0.0, 0.0);
}	
