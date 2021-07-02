#version 300 es
#line 1
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

uniform samplerExternalOES sTexture;
out vec4 Output;
in vec2 texCoord;

uniform float float_position;
uniform float zebra_high;
uniform float zebra_low;
uniform bool show_zebra;
uniform bool show_focuspeak;

uniform vec4 peak_color;
uniform float peak_strength;

vec4 getZebra(vec2 texS, vec4 color, float pos,float high,float low)
{
    vec4 out_color;
    if(fract((texS.x + texS.y)*(20.0+pos)) > 0.9)
    {
        float gray = (color.r + color.g +color.b) /3.0;
        if(gray > (1.0 - high))
        out_color = mix(color,vec4(1.0, 0.0, 0.0, 1.0),gray);
        else if(gray < low)
        out_color =  mix(color,vec4(0.0, 0.4, 1.0, 1.0),1.0-gray);
        else
        out_color = color;
    }
    else if(fract((texS.x - texS.y)*(20.0+pos)) > 0.9)
    {
        float gray = (color.r + color.g +color.b) /3.0;
        if(gray > (1.0 - high))
        out_color = mix(color,vec4(1.0, 0.0, 0.0, 1.0),gray);
        else if(gray < low)
        out_color =  mix(color,vec4(0.0, 0.4, 1.0, 1.0),1.0-gray);
        else
        out_color = color;
    }
    else
    out_color = color;
    return out_color;
}

void main() {
    vec2 texSize = texCoord.xy;
    vec4 color = texture(sTexture,texSize);
    vec4 out_color = color;
    if (show_zebra)
        out_color = getZebra(texSize, color,float_position,zebra_high,zebra_low);

    if(show_focuspeak)
    {
        vec2 size = vec2(textureSize(sTexture, 0));
        float w = 1.0 / size.x;
        float h = 1.0 / size.y;
        vec4 n[9];
        n[0] = texture(sTexture, texCoord + vec2( -w, -h));
        n[1] = texture(sTexture, texCoord + vec2(0.0, -h));
        n[2] = texture(sTexture, texCoord + vec2(  w, -h));
        n[3] = texture(sTexture, texCoord + vec2( -w, 0.0));
        n[4] = texture(sTexture, texCoord);
        n[5] = texture(sTexture, texCoord + vec2(  w, 0.0));
        n[6] = texture(sTexture, texCoord + vec2( -w, h));
        n[7] = texture(sTexture, texCoord + vec2(0.0, h));
        n[8] = texture(sTexture, texCoord + vec2(  w, h));

        vec4 sobel_edge_h = n[2] + (peak_strength*n[5]) + n[8] - (n[0] + (peak_strength*n[3]) + n[6]);
        vec4 sobel_edge_v = n[0] + (peak_strength*n[1]) + n[2] - (n[6] + (peak_strength*n[7]) + n[8]);
        vec4 sobel = sqrt(((sobel_edge_h * sobel_edge_h) + (sobel_edge_v * sobel_edge_v)));
        if(sobel == min(sobel, vec4(0.3,0.3,0.3,1)))
            sobel = vec4(0,0,0,1);
        out_color = mix(out_color, peak_color, sobel);
    }

    Output = out_color;
}