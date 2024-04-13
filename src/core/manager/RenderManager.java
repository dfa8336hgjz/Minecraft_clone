package core.manager;

import core.generator.MeshLoader;
import core.generator.TextureMapLoader;
import core.utils.Consts;
import core.World;
import core.entity.Camera;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;

import e16craft.E16craft;
//import core.entity.lights.DirectionalLight;

public class RenderManager {
    private ShaderManager shader;
    private static MeshLoader meshLoader;

    private World world;
    private Camera camera;
    private InputManager input;
    private MainWindow window;
    private Matrix4f model;

    public RenderManager() {
        TextureMapLoader mapLoader = new TextureMapLoader();
        model = new Matrix4f();
        model.identity();

        world = new World();
        window = E16craft.getMainWindow();
        meshLoader = new MeshLoader();
        camera = new Camera();
        input = new InputManager(15.0f, 0.03f);
        // light = new DirectionalLight(new Vector3f(1.0f), new Vector3f(1.0f, 0.0f,
        // 0.0f), 2);
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.init();
        input.init();
        world.init();

        camera.movePosition(0.0f, Consts.CHUNK_HEIGHT, 0.0f);
    }

    public void render() {
        clear();
        shader.bind();
        shader.setMat4f("model", model);
        shader.setMat4f("view", camera.getViewMatrix());
        shader.setMat4f("projection", window.updateProjection(camera.getViewMatrix()));
        // shader.setLight("directionalLight", light);
        // shader.set1f("specularPower", 10.0f);
        // shader.set1i("material.hasTexture", 1);
        world.render();
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

    public static MeshLoader getLoader() {
        return meshLoader;
    }
}
