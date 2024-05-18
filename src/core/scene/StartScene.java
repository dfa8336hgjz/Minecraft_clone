package core.scene;

import core.launcher.Launcher;
import core.launcher.Scene;
import core.renderer.font.Cfont;
import core.system.input.NormalInput;
import core.renderer.font.FontBatch;

public class StartScene extends Scene{
    private NormalInput input;
    private FontBatch batch;
    private Cfont font;

    @Override
    public void init() {
        try {
            font = new Cfont("Arial", 64);
            batch = new FontBatch();
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
