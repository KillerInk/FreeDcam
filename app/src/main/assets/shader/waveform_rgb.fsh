#version 300 es
#line 1
precision mediump float;
uniform sampler2D sTexture;
out vec4 Output;
in vec2 texCoord;


void main()
{
    vec2 oResolution = vec2(textureSize(sTexture, 0));
    vec2 onePixel = vec2(1.0, 1.0) / oResolution.xy;
    vec2 coords;
    float accum = 1.0 * onePixel.y;
    accum *= 20.0;

    vec4 col = vec4(0.0, 0.0, 0.0, 1.0);
    float ys = texCoord.y - onePixel.y;
    float ye = texCoord.y + onePixel.y;
    vec4  pix = texture(sTexture, texCoord);
    int w = int(oResolution.x / 8.0);
    for (int y = 0; y < w; ++y){
        coords.x = texCoord.x;
        coords.y += onePixel.y;
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
        if(col.r == 0. && col.g == 0. && col.b == 0.)
            col = pix;
    }
    Output = col;
}