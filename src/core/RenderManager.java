/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import org.joml.Vector3f;

import e16craft.E16craft;
import core.entity.Cube;
import core.entity.Mesh;
import core.entity.Texture;
import core.entity.lights.DirectionalLight;

public class RenderManager {
    private ShaderManager shader;
    private MeshLoader meshLoader;
    private Mesh mesh;
    private Cube cube;
    private Camera camera;
    private InputManager input;
    private MainWindow window;

    private DirectionalLight light;

    public RenderManager() {
        window = E16craft.getMainWindow();
        meshLoader = new MeshLoader();
        camera = new Camera();
        input = new InputManager(6.0f, 0.03f);
        light = new DirectionalLight(new Vector3f(1.0f), new Vector3f(1.0f, 0.0f, 0.0f), 2);
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.init();
        float[] vertices = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };

        float[] textCoords = new float[] {
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };

        float[] normals = new float[] {
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

        };
        int[] indices = new int[] {
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,
        };

        mesh = meshLoader.loadMesh(vertices, indices, textCoords, normals);
        int txt = meshLoader.loadTexture("gmerry.jpg");
        mesh.setTexture(new Texture(txt));
        cube = new Cube(mesh, new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f), 1.0f);
        camera.movePosition(0.0f, 0.0f, 0.0f);
        input.init();
    }

    public void render() {
        clear();
        shader.bind();
        shader.setMat4f("model", cube.getModelMatrix());
        shader.setMat4f("view", camera.getViewMatrix());
        shader.setMat4f("projection", window.updateProjection(camera.getViewMatrix()));
        // shader.setLight("directionalLight", light);
        // shader.set1f("specularPower", 10.0f);
        // shader.set1i("material.hasTexture", 1);
        glBindVertexArray(mesh.getId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawElements(GL_TRIANGLES, mesh.getIndexCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        shader.unbind();
    }

    public void update() {
        input.input(camera);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        meshLoader.cleanup();
    }

    public Camera getCamera() {
        return camera;
    }
}
