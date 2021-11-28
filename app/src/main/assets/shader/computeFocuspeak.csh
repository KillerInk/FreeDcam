#version 310 es

layout(rgba8,binding = 0) readonly uniform highp image2D inTexture;
layout(rgba8,binding = 1)  writeonly uniform highp image2D outTexture;
layout(location = 2) uniform vec4 peak_color;
layout(location = 3) uniform float peak_strength;

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;

void main() {
    ivec2 storePos = ivec2(gl_GlobalInvocationID.xy);
    vec4 texColor = imageLoad(inTexture,storePos).rgba;
    vec4 n[9];
    n[0] = imageLoad(inTexture,storePos + ivec2( -1, -1)).rgba;
    n[1] = imageLoad(inTexture,storePos + ivec2( 0, -1)).rgba;
    n[2] = imageLoad(inTexture,storePos + ivec2( 1, -1)).rgba;
    n[3] = imageLoad(inTexture,storePos + ivec2( -1, 0)).rgba;
    n[4] = imageLoad(inTexture,storePos + ivec2( 0, 0)).rgba;
    n[5] = imageLoad(inTexture,storePos + ivec2( 1, 0)).rgba;
    n[6] = imageLoad(inTexture,storePos + ivec2( -1, 1)).rgba;
    n[7] = imageLoad(inTexture,storePos + ivec2( 0, 1)).rgba;
    n[8] = imageLoad(inTexture,storePos + ivec2( 1, 1)).rgba;

    vec4 sobel_edge_h = n[2] + (peak_strength*n[5]) + n[8] - (n[0] + (peak_strength*n[3]) + n[6]);
    vec4 sobel_edge_v = n[0] + (peak_strength*n[1]) + n[2] - (n[6] + (peak_strength*n[7]) + n[8]);
    vec4 sobel = sqrt(((sobel_edge_h * sobel_edge_h) + (sobel_edge_v * sobel_edge_v)));
    if(sobel == min(sobel, vec4(0.3,0.3,0.3,1.0)))
        sobel = vec4(0,0,0,1.0);
    texColor = mix(texColor, peak_color, sobel);
    imageStore(outTexture, storePos, texColor);
}