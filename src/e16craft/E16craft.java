/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package e16craft;

import core.generator.TextureMapGenerator;
import core.manager.GameLauncher;
import core.manager.MainWindow;

/**
 *
 * @author ASUS
 */
public class E16craft {
    private static MainWindow window;
    private static Test game;
    private static TextureMapGenerator gen;

    public static void main(String[] args) {
        window = new MainWindow("pmc", 960, 640, true);
        game = new Test();
        GameLauncher launcher = new GameLauncher();
        gen = new TextureMapGenerator();
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
