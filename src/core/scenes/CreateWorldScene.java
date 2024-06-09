package core.scenes;

import java.util.Random;

import org.joml.Vector2i;
import org.joml.Vector3f;

import core.components.Transform;
import core.gameplay.Player;
import core.renderer.gui.Button;
import core.renderer.gui.GUIRenderer;
import core.renderer.supporters.CubeMap;
import core.renderer.supporters.texturePackage.TextureMapLoader;
import core.renderer.terrain.ChunkUpdateManager;
import core.utils.Consts;
import core.utils.Paths;
import core.utils.Utils;

public class CreateWorldScene extends Scene {
    private CubeMap cubemap;
    private GUIRenderer renderer;
    private Button backButton;
    private Button newWorldButton;
    private Button loadWorldButton;
    private float errorDisplayTime = 0.0f;

    @Override
    public void init() {
        try {
            renderer = new GUIRenderer();

            launcher.updater = new ChunkUpdateManager();
            launcher.updater.start();

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
        if (renderer.isButtonClicked(loadWorldButton)) {
            if (launcher.readPlayerLastData()) {
                launcher.updater.beginUpdateNewChunk();
                launcher.changeScene(3);
            } else {
                errorDisplayTime = 5.0f;
            }
        }

        if (renderer.isButtonClicked(newWorldButton)) {
            launcher.worldSeed = new Random().nextInt(100);
            Utils.deleteFileInFolder(Paths.binaryFolder);

            Transform transform = new Transform(new Vector3f(0.0f, Consts.CHUNK_HEIGHT + 10.0f, 0.0f), new Vector3f(),
                    1.0f);
            Player.instance.setPlayerPos(transform);
            launcher.updater.beginUpdateNewChunk();
            launcher.changeScene(3);
        }

        if (renderer.isButtonClicked(backButton)) {
            launcher.updater.cleanup();
            launcher.changeScene(0);
        }

        if (errorDisplayTime > 0) {
            renderer.drawTextHorizontalCenter("There is no saved data", 900, 0.5f, 0xA5F9F8);
            errorDisplayTime -= launcher.getDeltaTime();
            if (errorDisplayTime < 0)
                errorDisplayTime = 0;
        }

        renderer.flushBatch();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        cubemap.cleanup();
    }

}
