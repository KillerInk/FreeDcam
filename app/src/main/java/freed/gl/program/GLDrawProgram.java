package freed.gl.program;

import android.opengl.GLES31;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import freed.gl.shader.Shader;
import freed.gl.texture.GLFrameBuffer;
import freed.gl.texture.GLTex;
import freed.utils.Log;

public abstract class GLDrawProgram extends GLProgram implements GLDrawProgramInterface{

    private final String TAG = GLDrawProgram.class.getSimpleName();
    private Shader vertexShader;
    private Shader fragmentShader;
    private float[] vtmp = {1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
    private float[] ttmp = {1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer textureBuffer;
    protected int vTexCoord;
    protected int vPosition;
    protected int sTexture;

    public GLDrawProgram(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public void create(Shader vertexShader, Shader fragmentShader) {
        vertexBuffer = ByteBuffer.allocateDirect(vtmp.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vtmp);
        vertexBuffer.position(0);
        textureBuffer = ByteBuffer.allocateDirect(ttmp.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(ttmp);
        textureBuffer.position(0);
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        createAndLinkProgram();
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        checkGlError("glCreateProgram");
        Log.d(TAG,"createAndLinkProgram id:" +hProgram);
        if (vertexShader != null) {
            GLES31.glAttachShader(hProgram, vertexShader.getHandel());
            checkGlError("glAttachShader vertex");
        }
        if (fragmentShader != null) {
            GLES31.glAttachShader(hProgram, fragmentShader.getHandel());
            checkGlError("glAttachShader fragment");
        }
        GLES31.glLinkProgram(hProgram);
        checkGlError("glLinkProgram");
        vPosition = GLES31.glGetAttribLocation(hProgram, "vPosition");
        vTexCoord = GLES31.glGetAttribLocation(hProgram, "vTexCoord");
    }

    @Override
    public void draw(GLTex input, GLFrameBuffer output) {

        if (output != null)
            output.setActive();
        else
            GLFrameBuffer.switchToDefaultFB();
        //step0 clear
        onClear();
        //step1 use program
        onUseProgram();
        //step2 active and bind custom data
        onSetData();
        //step3 bind texture
        onBindTexture(input);
        //step4 normal draw
        onDraw();
    }

    protected void onDraw()
    {
        GLES31.glEnableVertexAttribArray(vPosition);
        GLES31.glEnableVertexAttribArray(vTexCoord);

        GLES31.glDrawArrays(GLES31.GL_TRIANGLE_STRIP, 0, 4);

        GLES31.glDisableVertexAttribArray(vPosition);
        GLES31.glDisableVertexAttribArray(vTexCoord);
    }

    protected void onBindTexture(GLTex input) {
        GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
        checkGlError("onBindTexture glActiveTexture");
        if (input != null) {
            GLES31.glBindTexture(input.getGLTextureType(), input.getId());
            checkGlError("onBindTexture glBindTexture");
        }
        checkGlError("onBindTexture glUniform1i");
    }

    protected void onSetData()
    {
        GLES31.glVertexAttribPointer(vPosition, 2, GLES31.GL_FLOAT, false, 4 * 2, vertexBuffer);
        checkGlError("onSetData vertex Buffer");
        GLES31.glVertexAttribPointer(vTexCoord, 2, GLES31.GL_FLOAT, false, 4 * 2, textureBuffer);
        checkGlError("onSetData texture Buffer");
    }

    public void onUseProgram() {
        GLES31.glUseProgram(hProgram);
        checkGlError("onUseProgram");
    }

    protected void onClear()
    {
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        checkGlError("onClear glClearColor");
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);
        checkGlError("onClear glClear");
    }
}
