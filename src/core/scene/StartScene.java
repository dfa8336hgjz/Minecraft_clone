package core.scene;

import core.launcher.Launcher;
import core.launcher.Scene;
import core.renderer.font.Cfont;
import core.system.input.NormalInput;
import core.renderer.font.Batch;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.*;

public class StartScene extends Scene{
    private NormalInput input;
    private Batch batch;
    private Cfont font;

    @Override
    public void init() {
        try {
            glDisable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            font = new Cfont("Arial", 64);
            batch = new Batch();
            batch.font = font;
            batch.initBatch();

            input = new NormalInput();
            Launcher.getInputManager().setCurrentInputControl(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        
    }

    @Override
    public void render() {
        batch.addText("MINECRAFT", 200, 400, 1f, 0xFFFFFF);
        batch.addText("New Game", 200, 200, 0.5f, 0x00FFFF);
        batch.addText("Continue", 200, 150, 0.5f, 0x00FFFF);
        batch.flushBatch();
    }

    @Override
    public void cleanup() {
    }
    
}
