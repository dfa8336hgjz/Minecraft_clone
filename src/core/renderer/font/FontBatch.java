package core.renderer.font;

import org.joml.Vector2i;

import core.launcher.Launcher;
import core.renderer.ShaderManager;
import core.renderer.gui.Button;
import core.utils.Consts;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;

public class FontBatch {
    private int[] indices = {
            0, 1, 3,
            1, 2, 3
    };

    // 25 quads
    public static int BATCH_SIZE = 200;
    public static int VERTEX_SIZE = 7;
    public float[] vertices = new float[BATCH_SIZE * VERTEX_SIZE];
    public int size = 0;

    private int vao;
    private int vbo;
    private ShaderManager shader;
    private Cfont font;

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
        shader = new ShaderManager("src\\assets\\shader\\vsFont.glsl", "src\\assets\\shader\\fsFont.glsl");
        shader.init();

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);

        generateEbo();

        int stride = 7 * Float.BYTES;
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 5 * Float.BYTES);
        glEnableVertexAttribArray(2);

        font = Launcher.instance.font;
        
        shader.bind();
        shader.set1i("uFontTexture", 0);
        shader.unbind();
    }

    public void flushBatch() {
        shader.bind();
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_BUFFER, font.textureId);
        shader.setMat4f("uProjection", Consts.GUI_PROJECTION);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, size * 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        size = 0;
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        shader.unbind();
    }

    public void addCharacter(float x, float y, float scale, CharInfo charInfo, int rgb) {
        if (size >= BATCH_SIZE - 4) {
            flushBatch();
        }

        float r = (float)((rgb >> 16) & 0xFF) / 255.0f;
        float g = (float)((rgb >> 8) & 0xFF) / 255.0f;
        float b = (float)((rgb >> 0) & 0xFF) / 255.0f;

        float x0 = x;
        float y0 = y;
        float x1 = x + scale * charInfo.width;
        float y1 = y + scale * charInfo.height;

        float ux0 = charInfo.textureCoordinates[0].x; float uy0 = charInfo.textureCoordinates[0].y;
        float ux1 = charInfo.textureCoordinates[1].x; float uy1 = charInfo.textureCoordinates[1].y;

        int index = size * 7;
        vertices[index] = x1;      vertices[index + 1] = y0;
        vertices[index + 2] = r;   vertices[index + 3] = g;  vertices[index + 4] = b;
        vertices[index + 5] = ux1; vertices[index + 6] = uy0;

        index += 7;
        vertices[index] = x1;      vertices[index + 1] = y1;
        vertices[index + 2] = r;   vertices[index + 3] = g;  vertices[index + 4] = b;
        vertices[index + 5] = ux1; vertices[index + 6] = uy1;

        index += 7;
        vertices[index] = x0;      vertices[index + 1] = y1;
        vertices[index + 2] = r;   vertices[index + 3] = g;  vertices[index + 4] = b;
        vertices[index + 5] = ux0; vertices[index + 6] = uy1;

        index += 7;
        vertices[index] = x0;      vertices[index + 1] = y0;
        vertices[index + 2] = r;   vertices[index + 3] = g;  vertices[index + 4] = b;
        vertices[index + 5] = ux0; vertices[index + 6] = uy0;

        size += 4;
    }

    public void drawTextOnButton(Button button, float scale, int rgb){
        int textSizeX = 0;
        for (int i=0; i < button.text.length(); i++) {
            char c = button.text.charAt(i);
            CharInfo charInfo = font.getCharacter(c);
            if (charInfo.width == 0) {
                System.out.println("Unknown character " + c);
                continue;
            }
            textSizeX += charInfo.width * scale;
        }
        int textSizeY = (int)(font.getCharacter(button.text.charAt(0)).height * scale);
        Vector2i startPosition = new Vector2i(0);
        startPosition.x = button.position.x + (button.size.x - textSizeX)/ 2;
        startPosition.y = button.position.y + (button.size.y - textSizeY)/ 2;
        
        for (int i=0; i < button.text.length(); i++) {
            char c = button.text.charAt(i);

            CharInfo charInfo = font.getCharacter(c);
            if (charInfo.width == 0) {
                System.out.println("Unknown character " + c);
                continue;
            }

            float xPos = startPosition.x;
            float yPos = startPosition.y;
            addCharacter(xPos, yPos, scale, charInfo, rgb);
            startPosition.x += charInfo.width * scale;
        }
    }

    public void drawText(String text, int x, int y, float scale, int rgb) {
        for (int i=0; i < text.length(); i++) {
            char c = text.charAt(i);

            CharInfo charInfo = font.getCharacter(c);
            if (charInfo.width == 0) {
                System.out.println("Unknown character " + c);
                continue;
            }

            float xPos = x;
            float yPos = y;
            addCharacter(xPos, yPos, scale, charInfo, rgb);
            x += charInfo.width * scale;
        }
    }

    public void cleanup(){
        shader.cleanup();
        glDeleteVertexArrays(vao);
    }
}