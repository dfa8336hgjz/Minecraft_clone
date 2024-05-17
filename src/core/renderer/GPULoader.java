package core.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import static org.lwjgl.opengl.GL31.glTexBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import core.components.Mesh;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Paths;
import core.utils.Utils;

public class GPULoader {
    private int textureId;
    private int textureCoordBufferId;
    private int textureObjectId;

    public Mesh loadMesh(int[] data) {
        int vao = createVAO();
        int vbo = createVBO(0, 1, data);
        unbind();
        return new Mesh(vao, vbo, data.length);
    }

    public void reloadMesh(int[] data, Mesh mesh) {
        int vao = createVAO();
        int vbo = createVBO(0, 1, data);
        unbind();

        mesh.setBuffer(vao, vbo);
    }

    private int createVAO() {
        int id = glGenVertexArrays();
        glBindVertexArray(id);
        return id;
    }

    private int createVBO(int attribNo, int vertexCount, int[] data) {
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribIPointer(attribNo, vertexCount, GL_UNSIGNED_INT, 0, 0);
        
        return vbo;
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

    public void generateTextureObject(ShaderManager shader) throws Exception{
        loadTexture(Paths.blockTexture);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureObjectId);
        shader.set1i("txt", 0);
        shader.bind();
    }

    public void generateTextureCoordBuffer(ShaderManager shader){
        storeTexCoordsInBuffer();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_BUFFER, textureId);
        shader.set1i("texCoordBuffer", 1);
        shader.unbind();
    }

    private void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void cleanup() {
        glDeleteBuffers(textureCoordBufferId);
        glDeleteTextures(textureObjectId);
        glDeleteTextures(textureId);
    }

}
