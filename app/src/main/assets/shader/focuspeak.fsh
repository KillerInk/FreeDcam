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
#END_GLES

uniform vec4 peak_color;
uniform float peak_strength;
void main() {

    vec4 color = texture(sTexture, texCoord);
    float gray = length(color.rgb);

    float focusPeak = step(peak_strength, length(vec2(dFdx(gray), dFdy(gray))));

#GLES2
    gl_FragColor = mix(color, peak_color, focusPeak);
#END_GLES
#GLES3
    Output = mix(color,peak_color, focusPeak);
#END_GLES
}