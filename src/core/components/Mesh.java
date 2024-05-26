package core.components;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class Mesh {
    public int vao, vbo, texture, dataCount;

    public Mesh(){
        this.vao = 0;
        this.vbo = 0;
        this.dataCount = 0;
    }

    public Mesh(int vao, int vbo, int dataCount, int texture){
        this.vao = vao;
        this.vbo = vbo;
        this.dataCount = dataCount;
        this.texture = 0;
    }

    public void cleanup(){
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteTextures(texture);
        vao = 0;
        vbo = 0;
        texture = 0;
        dataCount = 0;
    }
}
