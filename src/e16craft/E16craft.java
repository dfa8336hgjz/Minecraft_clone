/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package e16craft;

import core.GameLauncher;
import core.MainWindow;

/**
 *
 * @author ASUS
 */
public class E16craft {
    private static MainWindow window;
    private static Test game;

    public static void main(String[] args) {
        window = new MainWindow("pmc", 960, 640, true);
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
