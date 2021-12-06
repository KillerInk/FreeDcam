package freed.gl.program.draw;

import android.opengl.GLES20;

import freed.gl.program.GLDrawProgram;

public class WaveFormRGBProgram extends GLDrawProgram {

    private final String TAG = WaveFormRGBProgram.class.getSimpleName();

    private int colorwaveform = 0;
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
        GLES20.glUniform1i(show_color, colorwaveform);
    }

    public void setColorWaveForm(boolean on)
    {
        this.colorwaveform++;
        if (colorwaveform == 3)
            colorwaveform = 0;
    }

    public int isColorWaveForm()
    {
        return colorwaveform;
    }
}
