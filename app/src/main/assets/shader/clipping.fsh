#version 300 es
#line 1
precision mediump float;

uniform sampler2D sTexture;
out vec4 Output;
in vec2 texCoord;

uniform float float_position;
uniform float zebra_high;
uniform float zebra_low;

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
    ivec2 size  = textureSize(sTexture, 0);
    vec4 color = texelFetch(sTexture, ivec2(texCoord * vec2(size.x,size.y)), 0).rgba;
    Output = getZebra(texCoord, color,float_position,zebra_high,zebra_low);
}