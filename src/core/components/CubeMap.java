package core.components;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import core.launcher.Launcher;
import core.renderer.ShaderManager;
import core.utils.Utils;


public class CubeMap {
    public String[] texturePaths;
    private float[] vertices ={
        -1.0f,  1.0f, -1.0f,
		-1.0f, -1.0f, -1.0f,
		 1.0f, -1.0f, -1.0f,
		 1.0f, -1.0f, -1.0f,
		 1.0f,  1.0f, -1.0f,
		-1.0f,  1.0f, -1.0f,

		-1.0f, -1.0f,  1.0f,
		-1.0f, -1.0f, -1.0f,
		-1.0f,  1.0f, -1.0f,
		-1.0f,  1.0f, -1.0f,
		-1.0f,  1.0f,  1.0f,
		-1.0f, -1.0f,  1.0f,

		 1.0f, -1.0f, -1.0f,
		 1.0f, -1.0f,  1.0f,
		 1.0f,  1.0f,  1.0f,
		 1.0f,  1.0f,  1.0f,
		 1.0f,  1.0f, -1.0f,
		 1.0f, -1.0f, -1.0f,

		-1.0f, -1.0f,  1.0f,
		-1.0f,  1.0f,  1.0f,
		 1.0f,  1.0f,  1.0f,
		 1.0f,  1.0f,  1.0f,
		 1.0f, -1.0f,  1.0f,
		-1.0f, -1.0f,  1.0f,

		-1.0f,  1.0f, -1.0f,
		 1.0f,  1.0f, -1.0f,
		 1.0f,  1.0f,  1.0f,
		 1.0f,  1.0f,  1.0f,
		-1.0f,  1.0f,  1.0f,
		-1.0f,  1.0f, -1.0f,

		-1.0f, -1.0f, -1.0f,
		-1.0f, -1.0f,  1.0f,
		 1.0f, -1.0f, -1.0f,
		 1.0f, -1.0f, -1.0f,
		-1.0f, -1.0f,  1.0f,
		 1.0f, -1.0f,  1.0f
    };

    private Mesh mesh;
    private int texture;
    private ShaderManager shader;
    public Matrix4f projection;

    public CubeMap(String top, String bottom, String front, String back, String left, String right) throws Exception{
        texturePaths = new String[]{
            left, right, top, bottom, front, back
        };

        glActiveTexture(GL_TEXTURE4);
        shader = new ShaderManager("src\\assets\\shader\\vsCubemap.glsl", "src\\assets\\shader\\fsCubemap.glsl");
        shader.init();
        shader.bind();
        shader.set1i("skybox", 4);

        texture = glGenTextures();
        
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
        
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        for (int i = 0; i < texturePaths.length; i++) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer c = stack.mallocInt(1);
    
                stbi_set_flip_vertically_on_load(false);
                ByteBuffer texbuffer = stbi_load(texturePaths[i], w, h, c, 4);
                if (texbuffer == null) {
                    throw new Exception("Cannot load image file: " + texturePaths[i]);
                }
                texbuffer.flip();
    
                int width = w.get();
                int height = h.get();

                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texbuffer);
                stbi_image_free(texbuffer);
            }
        }
        
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(vertices);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        mesh = new Mesh(vao, vbo, 0, 0);
        shader.unbind();

        projection = new Matrix4f(Launcher.instance.getWindow().updateProjection(new Matrix4f().identity()));
    }

    public void render(Matrix4f view){
        glDepthFunc(GL_LEQUAL);
        glDisable(GL_CULL_FACE);

        shader.bind();
        shader.setMat4f("view", view);
        shader.setMat4f("projection",projection);

        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);

        glBindVertexArray(mesh.vao);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);

        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);

        shader.unbind();
    }

    public void cleanup(){
        glDeleteTextures(texture);
        glDeleteBuffers(mesh.vbo);
        glDeleteVertexArrays(mesh.vao);
    }

}
