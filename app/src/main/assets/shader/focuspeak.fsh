#version 300 es
#line 1
precision mediump float;

uniform sampler2D sTexture;
out vec4 Output;
in vec2 texCoord;

uniform vec4 peak_color;
uniform float peak_strength;

void main() {
    vec2 size = vec2(textureSize(sTexture, 0));
    vec2 texSize = texCoord.xy;
    vec4 color = texture(sTexture,texSize);
    vec4 out_color = color;
    float w = 1.0 / size.x;
    float h = 1.0 / size.y;
    vec4 n[9];
    n[0] = texelFetch(sTexture, ivec2((texCoord + vec2( -w, -h)) * vec2(size.x,size.y)), 0).rgba;
    n[1] = texelFetch(sTexture, ivec2((texCoord + vec2(0.0, -h)) * vec2(size.x,size.y)), 0).rgba;
    n[2] = texelFetch(sTexture, ivec2((texCoord + vec2(  w, -h)) * vec2(size.x,size.y)), 0).rgba;
    n[3] = texelFetch(sTexture, ivec2((texCoord + vec2( -w, 0.0)) * vec2(size.x,size.y)), 0).rgba;
    n[4] = texelFetch(sTexture, ivec2(texCoord * vec2(size.x,size.y)), 0).rgba;
    n[5] = texelFetch(sTexture, ivec2((texCoord + vec2(  w, 0.0)) * vec2(size.x,size.y)), 0).rgba;
    n[6] = texelFetch(sTexture, ivec2((texCoord + vec2( -w, h)) * vec2(size.x,size.y)), 0).rgba;
    n[7] = texelFetch(sTexture, ivec2((texCoord + vec2(0.0, h)) * vec2(size.x,size.y)), 0).rgba;
    n[8] = texelFetch(sTexture, ivec2((texCoord + vec2(w, h)) * vec2(size.x,size.y)), 0).rgba;

    vec4 sobel_edge_h = n[2] + (peak_strength*n[5]) + n[8] - (n[0] + (peak_strength*n[3]) + n[6]);
    vec4 sobel_edge_v = n[0] + (peak_strength*n[1]) + n[2] - (n[6] + (peak_strength*n[7]) + n[8]);
    vec4 sobel = sqrt(((sobel_edge_h * sobel_edge_h) + (sobel_edge_v * sobel_edge_v)));
    if(sobel == min(sobel, vec4(0.3,0.3,0.3,1)))
        sobel = vec4(0,0,0,1);
    Output = mix(out_color, peak_color, sobel);
}