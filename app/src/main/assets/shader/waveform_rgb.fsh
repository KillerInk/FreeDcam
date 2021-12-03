#version 300 es
#line 1
precision mediump float;
uniform sampler2D sTexture;
out vec4 Output;
in vec2 texCoord;
const float factor = 3.0;
const int lookupsize = 200;
const int lookupstep = 3;
const float intensity = 0.09;
const float thres = 0.006;
uniform int show_color;

void main()
{
    vec2 oResolution = vec2(textureSize(sTexture, 0));
    vec2 onePixel = vec2(1.0, 1.0) / oResolution.xy;
    float s = (texCoord.y)*factor;
    float maxb = s+thres;
    float minb = s-thres;
    vec2 coords;
//    float u = (texCoord.y / (oResolution.y * onePixel.y)) * factor;
    vec3 col = vec3(0.0);
    int size = lookupsize * int(factor);
    for (int y = 0; y < size; y+=lookupstep){
        coords.x = texCoord.x;
        coords.y = texCoord.y/factor + onePixel.y * float(y);
        vec3  texcol = texture(sTexture, coords).rgb;
        if(show_color == 0 || show_color == 1)
        {
            col += vec3(intensity)*step(texcol, vec3(maxb))*step(vec3(minb), texcol);
        }
        if (show_color == 2 || show_color == 0)
        {
            float l = dot(texcol, texcol)/factor;
            col += vec3(intensity)*step(l, maxb*maxb)*step(minb*minb, l);
        }

    }
    if(show_color == 2 && col.r >= 0.97 && col.b >= 0.97 && col.g >= 0.97)
    {
        col.r = 1.;
        col.g = 0.;
        col.b = 0.;
    }
    Output = vec4(col.b,col.g,col.r,1.0);
}