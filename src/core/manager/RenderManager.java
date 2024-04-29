package core.manager;

import core.system.MeshLoader;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Consts;
import core.entity.Player;
import core.entity.World;
import static org.lwjgl.opengl.GL11.*;
import org.joml.Matrix4f;
import e16craft.E16craft;

public class RenderManager {
    private static ShaderManager shader;
    private static MeshLoader meshLoader;
    private Player player;

    private World world;
    private MainWindow window;
    private Matrix4f model;

    public RenderManager() {
        window = E16craft.getMainWindow();
        TextureMapLoader mapLoader = new TextureMapLoader();

        model = new Matrix4f();
        model.identity();
        player = new Player(0.0f, Consts.CHUNK_HEIGHT + 1, 0.0f);

        world = new World();
        meshLoader = new MeshLoader();
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.init();
        world.init();
        player.init();
        meshLoader.generateTextureObject();
        meshLoader.generateTextureCoordBuffer();
    }

    public void render() {
        clear();
        shader.bind();
        shader.setMat4f("model", model);
        shader.setMat4f("view", player.getView());
        shader.setMat4f("projection", window.updateProjection(player.getView()));
        world.render(shader);
        shader.unbind();
    }

    public void update() {
        player.update();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        meshLoader.cleanup();
        world.cleanup();
    }

    public static MeshLoader getLoader() {
        return meshLoader;
    }

    public static ShaderManager getShader(){
        return shader;
    }
}
