#version 310 es
#line 1
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
uniform samplerExternalOES sTexture;
out vec4 Output;
in vec2 texCoord;

void main() {
    ivec2 size  = textureSize(sTexture, 0);
    Output = texelFetch(sTexture, ivec2(texCoord * vec2(size.x,size.y)), 0).rgba;
}