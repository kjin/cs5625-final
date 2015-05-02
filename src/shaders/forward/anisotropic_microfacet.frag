/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 * Copyright (c) 2015, Department of Computer Science, Cornell University.
 * 
 * This code repository has been authored collectively by:
 * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488), 
 * Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

#version 120

#define MAX_LIGHTS 40

const float PI = 3.14159265358979323846264;

varying vec3 geom_position;
varying vec2 geom_texCoord;
varying vec3 geom_normal;
varying vec3 geom_tangent;
varying vec3 geom_bitangent;

uniform vec4 mat_diffuseColor;
uniform bool mat_hasDiffuseTexture;
uniform sampler2D mat_diffuseTexture;

uniform float mat_indexOfRefraction;

uniform float mat_alphaX;
uniform bool mat_hasAlphaXTexture;
uniform sampler2D mat_alphaXTexture;

uniform float mat_alphaY;
uniform bool mat_hasAlphaYTexture;
uniform sampler2D mat_alphaYTexture;

uniform int light_count;
uniform vec3 light_eyePosition[MAX_LIGHTS];
uniform vec3 light_attenuation[MAX_LIGHTS];
uniform vec3 light_color[MAX_LIGHTS];

float fresnel(vec3 i, vec3 m, float eta) {
	float c = abs(dot(i,m));
	float g = sqrt(eta*eta - 1 + c*c);

	float gmc = g-c;
	float gpc = g+c;
	float nom = c*(g+c)-1;
	float denom = c*(g-c)+1;
	return 0.5*gmc*gmc/gpc/gpc*(1 + nom*nom/denom/denom);
}

float G1(vec3 v, vec3 m, vec3 n, float alpha) {
	float vm = dot(v,m);
	float vn = dot(v,n);
	if (vm*vn > 0) {		
		float cosThetaV = dot(n,v);
		float sinThetaV2 = 1 - cosThetaV*cosThetaV;
		float tanThetaV2 = sinThetaV2 / cosThetaV / cosThetaV;
		return 2 / (1 + sqrt(1 + alpha*alpha*tanThetaV2));
		/*
		float cosThetaV = dot(n,v);
		float sinThetaV = sqrt(1 - cosThetaV*cosThetaV);
		float tanThetaV = sinThetaV / cosThetaV;		
		float a = 1 / (alpha * tanThetaV);
		if (a < 1.6) {
			return (3.535*a + 2.181*a*a) / (1+2.276*a+2.577*a*a);
		} else {
			return 1;
		}
		*/
	} else {
		return 0;
	}
}

float D(vec3 m, vec3 n, vec3 t, vec3 b, float alphaX, float alphaY) {	
	float mn = dot(m,n);
	if (alphaX <= 0 || alphaY <= 0)
		return 0;
	if (mn > 0) {
		float cosThetaM = mn;
		float cosThetaM2 = cosThetaM*cosThetaM;
		float x = dot(m,t);
		float y = dot(m,b);
		float beckmannExponent = ((x*x) / (alphaX*alphaX)
				+ (y*y) / (alphaY*alphaY)) / cosThetaM2;			
		float root = (1 + beckmannExponent) * cosThetaM2;
		float result = 1 / (PI * alphaX * alphaY * root * root);
		return result;
		/*	
		float alpha = sqrt(cosPhiH*cosPhiH*alphaX*alphaX + sinPhiH*sinPhiH*alphaY*alphaY);
		float tanThetaM2 = (1 - cosThetaM2) / cosThetaM2;
		float cosThetaM4 =  cosThetaM*cosThetaM*cosThetaM*cosThetaM;
		float X = (alpha*alpha + tanThetaM2);
		return alpha*alpha / (PI * cosThetaM4 * X * X);
		*/
		/*
		float cosThetaM = mn;
		float cosThetaM2 = cosThetaM*cosThetaM;
		float tanThetaM2 = (1 - cosThetaM2) / cosThetaM2;
		float cosThetaM4 =  cosThetaM*cosThetaM*cosThetaM*cosThetaM;
		return exp(-tanThetaM2 / alpha / alpha) / (PI * alpha*alpha*cosThetaM4);
		*/
	} else {
		return 0;
	}
}

void main()
{
	vec4 diffuse = mat_diffuseColor;
	if (mat_hasDiffuseTexture) {
		vec4 tex = texture2D(mat_diffuseTexture, geom_texCoord);
		diffuse = diffuse * tex;
	}
	float eta = mat_indexOfRefraction;

	float alphaX = mat_alphaX;
	if (mat_hasAlphaXTexture) {
		alphaX *= texture2D(mat_alphaXTexture, geom_texCoord).x;
	}
	float alphaY = mat_alphaY;
	if (mat_hasAlphaYTexture) {
		alphaY *= texture2D(mat_alphaYTexture, geom_texCoord).x;
	}

	vec3 n = normalize(geom_normal);
	vec3 t = normalize(geom_tangent);
	t = normalize(t - dot(n,t)*n);
	vec3 b = normalize(cross(n, t));
	if (dot(b, geom_bitangent) < 0)
		b *= -1;		
	if (dot(b, geom_bitangent) < 0)
		t *= -1;

	vec4 result = vec4(0,0,0,diffuse.a);		
	vec3 o = -normalize(geom_position);
	float odotn = dot(o,n);
	if (odotn > 0) {			
		for (int k=0; k<light_count; k++) {
		//for (int k=0; k<1; k++) {		
			vec3 i = light_eyePosition[k] - geom_position;
			float d = length(i);
			i = normalize(i);
			vec3 m = normalize(i + o);		
			float attenuation = dot(light_attenuation[k], vec3(1, d, d*d));
			vec3 I = light_color[k] / attenuation;

			float idotn = dot(i,n);
			if (idotn <= 0)
				continue;

			result.xyz += (idotn * diffuse.xyz)*I;			

			float idotm = dot(i,m);
			float F = (idotm > 0) ? fresnel(i,m,eta) : 0;
			
			float cosPhiH = dot(m, t);			
			float sinPhiH = dot(m, b);
			float vecSize = sqrt(cosPhiH*cosPhiH + sinPhiH*sinPhiH);
			cosPhiH /= vecSize;
			sinPhiH /= vecSize;
			float alpha = sqrt(cosPhiH*cosPhiH*alphaX*alphaX + sinPhiH*sinPhiH*alphaY*alphaY);
						
			float G = G1(i,m,n,alpha) * G1(o,m,n,alpha);

			result.xyz += (F * G * D(m,n,t,b, alphaX,alphaY)) * I / (4*idotn*odotn);				
		}
	}	
		
	gl_FragColor = result;
}
