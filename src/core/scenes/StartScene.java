package core.scenes;

import org.joml.Vector2i;

import core.renderer.gui.Button;
import core.renderer.gui.GUIRenderer;
import core.renderer.supporters.CubeMap;
import core.renderer.supporters.texturePackage.TextureMapLoader;

public class StartScene extends Scene{
    private Button playGameButton;
    private Button quitButton;
    private GUIRenderer renderer;
    private CubeMap cubemap;

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void render() {
        cubemap.render(launcher.updatedView());
        renderer.drawSprite(350, 150, 900, 250, "logo");
        if(renderer.isButtonClicked(playGameButton)){
            launcher.changeScene(1);
        }

        if(renderer.isButtonClicked(quitButton)){
            launcher.stop();
        }

        renderer.flushBatch();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        cubemap.cleanup();
    }
    
}
