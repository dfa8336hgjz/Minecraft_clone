package G3craft;

import core.Launcher;

public class Main {
    public static void main(String[] args) {
        //TextureMapGenerator gen = new TextureMapGenerator();
        Launcher launcher = new Launcher();
        try {
            launcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
