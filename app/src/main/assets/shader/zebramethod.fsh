vec4 getZebra(vec2 texS, vec4 color)
{
    vec4 out_color;
    if(fract((texS.x + texS.y)*20.0) > 0.3)
    {
        float gray = ((color.r + color.g +color.b)/3.0);
        if(gray > (1.0 - 0.001))
        out_color = vec4(1.0, 0.0, 0.0, 1.0);
        else if(gray < 0.001)
        out_color = vec4(0.0, 0.0, 1.0, 1.0);
        else
        out_color = color;
    }
    else
    out_color = color;
    return out_color;
}