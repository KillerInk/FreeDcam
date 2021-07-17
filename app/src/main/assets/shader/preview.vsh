#version 300 es
#line 1
in vec2 vPosition;
in vec2 vTexCoord;
out vec2 texCoord;

uniform mat4 uTexRotateMatrix;
void main() {
    texCoord.xy  = vTexCoord.xy;
    texCoord.y  = 1.0-vTexCoord.y;
    gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );
}