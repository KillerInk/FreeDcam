#version 300 es
#line 1
precision mediump float;

uniform sampler2D sTexture;
uniform sampler2D sTexture1;
out vec4 Output;
in vec2 texCoord;

void main() {
    vec2 texSize = texCoord.xy;
    vec4 c1 = texture(sTexture,texSize);
    vec4 c2 = texture(sTexture1,texSize);
    Output = mix(c1, c2, 0.5);
}