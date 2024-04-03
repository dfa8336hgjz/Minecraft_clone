package e16craft;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

import core.MainWindow;
import core.RenderManager;
import core.interfaces.IGameLogic;

public class Test implements IGameLogic {
    private final RenderManager renderer;
    private final MainWindow window;

    public Test() {
        renderer = new RenderManager();
        window = E16craft.getMainWindow();
    }

    @Override
    public void init() throws Exception {
        renderer.init();
    }

    @Override
    public void input() {

    }

    @Override
    public void update() {
        renderer.update();
    }

    @Override
    public void render() {
        renderer.render();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }

}
