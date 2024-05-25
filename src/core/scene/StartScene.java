package core.scene;

import org.joml.Vector2i;

import core.components.Player;
import core.launcher.Launcher;
import core.launcher.Scene;
import core.renderer.gui.Button;
import core.renderer.gui.GUIRenderer;
import core.system.texturePackage.TextureMapLoader;

public class StartScene extends Scene{
    private Button playGameButton;
    private Button quitButton;
    private GUIRenderer renderer;

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        Player.instance.input.updateOnGUI();
    }

    @Override
    public void render() {
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
