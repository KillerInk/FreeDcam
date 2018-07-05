package freed.cam.apis.camera2.modules.helper;

public class Output {
    public int jpeg_width;
    public int jpeg_height;
    public int raw_width;
    public int raw_height;
    public int raw_format;

    @Override
    public String toString()
    {
        return "Jpeg: " + jpeg_width + "x"+ jpeg_height + " Raw: " + raw_width + "x"+ raw_height + " format:"  + raw_format;
    }
}
