#version 310 es
#line 1
in vec2 vPosition;
in vec2 vTexCoord;
out vec2 texCoord;


void main() {
    texCoord.xy = vTexCoord.xy;
    gl_Position = vec4 (vPosition.x, vPosition.y, 0.0, 1.0);
}