package freed.gl.program;

import android.opengl.GLES20;

public class ClippingProgram extends GLProgram {


    int float_position_id;
    int zebra_low_id;
    private int zebra_high_id;

    private float float_position = 0;
    private float zebra_high = 0.001f;
    private float zebra_low = 0.01f;

    public ClippingProgram(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        float_position_id = GLES20.glGetUniformLocation (hProgram, "float_position");
        zebra_low_id = GLES20.glGetUniformLocation(hProgram, "zebra_low");
        zebra_high_id = GLES20.glGetUniformLocation (hProgram, "zebra_high");
    }

    @Override
    protected void onSetData() {
        super.onSetData();
        GLES20.glUniform1f(float_position_id, float_position);
        GLES20.glUniform1f(zebra_low_id, zebra_low);
        GLES20.glUniform1f(zebra_high_id, zebra_high);
    }

    public void setZebra_high(float zebra_high) {
        this.zebra_high = zebra_high;
    }

    public float getZebra_high() {
        return zebra_high;
    }

    public void setZebra_low(float zebra_low) {
        this.zebra_low = zebra_low;
    }

    public float getZebra_low() {
        return zebra_low;
    }

    public float getFloat_position() {
        return float_position;
    }

    public void setFloat_position(float float_position) {
        this.float_position = float_position;
    }
}
