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
precision mediump float;
uniform samplerExternalOES sTexture;
varying vec2 texCoord;
#END_GLES

void main() {
    vec2 texSize = texCoord.xy;
#GLES3
    Output = texture(sTexture,texSize);
#END_GLES
#GLES2
    gl_FragColor = texture2D(sTexture,texSize);
#END_GLES
}