#version 310 es
layout(rgba8,binding = 0) readonly uniform highp image2D inTexture;
layout(std430, binding = 1) buffer outWaveform {
    uint waveform[];
};
layout(location = 2) uniform int show_color;
layout (local_size_x = 64, local_size_y = 1, local_size_z = 1) in;
const float factor = 8.0;
const int lookupsize = 400;
const int lookupstep = 1;
const float intensity = 0.06;
const float thres = 0.002;
void main() {
    vec2 storePos = vec2(gl_GlobalInvocationID.xy);
    vec2 imgsize = vec2(imageSize(inTexture).xy);
    float s = storePos.y  / (imgsize.y/factor);
    float maxb = s+thres;
    float minb = s-thres;
    vec2 coords;
    vec3 col = vec3(0.0);
    int size = int(imgsize.y);
    for (int y = 0; y < size-y; y+=lookupstep){
        coords.x = storePos.x;
        coords.y = float(y);
        vec3  texcol = imageLoad(inTexture, ivec2(coords.xy)).rgb;
        if (show_color == 0)
        {
            col += vec3(intensity)*step(texcol, vec3(maxb))*step(vec3(minb), texcol);
        }
        if (show_color == 1)
        {
            float l = (texcol.r +texcol.g + texcol.b) / 3.;
            col += vec3(intensity)*step(l, maxb*maxb)*step(minb*minb, l);
            if(col.r >= 0.99 && col.b >= 0.99 && col.g >= 0.99)
            {
                col.r = 1.;
                col.g = 0.;
                col.b = 0.;
            }
        }
    }
    int pos =  (int(storePos.y * imgsize.x - imgsize.x) + int(imgsize.x -storePos.x));
    ivec4 bytes = ivec4(col * 255.,255);
    uint integerValue = (uint(bytes.a) << 24) | (uint(bytes.r) << 16) | (uint(bytes.g) << 8) | uint((bytes.b));
    waveform[pos] = integerValue;
}
