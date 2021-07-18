#version 300 es
#line 1
precision mediump float;

uniform sampler2D sTexture;
out vec4 Output;
in vec2 texCoord;

void main() {
    vec2 texSize = texCoord.xy;
    Output = texture(sTexture,texSize);
}