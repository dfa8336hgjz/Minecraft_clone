package e16craft;

import core.launcher.Launcher;
//import core.generator.TextureMapGenerator;

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
