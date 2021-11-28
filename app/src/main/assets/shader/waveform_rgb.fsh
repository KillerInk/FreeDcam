#version 300 es
#line 1
precision mediump float;
uniform sampler2D sTexture;
out vec4 Output;
in vec2 texCoord;
const float factor = 3.0;
const int lookupsize = 16;
const int lookupstep = 1;
uniform bool show_color;

void main()
{
    vec2 oResolution = vec2(textureSize(sTexture, 0));
    vec2 onePixel = vec2(1.0, 1.0) / oResolution.xy;
    vec2 coords;
    float accum = 1.0 * onePixel.y;
    accum *= 80.0 * 5.;
//    float u = (texCoord.y / (oResolution.y * onePixel.y)) * factor;
    vec4 col = vec4(0.0, 0.0, 0.0, 1.0);
    float ys = (texCoord.y - onePixel.y) * factor;
    float ye = (texCoord.y + onePixel.y) * factor;
    vec4  pix = texture(sTexture, texCoord);
        for (int y = 0; y < lookupsize; y+=lookupstep){
            coords.x = texCoord.x;
            coords.y = onePixel.y * float(y);
            vec4  texcol = texture(sTexture, coords);
            if (texcol.r > ys && texcol.r < ye){
                if(show_color)
                    col += vec4(0., 0., accum, 0.);
                else
                    col += vec4(accum, accum, accum, 0.);
            }
            if (texcol.g > ys && texcol.g < ye){
                if(show_color)
                    col += vec4(0., accum, 0., 0.);
                else
                    col += vec4(accum, accum, accum, 0.);
            }
            if (texcol.b > ys && texcol.b < ye){
                if(show_color)
                    col += vec4(accum, 0., 0., 0.);
                else
                    col += vec4(accum, accum, accum, 0.);
            }
        }
    if(col.r == 0. && col.b == 0. && col.g == 0.)
        col.a = 0.8;
    if(!show_color && col.r >= 1. && col.b >= 1. && col.g >= 1.)
    {
        col.r = 1.;
        col.g = 0.;
        col.b = 0.;
        col.a = 1.;
    }
    Output = col;
}