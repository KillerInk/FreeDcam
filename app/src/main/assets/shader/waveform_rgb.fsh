#version 300 es
#line 1
precision mediump float;
uniform sampler2D sTexture;
out vec4 Output;
in vec2 texCoord;
const float intensity = 0.03;
const float thres = 0.001;
const int hres = 500;

void main()
{
    vec2 size = vec2(textureSize(sTexture, 0));
    vec2 uv = vec2(1.0 / size.x,1.0 / size.y).xy;
    vec3 col = vec3(0);
    float s = float(uv.y)*1.8 - 0.15;
    float maxb = s+thres;
    float minb = s-thres;

    for (int i = 0; i <= hres; i++) {
        vec3 x = texture(sTexture, vec2(float(i)/float(hres), uv.x)).rgb;
        col += vec3(intensity)*step(x, vec3(maxb))*step(vec3(minb), x);

        float l = dot(x, x);
        col += vec3(intensity)*step(l, maxb*maxb)*step(minb*minb, l);
    }

    Output = vec4(col,1.0);
}