package freed.gl.program;

import freed.gl.shader.Shader;
import freed.gl.texture.GLFrameBuffer;
import freed.gl.texture.GLTex;

public interface GLDrawProgramInterface {
    void setVertexShader(Shader vertexShader);
    void setFragmentShader(Shader vertexShader);
    void create();
    void draw(GLTex input, GLFrameBuffer output);
}
