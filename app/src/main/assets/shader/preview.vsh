#version 300 es
#line 1
in vec2 vPosition;
in vec2 vTexCoord;
out vec2 texCoord;

uniform mat4 uTexRotateMatrix;
void main() {
    texCoord.yx  = vTexCoord.xy;
    gl_Position = uTexRotateMatrix * vec4(vPosition.x, vPosition.y, 0.0, 1.0);
}