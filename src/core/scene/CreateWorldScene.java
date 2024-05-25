package core.scene;

import org.joml.Vector2i;

import core.components.Player;
import core.launcher.Launcher;
import core.launcher.Scene;
import core.renderer.gui.Button;
import core.renderer.gui.GUIRenderer;
import core.system.texturePackage.TextureMapLoader;

public class CreateWorldScene extends Scene{
    private Button loadWorldButton;
    private Button newWorldButton;
    private Button backButton;
    private GUIRenderer renderer;

    @Override
    public void init() {
        try {
            renderer = new GUIRenderer();

            loadWorldButton = new Button();
            loadWorldButton.clickSprite = TextureMapLoader.getGUITexture("buttonClick");
            loadWorldButton.hoverSprite = TextureMapLoader.getGUITexture("buttonHover");
            loadWorldButton.defaultSprite = TextureMapLoader.getGUITexture("buttonDefault");
            loadWorldButton.position = new Vector2i(300, 350);
            loadWorldButton.size = new Vector2i(400, 80);
            loadWorldButton.textScale = 0.45f;
            loadWorldButton.text = "Load Current World";

            newWorldButton = new Button();
            newWorldButton.clickSprite = TextureMapLoader.getGUITexture("buttonClick");
            newWorldButton.hoverSprite = TextureMapLoader.getGUITexture("buttonHover");
            newWorldButton.defaultSprite = TextureMapLoader.getGUITexture("buttonDefault");
            newWorldButton.position = new Vector2i(900, 350);
            newWorldButton.size = new Vector2i(400, 80);
            newWorldButton.textScale = 0.45f;
            newWorldButton.text = "Create New World";

            backButton = new Button();
            backButton.clickSprite = TextureMapLoader.getGUITexture("buttonClick");
            backButton.hoverSprite = TextureMapLoader.getGUITexture("buttonHover");
            backButton.defaultSprite = TextureMapLoader.getGUITexture("buttonDefault");
            backButton.position = new Vector2i(600, 650);
            backButton.size = new Vector2i(400, 80);
            backButton.textScale = 0.45f;
            backButton.text = "Back";

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
        if(renderer.isButtonClicked(loadWorldButton)){
            Launcher.instance.stop();
        }

        if(renderer.isButtonClicked(newWorldButton)){
            Launcher.instance.changeScene(2);
        }

        if(renderer.isButtonClicked(backButton)){
            Launcher.instance.changeScene(0);
        }

        renderer.flushBatch();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
    
}
