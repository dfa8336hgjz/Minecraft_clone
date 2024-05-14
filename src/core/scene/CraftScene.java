package core.scene;

import core.components.Camera;
import core.components.Player;
import core.components.World;
import core.launcher.Launcher;
import core.launcher.Scene;
import core.system.input.PlayerInput;
import renderer.ShaderManager;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Consts;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;

public class CraftScene extends Scene {
    private Camera playerView;
    private PlayerInput playerInput;
    private ShaderManager shader;

    private World world;
    private Matrix4f model = new Matrix4f().identity();

    @Override
    public void init() {
        try {
            TextureMapLoader mapLoader = new TextureMapLoader();
            playerView = new Camera(36.0f, Consts.CHUNK_HEIGHT + 10, 5.0f);
            playerInput = new PlayerInput(10.0f, 0.03f);
            Player.instance.setPlayerView(playerView);
            Launcher.getInputManager().setCurrentInputControl(playerInput);

            shader = new ShaderManager();
            shader.init();

            world = new World();
            world.init();
            Launcher.getGpuLoader().generateTextureObject(shader);
            Launcher.getGpuLoader().generateTextureCoordBuffer(shader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void render(){
        Player.instance.update();
        shader.bind();
        shader.setMat4f("model", model);
        shader.setMat4f("view", playerView.getViewMatrix());
        shader.setMat4f("projection", Launcher.getWindow().updateProjection(playerView.getViewMatrix()));
        shader.set3f("playerPos", playerView.transform.position);
        world.render(shader);
        shader.unbind();
    }

    @Override
    public void cleanup() {
        world.cleanup();
    }
}
