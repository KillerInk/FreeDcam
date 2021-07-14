#version 300 es
#line 1
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
uniform samplerExternalOES sTexture;
out vec4 Output;
in vec2 texCoord;

void main() {
    vec2 texSize = texCoord.xy;
    Output = texture(sTexture,texSize);
}