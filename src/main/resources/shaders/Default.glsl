#type GL_VERTEX_SHADER
#version 330 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoords;
layout(location = 3) in float aTexID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec2 fPos;
out vec4 fColor;
out vec2 fTexCoords;
out float fTexID;

void main() {
	fPos = aPos.xy;
	fColor = aColor;
	fTexCoords = aTexCoords;
	fTexID = aTexID;
	gl_Position = uProjection * uView * uvec4(aPos, 1.0);
}
	
#type GL_FRAGMENT_SHADER
#version 330 core

in vec2 fPos;
in vec4 fColor;
in vec2 fTexCoords;
in float fTexID;

#define MAX_LIGHTS 128

uniform sampler2D textures[8];
uniform float timeElapsed, fadeTime;
//Dynamic lighting.
uniform bool lighting;
uniform int lightCount;
uniform vec2 lights[MAX_LIGHTS];
uniform float radius, intensity;

out vec4 color;

vec4 fade() {
	if (timeElapsed < fadeTime) {
		return color * (timeElapsed / fadeTime);
	}
	return color;
}

float dynamicLighting(vec2 pos) {
	float distance = distance(fPos, pos);
	if (distance <= radius) {
		float radius2 = radius / 2;
		float distance2 = distance - radius2;
		if (distance2 > 0) {
			float brightness = distance2 / radius2 * (intensity);
			return brightness;
		} else {
			return 0;
		}
	} else {
		return intensity;
	}
}

void main() {
	int id = int(fTexID);
	if (id > 0) {
		color = fColor * texture(textures[id], fTexCoords);
		//        color = vec4(fTexCoords, 0, 1);
	} else {
		color = fColor;
	}
	
	vec3 myColor = color.rgb - intensity;
	
	if (lighting) {
		for (int i = 0; i < lightCount; i++) {
			float dl = dynamicLighting(lights[i]);
			if (dl != intensity) {
				myColor = color.rgb - dl;
			}
		}
	}
	color.rgb = myColor;
	
	color = fade();
}