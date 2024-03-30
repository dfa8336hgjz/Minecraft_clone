/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import org.lwjgl.glfw.GLFWErrorCallback;

import core.interfaces.IGameLogic;
import e16craft.E16craft;

/**
 *
 * @author ASUS
 */
public class GameLauncher {
    public static final long NANOSECOND = 1000000000;
    public static final float FRAMERATE = 1000;

    private int fps;
    private static long deltaTime;
    private float frametime = 1.0f / FRAMERATE;

    private boolean isRunning;
    private MainWindow window;
    private IGameLogic gameLogic;
    private GLFWErrorCallback errorCallback;

    private void init() throws Exception {
        deltaTime = 0;
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = E16craft.getMainWindow();
        gameLogic = E16craft.getGame();
        window.init();
        gameLogic.init();
    }

    public void start() throws Exception {
        init();
        if (isRunning)
            return;

        run();
    }

    public void run() {
        this.isRunning = true;
        int frame = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessTime = 0;

        while (isRunning) {
            boolean render = false;
            long currentTime = System.nanoTime();
            deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            unprocessTime += deltaTime / (double) NANOSECOND;
            frameCounter += deltaTime;

            while (unprocessTime > frametime) {
                unprocessTime -= frametime;
                render = true;
                if (window.shouldClose()) {
                    stop();
                }

                if (frameCounter >= NANOSECOND) {
                    setFps(frame);
                    window.setTitle("pmc " + fps);
                    frame = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update();
                render();
                frame++;
            }
        }
        cleanup();
    }

    public static double getDeltaTime() {
        return deltaTime / (double) NANOSECOND;
    }

    public void input() {
        gameLogic.input();
    }

    public void stop() {
        isRunning = false;
    }

    public void render() {
        gameLogic.render();
        window.swapBuffer();
    }

    public void update() {
        window.update();
        gameLogic.update();
    }

    public void cleanup() {
        window.cleanup();
        gameLogic.cleanup();
        errorCallback.free();
        glfwTerminate();
    }

    public void setFps(int fps) {
        this.fps = fps;
    }
}
