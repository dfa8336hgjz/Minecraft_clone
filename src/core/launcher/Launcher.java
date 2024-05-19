package core.launcher;

import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import core.components.Player;
import core.renderer.GPULoader;
import core.renderer.OpenGlWindow;
import core.scene.CraftScene;
import core.scene.StartScene;

public class Launcher {
    private static OpenGlWindow window;
    private static Scene currentScene;
    private static GPULoader gpuLoader;
    private Player player;

    public static final long NANOSECOND = 1000000000;
    public static final float FRAMERATE = 1000;

    private int fps;
    private static long deltaTime;
    private float frametime = 1.0f / FRAMERATE;

    private boolean isRunning;
    private GLFWErrorCallback errorCallback;

    public void start() throws Exception {
        init();
        if (isRunning)
            return;

        run();
    }

    private void init() throws Exception {
        deltaTime = 0;
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = new OpenGlWindow("pmc", 700, 900, true);
        gpuLoader = new GPULoader();
        player = new Player();
        changeScene(1);
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
            input();

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
        player.input.input();
    }

    public void stop() {
        isRunning = false;
    }

    public void update() {
        window.update();
        currentScene.update();
    }

    public void render(){
        currentScene.render();
        window.swapBuffer();
    }

    public void cleanup() {
        window.cleanup();
        gpuLoader.cleanup();
        currentScene.cleanup();
        errorCallback.free();
        glfwTerminate();
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public static void changeScene(int sceneId){
        if(currentScene != null) currentScene.cleanup();
        switch (sceneId) {
            case 0:
                currentScene = new StartScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new CraftScene();
                currentScene.init();
                break;
        
            default:
                break;
        }
    }

    public static OpenGlWindow getWindow(){
        return window;
    }

    public static GPULoader getGpuLoader(){
        return gpuLoader;
    }
}