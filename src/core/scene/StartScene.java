package core.scene;

import org.joml.Vector2i;

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
            playGameButton.position = new Vector2i(0, 0);
            playGameButton.size = new Vector2i(300, 50);
            playGameButton.textScale = 0.35f;
            playGameButton.text = "Start Game";

            quitButton = new Button();
            quitButton.clickSprite = TextureMapLoader.getGUITexture("buttonClick");
            quitButton.hoverSprite = TextureMapLoader.getGUITexture("buttonHover");
            quitButton.defaultSprite = TextureMapLoader.getGUITexture("buttonDefault");
            quitButton.position = new Vector2i(250, 450);
            quitButton.size = new Vector2i(300, 50);
            quitButton.textScale = 0.35f;
            quitButton.text = "Quit Game";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        
    }

    @Override
    public void render() {
        renderer.drawSprite(400, 250, 750, 150, "logo");
        if(renderer.isButtonClicked(playGameButton)){
            Launcher.instance.changeScene(1);
        }

        // if(renderer.isButtonClicked(quitButton)){
        //     Launcher.instance.stop();
        // }

        renderer.flushBatch();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
    
}
