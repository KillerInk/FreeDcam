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
layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
void main() {
    ivec2 storePos = ivec2(gl_GlobalInvocationID.xy);
    ivec2 imgsize = imageSize(inTexture).xy;
    if (storePos.x < imgsize.x && storePos.y < imgsize.y) {
        vec4 texColor = imageLoad(inTexture, storePos).rgba;
        uint red = uint(texColor.r * 255.0);
        uint green = uint(texColor.g * 255.0);
        uint blue = uint(texColor.b * 255.0);
        atomicAdd(reds[red], 1u);
        atomicAdd(greens[green], 1u);
        atomicAdd(blues[blue], 1u);
    }
}
