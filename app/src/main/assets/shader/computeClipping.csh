#version 310 es

layout(rgba8,binding = 0) readonly uniform highp image2D inTexture;
layout(rgba8,binding = 1)  writeonly uniform highp image2D outTexture;

layout(location = 2) uniform float float_position;

layout(location = 3) uniform float zebra_high;
layout(location = 4) uniform float zebra_low;

layout (local_size_x = 4, local_size_y = 4, local_size_z = 1) in;

vec4 getZebra(ivec2 texS, vec4 color, float pos,float high,float low)
{
    vec4 out_color;
    float gray = (color.r + color.g +color.b) /3.0;
    if(gray > (1.0 - high))
        out_color = mix(color,vec4(1.0, 0.0, 0.0, 1.0),gray);
    else if(gray < low)
        out_color =  mix(color,vec4(0.0, 0.4, 1.0, 1.0),1.0-gray);
    else
        out_color = color;
    return out_color;
}

void main() {
    ivec2 storePos = ivec2(gl_GlobalInvocationID.xy);
    vec4 texColor = imageLoad(inTexture,storePos).rgba;
    vec4 out_color = getZebra(storePos,texColor,float_position,zebra_high,zebra_low);
    imageStore(outTexture, storePos, out_color);
}