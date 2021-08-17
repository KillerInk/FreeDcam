package freed.gl;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import freed.FreedApplication;
import freed.gl.shader.Shader;

public class ShaderUtil {

    private static final String TAG = ShaderUtil.class.getSimpleName();


    public static String getShader(String name, int glesVersion)
    {
        Context context = FreedApplication.getContext();
        final ShaderParser parser = new ShaderParser(glesVersion);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(name, AssetManager.ACCESS_BUFFER)));

            // do reading, usually loop until end of file reading
            String mLine;

            while ((mLine = reader.readLine()) != null) {
                if (!mLine.isEmpty() && !mLine.startsWith("//"))
                {
                    parser.parseLine(mLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    e.printStackTrace();
                }
            }
        }
        String out = parser.getString();
        return out;
    }

    public static String getShader(String name) throws IOException {
        Context context = FreedApplication.getContext();
        try (InputStream stream = context.getAssets().open(name, AssetManager.ACCESS_BUFFER))
        {
            byte[] result = new byte[stream.available()];
            int bytesRead = stream.read(result);
            return new String(result, Charset.defaultCharset());
        }
    }

    public static String getShader(int glesVersion, String name, Shader.ShaderType type) throws IOException {
        String end = ".vsh";
        if (type == Shader.ShaderType.fragment)
            end = ".fsh";

        return getShader("shader/"+name+end,glesVersion);
    }


    public static int createShader(String shader , String shadername, int shaderType)
    {
        int[] compiled = new int[1];
        int fshader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(fshader, shader);
        GLES20.glCompileShader(fshader);
        GLES20.glGetShaderiv(fshader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        Log.d(TAG, "create shader: " + shadername + "\n" + shader);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader: " + shadername + "\n" + GLES20.glGetShaderInfoLog(fshader) + "\n" + shader);
            GLES20.glDeleteShader(fshader);
            fshader = 0;
            throw new RuntimeException("Could not compile shader: " + shadername);
        }
        return fshader;
    }
}
