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

uniform float float_position;

#IMPORT shader/zebramethod.fsh

void main() {
    vec2 texSize = texCoord.xy;
#GLES3
    vec4 color = texture(sTexture,texSize);
#END_GLES
#GLES2
    vec4 color = texture2D(sTexture,texSize);
#END_GLES

    vec4 out_color = getZebra(texSize, color,float_position);

#GLES3
    Output = out_color;
#END_GLES

#GLES2
    gl_FragColor = out_color;
#END_GLES
}