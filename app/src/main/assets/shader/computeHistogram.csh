#version 310 es
layout(rgba8,binding = 0) readonly uniform highp image2D inTexture;
layout(std430, binding = 1) buffer histogramRed {
    uint reds[];
};
layout(std430, binding = 2) buffer histogramGreen {
    uint greens[];
};
layout(std430, binding = 3) buffer histogramBlue {
    uint blues[];
};
shared uint histogramSharedRed[256];
shared uint histogramSharedGreen[256];
shared uint histogramSharedBlue[256];
layout (local_size_x = 16, local_size_y = 16, local_size_z = 1) in;
void main() {
    histogramSharedRed[gl_LocalInvocationIndex] = 0u;
    histogramSharedGreen[gl_LocalInvocationIndex] = 0u;
    histogramSharedBlue[gl_LocalInvocationIndex] = 0u;
    barrier();
    ivec2 storePos = ivec2(gl_GlobalInvocationID.xy);
    ivec2 imgsize = imageSize(inTexture).xy;
    if (storePos.x < imgsize.x && storePos.y < imgsize.y) {
        vec4 texColor = imageLoad(inTexture, storePos).rgba;
        uint red = uint(texColor.r * 255.0);
        uint green = uint(texColor.g * 255.0);
        uint blue = uint(texColor.b * 255.0);
        atomicAdd(histogramSharedRed[red], 1u);
        atomicAdd(histogramSharedGreen[green], 1u);
        atomicAdd(histogramSharedBlue[blue], 1u);
        barrier();
        atomicAdd(reds[gl_LocalInvocationIndex], histogramSharedRed[gl_LocalInvocationIndex]);
        atomicAdd(greens[gl_LocalInvocationIndex], histogramSharedGreen[gl_LocalInvocationIndex]);
        atomicAdd(blues[gl_LocalInvocationIndex], histogramSharedBlue[gl_LocalInvocationIndex]);
    }

}
