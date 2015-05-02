#version 120

varying vec3 geom_position;
varying vec3 geom_normal;

uniform mat3 mat_cameraToCubeMap;
uniform mat3 mat_normalCameraToCubeMap;
uniform samplerCube mat_cubeMap;

void main()
{	
	vec3 n = normalize(mat_normalCameraToCubeMap*normalize(geom_normal));
	vec3 v = normalize(mat_cameraToCubeMap*(normalize(geom_position)));
	vec3 r = reflect(v,n);
	vec3 sampledColor = textureCube(mat_cubeMap, r).xyz;
	gl_FragColor = vec4(sampledColor, 1.0);
}
