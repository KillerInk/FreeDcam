package freed.gl.program;

import freed.gl.shader.Shader;
import freed.gl.texture.GLFrameBuffer;
import freed.gl.texture.GLTex;

public interface GLDrawProgramInterface {
    void create(Shader vertexShader,Shader fragmentShader);
    void draw(GLTex input, GLFrameBuffer output);
}
