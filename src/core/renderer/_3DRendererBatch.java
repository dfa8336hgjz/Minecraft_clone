package core.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

import org.joml.Vector3f;

import core.components.Mesh;
import core.components.Player;
import core.launcher.Launcher;
import core.utils.Consts;

public class _3DRendererBatch {
    public static _3DRendererBatch instance;
    private final int BATCH_VERTEX_SIZE = 2400;
    private float vertices[] = new float[BATCH_VERTEX_SIZE];

    private Mesh renderMesh;
    private ShaderManager shader;

    private int numVertices = 0;

    public _3DRendererBatch() throws Exception{
        instance = this;
        shader = new ShaderManager("src\\assets\\shader\\vsLineRender.glsl", "src\\assets\\shader\\fsLineRender.glsl");
        shader.init();
        setupMesh();
    }

    public void render(){
        flushBatch();
    }

    public void setupMesh(){
        int vao = glCreateVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();

        // Allocate space for the batched vao
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * BATCH_VERTEX_SIZE, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 1, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 1, GL_FLOAT, false,  8 * Float.BYTES, 1 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 3, GL_FLOAT, false,  8 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, 3, GL_FLOAT, false,  8 * Float.BYTES, 5 * Float.BYTES);
        glEnableVertexAttribArray(3);

        renderMesh = new Mesh(vao, vbo, 0, 0);

        shader.bind();
        shader.set1f("uStrokeWidth", 0.02f);
        shader.set4f("uColor", 0.0f, 0.0f, 0.0f, 1.0f);
        shader.set1f("uAspectRatio", Consts.WINDOW_WIDTH / Consts.WINDOW_HEIGHT);
        shader.unbind();
    }


    public void flushBatch(){
        if (numVertices <= 0) return;

        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBindBuffer(GL_ARRAY_BUFFER, renderMesh.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        shader.bind();
        shader.setMat4f("uProjection", Launcher.instance.getWindow().updateProjection(Player.instance.camera.getViewMatrix()));
        shader.setMat4f("uView", Player.instance.camera.getViewMatrix());

        glBindVertexArray(renderMesh.vao);
        glDrawArrays(GL_TRIANGLES, 0, numVertices);

        // Clear the batch
        numVertices = 0;

        shader.unbind();
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
    }


    public void drawLine(Vector3f start, Vector3f end){
        if (numVertices + 6 >= (BATCH_VERTEX_SIZE))
        {
            flushBatch();
        }

        vertices[numVertices] = 1.0f;
        vertices[numVertices + 1] = -1.0f;
        vertices[numVertices + 2] = start.x;
        vertices[numVertices + 3] = start.y;
        vertices[numVertices + 4] = start.z;
        vertices[numVertices + 5] = end.x;
        vertices[numVertices + 6] = end.y;
        vertices[numVertices + 7] = end.z;
        numVertices += 8;

        vertices[numVertices] = 1.0f;
        vertices[numVertices + 1] = 1.0f;
        vertices[numVertices + 2] = start.x;
        vertices[numVertices + 3] = start.y;
        vertices[numVertices + 4] = start.z;
        vertices[numVertices + 5] = end.x;
        vertices[numVertices + 6] = end.y;
        vertices[numVertices + 7] = end.z;
        numVertices += 8;

        vertices[numVertices] = 0.0f;
        vertices[numVertices + 1] = 1.0f;
        vertices[numVertices + 2] = start.x;
        vertices[numVertices + 3] = start.y;
        vertices[numVertices + 4] = start.z;
        vertices[numVertices + 5] = end.x;
        vertices[numVertices + 6] = end.y;
        vertices[numVertices + 7] = end.z;
        numVertices += 8;

        vertices[numVertices] = 1.0f;
        vertices[numVertices + 1] = -1.0f;
        vertices[numVertices + 2] = start.x;
        vertices[numVertices + 3] = start.y;
        vertices[numVertices + 4] = start.z;
        vertices[numVertices + 5] = end.x;
        vertices[numVertices + 6] = end.y;
        vertices[numVertices + 7] = end.z;
        numVertices += 8;

        vertices[numVertices] = 0.0f;
        vertices[numVertices + 1] = 1.0f;
        vertices[numVertices + 2] = start.x;
        vertices[numVertices + 3] = start.y;
        vertices[numVertices + 4] = start.z;
        vertices[numVertices + 5] = end.x;
        vertices[numVertices + 6] = end.y;
        vertices[numVertices + 7] = end.z;
        numVertices += 8;

        vertices[numVertices] = 0.0f;
        vertices[numVertices + 1] = -1.0f;
        vertices[numVertices + 2] = start.x;
        vertices[numVertices + 3] = start.y;
        vertices[numVertices + 4] = start.z;
        vertices[numVertices + 5] = end.x;
        vertices[numVertices + 6] = end.y;
        vertices[numVertices + 7] = end.z;
        numVertices += 8;
    }

    public void drawBox(Vector3f center, Vector3f size){
        Vector3f v0 = new Vector3f(center.x - size.x * 0.5f, center.y - size.y * 0.5f, center.z - size.z * 0.5f);
        Vector3f v1 = new Vector3f(v0.x + size.x, v0.y, v0.z);
        Vector3f v2 = new Vector3f(v0.x, v0.y, v0.z + size.z);
        Vector3f v3 = new Vector3f(v0.x + size.x, v0.y, v0.z + size.z);

        Vector3f v4 = new Vector3f(v0.x, v0.y + size.y, v0.z);
        Vector3f v5 = new Vector3f(v1.x, v1.y + size.y, v1.z);
        Vector3f v6 = new Vector3f(v2.x, v2.y + size.y, v2.z);
        Vector3f v7 = new Vector3f(v3.x, v3.y + size.y, v3.z);

        drawLine(v0, v1);
        drawLine(v0, v2);
        drawLine(v2, v3);
        drawLine(v1, v3);

        drawLine(v4, v5);
        drawLine(v4, v6);
        drawLine(v5, v7);
        drawLine(v6, v7);

        drawLine(v0, v4);
        drawLine(v1, v5);
        drawLine(v2, v6);
        drawLine(v3, v7);
    }

    public void cleanup(){
        renderMesh.cleanup();
        shader.cleanup();
        vertices = null;
    }
}
