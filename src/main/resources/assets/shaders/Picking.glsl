#type vertex
#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in float aTexID;
layout (location = 4) in float aEntityID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexID;
out float fEntityID;

void main() {
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexID = aTexID;
    fEntityID = aEntityID;

    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
in float fTexID;
in float fEntityID;

uniform sampler2D uTextures[8];

out vec3 color;

void main() {
    vec4 texColor = vec4( 1, 1, 1, 1);
    if (fTexID > 0) {
        int id = int(fTexID);
        texColor = fColor * texture(uTextures[id], fTexCoords);
    }

    if (texColor.a < 0.5) {
        discard;
    }

    color = vec3(fEntityID, fEntityID, fEntityID);
}