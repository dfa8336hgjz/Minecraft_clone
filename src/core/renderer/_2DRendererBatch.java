package core.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import core.components.TextureData;
import core.utils.Consts;
import core.utils.Paths;

public class _2DRendererBatch {
    private int[] indices = {
            0, 1, 2, 2, 3, 0
    };

    // 25 quads
    public static int BATCH_SIZE = 100;
    public static int VERTEX_SIZE = 4;
    public float[] vertices = new float[BATCH_SIZE * VERTEX_SIZE];
    public int size = 0;

    private int vao;
    private int vbo;
    private int texture;
    private ShaderManager shader;

    public void generateEbo() {
        int elementSize = BATCH_SIZE * 3;
        int[] elementBuffer = new int[elementSize];

        for (int i=0; i < elementSize; i++) {
            elementBuffer[i] = indices[(i % 6)] + ((i / 6) * 4);
        }

        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
    }

    public void initBatch() throws Exception {
        shader = new ShaderManager("src\\assets\\shader\\vs2D.glsl", "src\\assets\\shader\\fs2D.glsl");
        shader.init();

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);

        generateEbo();

        int stride = 4 * Float.BYTES;
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        String filename = Paths.guiTexture;
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

        glActiveTexture(GL_TEXTURE1);
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_NONE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_NONE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);

        
        shader.bind();
        shader.set1i("uFontTexture", 1);
        shader.unbind();
    }

    public void flushBatch() {
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_BUFFER, texture);
        shader.bind();
        shader.setMat4f("uProjection", Consts.GUI_PROJECTION);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, size * 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        size = 0;
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        shader.unbind();
    }

    public void drawSprite(float x, float y, float sizeX, float sizeY, TextureData texture){
        if (size >= BATCH_SIZE - 4) {
            flushBatch();
        }

        float x0 = x;
        float y0 = y;
        float x1 = x + sizeX;
        float y1 = y + sizeY;

        int index = size * 4;
        vertices[index] = x0;      vertices[index + 1] = y0;
        vertices[index + 2] = texture.getCoordsAt(0).x; vertices[index + 3] = texture.getCoordsAt(0).y;

        index += 4;
        vertices[index] = x1;      vertices[index + 1] = y0;
        vertices[index + 2] = texture.getCoordsAt(2).x; vertices[index + 3] = texture.getCoordsAt(2).y;

        index += 4;
        vertices[index] = x1;      vertices[index + 1] = y1;
        vertices[index + 2] = texture.getCoordsAt(3).x; vertices[index + 3] = texture.getCoordsAt(3).y;

        index += 4;
        vertices[index] = x0;      vertices[index + 1] = y1;
        vertices[index + 2] = texture.getCoordsAt(1).x; vertices[index + 3] = texture.getCoordsAt(1).y;

        size += 4;
    }

    public void cleanup(){
        shader.cleanup();
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteTextures(texture);
    }
}
