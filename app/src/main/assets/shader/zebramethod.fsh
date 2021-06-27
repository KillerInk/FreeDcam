
vec4 getZebra(vec2 texS, vec4 color, float pos,float high,float low)
{
    vec4 out_color;
    if(fract((texS.x + texS.y)*(20.0+pos)) > 0.9)
    {
        float gray = (color.r + color.g +color.b) /3.0;
        if(gray > (1.0 - high))
            out_color = mix(color,vec4(1.0, 0.0, 0.0, 1.0),gray);
        else if(gray < low)
            out_color =  mix(color,vec4(0.0, 0.4, 1.0, 1.0),1.0-gray);
        else
            out_color = color;
    }
    else if(fract((texS.x - texS.y)*(20.0+pos)) > 0.9)
    {
        float gray = (color.r + color.g +color.b) /3.0;
        if(gray > (1.0 - high))
            out_color = mix(color,vec4(1.0, 0.0, 0.0, 1.0),gray);
        else if(gray < low)
            out_color =  mix(color,vec4(0.0, 0.4, 1.0, 1.0),1.0-gray);
        else
            out_color = color;
    }
    else
        out_color = color;
    return out_color;
}