#version 300 es
#line 1
precision mediump float;

uniform sampler2D sTexture;
uniform sampler2D sTexture1;
out vec4 Output;
in vec2 texCoord;

void main() {
    ivec2 size  = textureSize(sTexture, 0);
    vec4 c1 = texelFetch(sTexture, ivec2(texCoord * vec2(size.x,size.y)), 0).rgba;
    vec4 c2 = texelFetch(sTexture1, ivec2(texCoord * vec2(size.x,size.y)), 0).rgba;
    Output = mix(c1, c2, 0.5);
}