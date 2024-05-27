package core.scene;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import core.components.CubeMap;
import core.launcher.Launcher;
import core.launcher.Scene;
import core.renderer.gui.Button;
import core.renderer.gui.GUIRenderer;
import core.system.texturePackage.TextureMapLoader;

public class StartScene extends Scene{
    private Button playGameButton;
    private Button quitButton;
    private GUIRenderer renderer;
    private CubeMap cubemap;
    private Matrix4f view;

    private float yRotate = 0.0f;

    @Override
    public void init() {
        try {
            renderer = new GUIRenderer();
            playGameButton = new Button();
            playGameButton.clickSprite = TextureMapLoader.getGUITexture("buttonClick");
            playGameButton.hoverSprite = TextureMapLoader.getGUITexture("buttonHover");
            playGameButton.defaultSprite = TextureMapLoader.getGUITexture("buttonDefault");
            playGameButton.position = new Vector2i(600, 500);
            playGameButton.size = new Vector2i(400, 80);
            playGameButton.textScale = 0.45f;
            playGameButton.text = "Start Game";

            quitButton = new Button();
            quitButton.clickSprite = TextureMapLoader.getGUITexture("buttonClick");
            quitButton.hoverSprite = TextureMapLoader.getGUITexture("buttonHover");
            quitButton.defaultSprite = TextureMapLoader.getGUITexture("buttonDefault");
            quitButton.position = new Vector2i(600, 650);
            quitButton.size = new Vector2i(400, 80);
            quitButton.textScale = 0.45f;
            quitButton.text = "Quit Game";
            
            cubemap = new CubeMap("src\\assets\\textures\\skybox\\menu\\Top.png",
                                "src\\assets\\textures\\skybox\\menu\\Bottom.png",
                                "src\\assets\\textures\\skybox\\menu\\Front.png",
                                "src\\assets\\textures\\skybox\\menu\\Back.png",
                                "src\\assets\\textures\\skybox\\menu\\Left.png",
                                "src\\assets\\textures\\skybox\\menu\\Right.png");

            view = new Matrix4f();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        view.identity().rotate((float) Math.toRadians(10.0f), new Vector3f(1.0f, 0.0f, 0.0f))
                        .rotate((float) Math.toRadians(yRotate), new Vector3f(0.0f, 1.0f, 0.0f))
                        .rotate((float) Math.toRadians(0.0f), new Vector3f(0.0f, 0.0f, 1.0f));
        yRotate += 10f * Launcher.instance.getDeltaTime();
        if(yRotate >= 360.0f) yRotate = 0.0f;
    }

    @Override
    public void render() {
        cubemap.render(view);
        renderer.drawSprite(350, 150, 900, 250, "logo");
        if(renderer.isButtonClicked(playGameButton)){
            Launcher.instance.changeScene(1);
        }

        if(renderer.isButtonClicked(quitButton)){
            Launcher.instance.stop();
        }

        renderer.flushBatch();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
    
}
