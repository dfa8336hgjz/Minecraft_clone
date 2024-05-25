package core.scene;

import core.components.Camera;
import core.components.Player;
import core.components.World;
import core.launcher.Launcher;
import core.launcher.Scene;
import core.renderer._3DRendererBatch;
import core.renderer.BlockTextureLoader;
import core.renderer.ShaderManager;
import core.utils.Consts;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;

public class CraftScene extends Scene {
    private World world;
    private Camera playerView;
    private ShaderManager shader;
    private _3DRendererBatch renderBatch;
    private BlockTextureLoader blockTextureLoader;
    private Matrix4f model = new Matrix4f().identity();

    @Override
    public void init() {
        try {
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_STENCIL_TEST);
            playerView = new Camera(0.0f, Consts.CHUNK_HEIGHT + 10, 0.0f);
            Player.instance.setPlayerView(playerView);
            renderBatch = new _3DRendererBatch();

            shader = new ShaderManager("src\\assets\\shader\\vs.glsl","src\\assets\\shader\\fs.glsl");
            shader.init();
            shader.bind();

            world = new World();
            world.init();

            blockTextureLoader = new BlockTextureLoader();
            blockTextureLoader.generateTextureObject(shader);
            blockTextureLoader.generateTextureCoordBuffer(shader);
            shader.unbind();
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
        blockTextureLoader.bindTexture();
        shader.bind();
        shader.setMat4f("model", model);
        shader.setMat4f("view", playerView.getViewMatrix());
        shader.setMat4f("projection", Launcher.instance.getWindow().updateProjection(playerView.getViewMatrix()));
        shader.set3f("playerPos", playerView.transform.position);
        world.render(shader);
        shader.unbind();

        renderBatch.flushBatch();
    }

    @Override
    public void cleanup() {
        world.cleanup();
        blockTextureLoader.cleanup();
    }
}
