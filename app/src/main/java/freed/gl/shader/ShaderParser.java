package freed.gl.shader;

import java.io.IOException;

public class ShaderParser
{

    private final static String linebreak = "\n";
    private final static String GLES2 = "#GLES2";
    private final static String GLES3 = "#GLES3";
    private final static String END = "#END_GLES";
    private final static String IMPORT = "#IMPORT ";
    private final float glesVersion;
    private boolean gles2BlockOpen = false;
    private boolean gles3BlockOpen = false;

    private StringBuilder stringBuilder;
    public ShaderParser(float glesVersion)
    {
        this.glesVersion = glesVersion;
        stringBuilder = new StringBuilder();
    }

    public void parseLine(String line) throws IOException {
        boolean gles2 = line.startsWith(GLES2);
        boolean gles3 = line.startsWith(GLES3);
        boolean end = line.startsWith(END);
        boolean imports = line.startsWith(IMPORT);
        if (imports)
        {
            String p = line.replace(IMPORT,"");
            String add = ShaderUtil.getShader(p,glesVersion);
            stringBuilder.append(add);
        }
        else if (gles2) {
            gles2BlockOpen = true;
            return;
        }
        else if (gles3) {
            gles3BlockOpen = true;
            return;
            //stringBuilder.append(line.replace(GLES3, "")).append(linebreak);
        }
        else if (end)
        {
            gles2BlockOpen = false;
            gles3BlockOpen = false;
            return;
        }
        else if (!gles2 && !gles2 && !gles3BlockOpen && !gles2BlockOpen)
            stringBuilder.append(line).append(linebreak);
        else if (gles2BlockOpen  && glesVersion == 2)
            stringBuilder.append(line).append(linebreak);
        else if (gles3BlockOpen  && glesVersion == 3)
            stringBuilder.append(line).append(linebreak);
    }


    public String getString()
    {
        return stringBuilder.toString();
    }
}
