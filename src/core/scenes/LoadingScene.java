package core.scenes;

import org.joml.Vector2f;

import core.renderer.batches.FontBatch;
import core.renderer.batches._2DRendererBatch;
import core.renderer.supporters.texturePackage.TextureData;
import core.utils.Consts;
import core.utils.Paths;

public class LoadingScene extends Scene {
    private _2DRendererBatch renderer2d;
    private FontBatch rendererFont;
    private TextureData textureUV;
    private float dotNum = 1;
    private float runTime = 3;
    private int nextScene;

    public LoadingScene(int nextScene) {
        this.nextScene = nextScene;
    }

    @Override
    public void init() {
        try {
            renderer2d = new _2DRendererBatch();
            rendererFont = new FontBatch();
            renderer2d.initBatch(Paths.loadingBackgroundTexture, 1);
            rendererFont.initBatch();

            textureUV = new TextureData("", new Vector2f(0, 0), new Vector2f(0, 1), new Vector2f(1, 0),
                    new Vector2f(1, 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        runTime -= launcher.getDeltaTime();
        if (runTime < 0)
            launcher.changeScene(this.nextScene);
    }

    @Override
    public void render() {
        dotNum += launcher.getDeltaTime();
        if (dotNum > 4)
            dotNum = 1;
        renderer2d.drawSprite(0, 0, Consts.WINDOW_WIDTH, Consts.WINDOW_HEIGHT, textureUV);
        rendererFont.drawTextHorizontalCenter("Loading" + ".".repeat((int) dotNum), 460, 1.2f, 0xFFFFFF);

        renderer2d.flushBatch();
        rendererFont.flushBatch();
    }

    @Override
    public void cleanup() {
        launcher.backgroundMusic.stop();
        renderer2d.cleanup();
        rendererFont.cleanup();
    }
}
