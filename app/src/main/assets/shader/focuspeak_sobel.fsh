#GLES3
#version 300 es
#line 1
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
uniform samplerExternalOES sTexture;
out vec4 Output;
in vec2 texCoord;
#END_GLES

#GLES2
#extension GL_OES_EGL_image_external : require
#extension GL_OES_standard_derivatives : enable
precision mediump float;
uniform mediump samplerExternalOES sTexture;
varying mediump vec2 texCoord;
uniform vec2 texRes;
#END_GLES

uniform vec4 peak_color;
uniform float peak_strength;
//test
void main()
{
#GLES3
    vec4 color =  texture(sTexture, texCoord);
    vec2 texSize = vec2(textureSize(sTexture, 0));
#END_GLES
#GLES2
    vec4 color =  texture2D(sTexture, texCoord);
    vec2 texSize =  texRes;
#END_GLES

    float w = 1.0 / texSize.x;
    float h = 1.0 / texSize.y;

    vec4 n[9];
#GLES3
    n[0] = texture(sTexture, texCoord + vec2( -w, -h));
    n[1] = texture(sTexture, texCoord + vec2(0.0, -h));
    n[2] = texture(sTexture, texCoord + vec2(  w, -h));
    n[3] = texture(sTexture, texCoord + vec2( -w, 0.0));
    n[4] = texture(sTexture, texCoord);
    n[5] = texture(sTexture, texCoord + vec2(  w, 0.0));
    n[6] = texture(sTexture, texCoord + vec2( -w, h));
    n[7] = texture(sTexture, texCoord + vec2(0.0, h));
    n[8] = texture(sTexture, texCoord + vec2(  w, h));
#END_GLES
#GLES2
    n[0] = texture2D(sTexture, texCoord + vec2( -w, -h));
    n[1] = texture2D(sTexture, texCoord + vec2(0.0, -h));
    n[2] = texture2D(sTexture, texCoord + vec2(  w, -h));
    n[3] = texture2D(sTexture, texCoord + vec2( -w, 0.0));
    n[4] = texture2D(sTexture, texCoord);
    n[5] = texture2D(sTexture, texCoord + vec2(  w, 0.0));
    n[6] = texture2D(sTexture, texCoord + vec2( -w, h));
    n[7] = texture2D(sTexture, texCoord + vec2(0.0, h));
    n[8] = texture2D(sTexture, texCoord + vec2(  w, h));
#END_GLES

    vec4 sobel_edge_h = n[2] + (peak_strength*n[5]) + n[8] - (n[0] + (peak_strength*n[3]) + n[6]);
    vec4 sobel_edge_v = n[0] + (peak_strength*n[1]) + n[2] - (n[6] + (peak_strength*n[7]) + n[8]);
    vec4 sobel = sqrt(((sobel_edge_h * sobel_edge_h) + (sobel_edge_v * sobel_edge_v)));
    if(sobel == min(sobel, vec4(0.3,0.3,0.3,1)))
        sobel = vec4(0,0,0,1);
#END_GLES
    Output = mix(color, peak_color, sobel);
#GLES2
    gl_FragColor = mix(color, peak_color, sobel);
#END_GLES
}