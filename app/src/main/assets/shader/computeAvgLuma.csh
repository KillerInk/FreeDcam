#version 310 es
layout(rgba8,binding = 0) readonly uniform highp image2D inTexture;
layout(std430, binding = 1) buffer avgLuma {
    uint luma[];
};
layout (local_size_x = 16, local_size_y = 16, local_size_z = 1) in;
void main() {
    ivec2 storePos = ivec2(gl_GlobalInvocationID.xy);
    ivec2 imgsize = imageSize(inTexture).xy;
    if (storePos.x < imgsize.x && storePos.y < imgsize.y) {
        vec4 texColor = imageLoad(inTexture, storePos).rgba;
        uint red = uint(texColor.r * 255.0);
        uint green = uint(texColor.g * 255.0);
        uint blue = uint(texColor.b * 255.0);
        float lum = ((0.21260 * float(red)) + (0.71520 * float(green)) + (0.0722 * float(blue)));
        lum = lum / float(imgsize.x * imgsize.y);
        uint l = uint(lum * 1000000.) ;
        atomicAdd(luma[0], l);
    }

}
