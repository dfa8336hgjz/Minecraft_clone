package e16craft;

//import core.generator.TextureMapGenerator;
import core.manager.GameLauncher;
import core.manager.MainWindow;

public class E16craft {
    private static MainWindow window;
    private static Test game;

    public static void main(String[] args) {
        window = new MainWindow("pmc", 960, 640, true);
        // TextureMapGenerator gen = new TextureMapGenerator();
        game = new Test();
        GameLauncher launcher = new GameLauncher();
        try {
            launcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MainWindow getMainWindow() {
        return window;
    }

    public static Test getGame() {
        return game;
    }
}
