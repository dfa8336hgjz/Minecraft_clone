package core.scene;

import core.components.Camera;
import core.components.CubeMap;
import core.components.World;
import core.enums.GameMode;
import core.enums.InteractMode;
import core.gameplay.Player;
import core.launcher.Launcher;
import core.launcher.Scene;
import core.renderer._3DRendererBatch;
import core.renderer.gui.Button;
import core.renderer.gui.CraftGUIRenderer;
import core.system.Input;
import core.system.texturePackage.TextureMapLoader;
import core.renderer.BlockTextureLoader;
import core.renderer.ShaderManager;
import core.utils.Consts;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector2i;

public class CraftScene extends Scene {
    private World world;
    private Camera playerView;
    private CubeMap cubeMap;
    private ShaderManager shader;
    private _3DRendererBatch renderBatch;
    private BlockTextureLoader blockTextureLoader;
    private Matrix4f model = new Matrix4f().identity();

    private CraftGUIRenderer guiRenderer;
    private Button quitButton;

    @Override
    public void init() {
        try {
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_STENCIL_TEST);
            playerView = new Camera(0.0f, Consts.CHUNK_HEIGHT + 20.0f, 0.0f);
            Player.instance.setPlayerView(playerView);
            Player.instance.gameMode = GameMode.Interact;
            Player.instance.interactMode = InteractMode.Creative;
            glfwSetCursorPos(Launcher.instance.getWindow().getWindowHandle(), (double)Consts.WINDOW_WIDTH / 2, (double)Consts.WINDOW_HEIGHT / 2 - 1);
            
            renderBatch = new _3DRendererBatch();
            guiRenderer = new CraftGUIRenderer();

            shader = new ShaderManager("src\\assets\\shader\\vs.glsl","src\\assets\\shader\\fs.glsl");
            shader.init();
            shader.bind();

            world = new World();
            world.init();

            blockTextureLoader = new BlockTextureLoader();
            blockTextureLoader.generateTextureObject(shader);
            blockTextureLoader.generateTextureCoordBuffer(shader);
            shader.unbind();

            cubeMap =  new CubeMap("src\\assets\\textures\\skybox\\craft\\dayTop.png",
                            "src\\assets\\textures\\skybox\\craft\\dayBottom.png",
                            "src\\assets\\textures\\skybox\\craft\\dayBack.png",
                            "src\\assets\\textures\\skybox\\craft\\dayBack.png",
                            "src\\assets\\textures\\skybox\\craft\\dayBack.png",
                            "src\\assets\\textures\\skybox\\craft\\dayBack.png");

            quitButton = new Button();
            quitButton.clickSprite = TextureMapLoader.getGUITexture("buttonClick");
            quitButton.hoverSprite = TextureMapLoader.getGUITexture("buttonHover");
            quitButton.defaultSprite = TextureMapLoader.getGUITexture("buttonDefault");
            quitButton.position = new Vector2i(550, 420);
            quitButton.size = new Vector2i(500, 120);
            quitButton.textScale = 0.45f;
            quitButton.text = "Back to Main Screen";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if(Input.isKeyPressedOnce(GLFW_KEY_K)){
            switch (Player.instance.gameMode) {
            case GUI:
                Player.instance.gameMode = GameMode.Interact;
                break;
            case Interact:
                Player.instance.gameMode = GameMode.GUI;
                break;
            default:
                break;
            }

        }
        
        if(Input.isKeyPressedOnce(GLFW_KEY_C) && Player.instance.gameMode == GameMode.Interact){
            if(Player.instance.interactMode == InteractMode.Creative)
                Player.instance.interactMode = InteractMode.Spectator;
            else Player.instance.interactMode = InteractMode.Creative;
        }

    }

    @Override
    public void render(){
        Player.instance.update();
        Matrix4f view = new Matrix4f(playerView.getViewMatrix());
        Matrix4f projection = new Matrix4f(Launcher.instance.getWindow().updateProjection(playerView.getViewMatrix()));
        cubeMap.render(playerView.getViewMatrixForCubemap());
        
        blockTextureLoader.bindTexture();
        shader.bind();
        shader.setMat4f("model", model);
        shader.setMat4f("view", view);
        shader.setMat4f("projection", projection);
        shader.set3f("playerPos", playerView.transform.position);
        world.render(shader);
        shader.unbind();

        renderBatch.flushBatch();

        if(Player.instance.gameMode == GameMode.GUI){
            if(guiRenderer.isButtonClicked(quitButton)){
                Launcher.instance.changeScene(0);
            }
        }
        guiRenderer.draw();

        guiRenderer.flushBatch();
    }

    @Override
    public void cleanup() {
        world.cleanup();
        blockTextureLoader.cleanup();
    }
}
