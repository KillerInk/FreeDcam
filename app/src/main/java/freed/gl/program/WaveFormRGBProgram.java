package freed.gl.program;

import android.opengl.GLES20;

import freed.utils.Log;

public class WaveFormRGBProgram extends GLProgram {

    private final String TAG = WaveFormRGBProgram.class.getSimpleName();

    private boolean colorwaveform = true;
    private int show_color;



    public WaveFormRGBProgram(int glesVersion) {
        super(glesVersion);
    }


    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        show_color = GLES20.glGetUniformLocation(hProgram, "show_color");
    }

    @Override
    protected void onSetData() {
        super.onSetData();
        GLES20.glUniform1f(show_color, colorwaveform ? 1:0);
    }

    public void setColorWaveForm(boolean on)
    {
        this.colorwaveform = on;
    }

    public boolean isColorWaveForm()
    {
        return colorwaveform;
    }
}
