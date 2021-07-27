#version 300 es
#line 1
precision mediump float;
uniform sampler2D sTexture;
out vec4 Output;
in vec2 texCoord;
const float factor = 3.0;

void main()
{
    vec2 oResolution = vec2(textureSize(sTexture, 0));
    vec2 onePixel = vec2(1.0, 1.0) / oResolution.xy;
    vec2 coords;
    float accum = 1.0 * onePixel.y;
    accum *= 80.0;
//    float u = (texCoord.y / (oResolution.y * onePixel.y)) * factor;
    vec4 col = vec4(0.0, 0.0, 0.0, 1.0);
    float ys = (texCoord.y - onePixel.y) * factor;
    float ye = (texCoord.y + onePixel.y) * factor;
    vec4  pix = texture(sTexture, texCoord);

        int h = int(oResolution.y);
        for (int y = 0; y < h; y+=4){
            coords.x = texCoord.x;
            coords.y = onePixel.y * float(y);
            vec4  texcol = texture(sTexture, coords);
            if (texcol.r > ys && texcol.r < ye){
                col += vec4(accum, 0., 0., 0.);
            }
            if (texcol.g > ys && texcol.g < ye){
                col += vec4(0., accum, 0., 0.);
            }
            if (texcol.b > ys && texcol.b < ye){
                col += vec4(0., 0., accum, 0.);
            }
        }
    if(col.r == 0. && col.b == 0. && col.g == 0.)
        col.a = 0.8;
    Output = col;
}