package e16craft;

import core.MainWindow;
import core.MeshLoader;
import core.RenderManager;
import core.interfaces.IGameLogic;
import entity.Mesh;

/**
 *
 * @author ASUS
 */
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
