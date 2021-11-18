#version 310 es

layout(rgba8,binding = 0) readonly uniform highp image2D inTexture;
layout(rgba8,binding = 1)  writeonly uniform highp image2D outTexture;

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
void main() {
    ivec2 storePos = ivec2(gl_GlobalInvocationID.xy);
    vec4 texColor = imageLoad(inTexture,storePos).rgba;
    imageStore(outTexture, storePos, texColor);
}
