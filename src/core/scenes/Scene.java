package core.scenes;

import core.Launcher;

public abstract class Scene {
    protected Launcher launcher = Launcher.instance;
    public abstract void init();
    public abstract void update();
    public abstract void render();
    public abstract void cleanup();

}
