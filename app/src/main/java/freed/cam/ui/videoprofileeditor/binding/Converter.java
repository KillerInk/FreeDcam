package freed.cam.ui.videoprofileeditor.binding;

import android.widget.Button;
import android.widget.EditText;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import freed.cam.ui.videoprofileeditor.enums.AudioCodecs;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;
import freed.cam.ui.videoprofileeditor.enums.VideoCodecs;

public class Converter
{
    @BindingAdapter("convertByteToMb")
    public static String convertByteToMb(EditText editText, long value)
    {
        String t = String.valueOf((value /1024));
        if (editText != null)
            editText.setText(t);
        return t;
    }


    @BindingAdapter("convertFromMStoMin")
    public static String convertFromMStoMin(EditText editText,int time)
    {
        String t = String.valueOf((time /60 /1000));
        if (editText != null)
            editText.setText(t);
        return t;
    }


    @BindingAdapter("convertVideoCodecIntToString")
    public static String convertVideoCodecIntToString(Button editText, int codec)
    {
        String ret = "";
        for (VideoCodecs audio : VideoCodecs.values())
        {
            if (audio.GetInt() == codec)
                ret = audio.toString();
        }
        if (editText != null)
            editText.setText(ret);
        return ret;
    }

    @BindingAdapter("convertAudioCodecIntToString")
    public static String convertAudioCodecIntToString(Button editText, int codec)
    {
        String ret = "";
        for (AudioCodecs audio : AudioCodecs.values())
        {
            if (audio.GetInt() == codec)
                ret = audio.toString();
        }
        if (editText != null)
            editText.setText(ret);
        return ret;
    }


    @BindingAdapter("convertOpCodecIntToString")
    public static String convertOpCodecIntToString(Button editText, int codec)
    {
        String ret = "";
        for (OpCodes audio : OpCodes.values())
        {
            if (audio.GetInt() == codec)
                ret = audio.toString();
        }
        if (editText != null)
            editText.setText(ret);
        return ret;
    }

}
