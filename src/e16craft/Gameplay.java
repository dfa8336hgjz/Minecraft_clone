package e16craft;

import core.manager.RenderManager;

public class Gameplay {
    private final RenderManager renderer;

    public Gameplay() {
        renderer = new RenderManager();
    }

    public void init() throws Exception {
        renderer.init();
    }

    public void input() {

    }

    public void update() {
        renderer.update();
    }

    public void render() {
        renderer.render();
    }

    public void cleanup() {
        renderer.cleanup();
    }

}
