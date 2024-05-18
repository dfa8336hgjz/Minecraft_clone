package core.scene;

import core.components.Camera;
import core.components.Player;
import core.components.World;
import core.launcher.Launcher;
import core.launcher.Scene;
import core.renderer.Renderer2dBatch;
import core.renderer.ShaderManager;
import core.system.input.PlayerInput;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Consts;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;

public class CraftScene extends Scene {
    private World world;
    private Camera playerView;
    private ShaderManager shader;
    private PlayerInput playerInput;
    private Renderer2dBatch renderBatch;
    private Matrix4f model = new Matrix4f().identity();

    @Override
    public void init() {
        try {
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_STENCIL_TEST);
            TextureMapLoader mapLoader = new TextureMapLoader();
            playerView = new Camera(36.0f, Consts.CHUNK_HEIGHT + 10, 5.0f);
            playerInput = new PlayerInput(15.0f, 0.05f);
            Player.instance.setPlayerView(playerView);
            Launcher.getInputManager().setCurrentInputControl(playerInput);
            renderBatch = new Renderer2dBatch();

            shader = new ShaderManager("src\\assets\\shader\\vs.glsl","src\\assets\\shader\\fs.glsl");
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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
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
        
        Renderer2dBatch.instance.flushBatch();
    }

    @Override
    public void cleanup() {
        world.cleanup();
    }
}
