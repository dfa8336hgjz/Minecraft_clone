package core.generator;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import static org.lwjgl.opengl.GL31.glTexBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import core.entity.Mesh;
import core.manager.RenderManager;
import core.utils.Paths;
import core.utils.Utils;

public class MeshLoader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private int textureId;
    private int textureCoordBufferId;
    private int textureObjectId;

    public Mesh loadMesh(int[] data) {
        int id = createVAO();
        storeDataInAttribList(0, 1, data);
        unbind();
        return new Mesh(id, data.length);
    }

    private int createVAO() {
        int id = glGenVertexArrays();
        vaos.add(id);
        glBindVertexArray(id);
        return id;
    }

    private void storeDataInAttribList(int attribNo, int vertexCount, int[] data) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribIPointer(attribNo, vertexCount, GL_UNSIGNED_INT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void loadTexture(String filename) throws Exception {  
        glActiveTexture(GL_TEXTURE1);
        int width, height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if (buffer == null) {
                throw new Exception("Cannot load image file: " + filename);
            }

            width = w.get();
            height = h.get();
        }

        textureObjectId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureObjectId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
    }

    public void storeTexCoordsInBuffer(){
        textureCoordBufferId = glGenBuffers();
        float[] texCoord = TextureMapLoader.getTexCoordList();

        FloatBuffer uvBuffer = Utils.storeDataInFloatBuffer(texCoord);
        glBindBuffer(GL_TEXTURE_BUFFER, textureCoordBufferId);
        glBufferData(GL_TEXTURE_BUFFER, uvBuffer, GL_STATIC_DRAW);

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_BUFFER, textureId);
        glTexBuffer(GL_TEXTURE_BUFFER, GL_R32F, textureCoordBufferId);

        glBindBuffer(GL_TEXTURE_BUFFER, 0);
        glBindTexture(GL_TEXTURE_BUFFER, 0);
    }

    public void generateTextureObject() throws Exception{
        loadTexture(Paths.blockTexture);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureObjectId);
        RenderManager.getShader().set1i("txt", 0);
        RenderManager.getShader().bind();
    }

    public void generateTextureCoordBuffer(){
        storeTexCoordsInBuffer();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_BUFFER, textureId);
        RenderManager.getShader().set1i("texCoordBuffer", 1);
        RenderManager.getShader().unbind();
    }

    private void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void cleanup() {
        for (int vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            glDeleteBuffers(vbo);
        }
        glDeleteBuffers(textureCoordBufferId);
        glDeleteTextures(textureObjectId);
        glDeleteTextures(textureId);
    }

}
