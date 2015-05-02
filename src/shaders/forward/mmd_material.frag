#version 120

#define MAX_LIGHTS 40	

#define MULTIPLY_SPHERE_MAP 1
#define ADD_SPHERE_MAP 2

varying vec3 geom_position;
varying vec3 geom_normal;
varying vec2 geom_texCoord;

uniform int light_count;
uniform vec3 light_eyePosition[MAX_LIGHTS];
uniform vec3 light_attenuation[MAX_LIGHTS];
uniform vec3 light_color[MAX_LIGHTS];

uniform vec3 mat_ambient;
uniform vec4 mat_diffuse;
uniform vec3 mat_specular;
uniform float mat_shininess;

uniform bool mat_useTexture;
uniform sampler2D mat_texture;

uniform bool mat_useToonTexture;
uniform sampler2D mat_toonTexture;

uniform bool mat_useSphereMap;
uniform sampler2D mat_sphereMapTexture;
uniform int mat_sphereMapMode;

uniform mat4 sys_viewMatrix;

varying vec4 geom_color;

void main()
{		
	vec4 result = vec4(0,0,0, mat_diffuse.a);
	vec3 n = normalize(geom_normal);	

	vec3 baseColor = 0.5*mat_ambient;
	vec4 textureColor = vec4(1);	
	if (mat_useTexture) {
		textureColor = texture2D(mat_texture, geom_texCoord);		
		baseColor *= textureColor.xyz;
		result.a = mat_diffuse.a * textureColor.a;
	}
	
	for (int i=0;i<light_count;i++) {
		vec3 v = -normalize(geom_position);
		vec3 l = light_eyePosition[i] - geom_position;
		float d = sqrt(dot(l,l));
		l = normalize(l);
		float attenuation = dot(light_attenuation[i], vec3(1, d, d*d));
		vec3 I = light_color[i] / attenuation;
		vec3 color = baseColor*I;
		
		float dotProd = dot(n,l);
		if (mat_useToonTexture)
		{		
			vec4 toonColor = texture2D(mat_toonTexture, vec2(0.5,0.5+0.5*dotProd));			
			color += 0.7*mat_diffuse.xyz*textureColor.xyz*toonColor.xyz*I;
		} else {
			color += 0.7*mat_diffuse.xyz*max(0,dotProd)*textureColor.xyz*I;
			//color += 0.5*mat_diffuse.xyz*textureColor.xyz*I;
		}
		
		vec3 h = normalize(l + v);
		vec3 specular = pow(max(0.00001, dot(h,n)), mat_shininess) * mat_specular * I;
		color.rgb += specular;

		result.rgb += color.rgb;		
	}

	if (mat_useSphereMap)
	{
		vec2 t = normalize(mat3(transpose(sys_viewMatrix)) * n).xy;
		t.x = t.x*0.5 + 0.5;
		t.y = t.y*0.5 + 0.5;
		vec4 sphereMapColor = texture2D(mat_sphereMapTexture, t);
		if (mat_sphereMapMode == MULTIPLY_SPHERE_MAP)
		{
			result.rgb *= sphereMapColor.rgb;
		}
		else if (mat_sphereMapMode == ADD_SPHERE_MAP)
		{
			result.rgb += sphereMapColor.rgb;
		}
	}

	gl_FragColor = result;
	//gl_FragColor = geom_color;
}