package core.manager;

import core.generator.MeshLoader;
import core.generator.TextureMapLoader;
import core.utils.Paths;
import core.generator.ChunkGenerator;
import core.entity.Camera;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import org.joml.Vector3f;

import e16craft.E16craft;
import core.entity.ChunkData;
import core.entity.Cube;
import core.entity.Mesh;
//import core.entity.lights.DirectionalLight;

public class RenderManager {
    private ShaderManager shader;
    private MeshLoader meshLoader;

    private Mesh mesh;
    private Cube cube;
    private ChunkData chunk;
    private Camera camera;
    private InputManager input;
    private MainWindow window;

    public RenderManager() {
        window = E16craft.getMainWindow();
        TextureMapLoader mapLoader = new TextureMapLoader();
        meshLoader = new MeshLoader();
        camera = new Camera();
        input = new InputManager(6.0f, 0.03f);
        chunk = ChunkGenerator.generate();
        // light = new DirectionalLight(new Vector3f(1.0f), new Vector3f(1.0f, 0.0f,
        // 0.0f), 2);
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.init();
        input.init();

        mesh = meshLoader.loadMesh(chunk.positions, chunk.indices, chunk.uvs, null);
        int txt = meshLoader.loadTexture(Paths.blockTexture);
        mesh.setTexture(txt);

        cube = new Cube(mesh, new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f), 1.0f);

        camera.movePosition(0.0f, 0.0f, 0.0f);
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
        // glEnableVertexAttribArray(2);
        glDrawElements(GL_TRIANGLES, mesh.getIndexCount(), GL_UNSIGNED_INT, 0);
        // glDisableVertexAttribArray(2);
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
