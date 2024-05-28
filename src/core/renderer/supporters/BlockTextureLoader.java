package core.renderer.supporters;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import static org.lwjgl.opengl.GL31.glTexBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import core.renderer.supporters.texturePackage.TextureMapLoader;
import core.utils.Paths;
import core.utils.Utils;

public class BlockTextureLoader {
    private int textureId;
    private int textureCoordBufferId;
    private int textureObjectId;

    public void loadTexture(String filename) throws Exception {  
        glActiveTexture(GL_TEXTURE2);
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
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_NONE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_NONE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
    }

    public void storeTexCoordsInBuffer(){
        glActiveTexture(GL_TEXTURE3);
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
        shader.set1i("txt", 2);
    }

    public void generateTextureCoordBuffer(ShaderManager shader){
        storeTexCoordsInBuffer();
        shader.set1i("texCoordBuffer", 3);
    }

    public void cleanup() {
        glDeleteBuffers(textureCoordBufferId);
        glDeleteTextures(textureObjectId);
        glDeleteTextures(textureId);
    }

    public void bindTexture(){
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_BUFFER, textureId);
    }

}
