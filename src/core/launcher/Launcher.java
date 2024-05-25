package core.launcher;

import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import core.components.Player;
import core.renderer.OpenGlWindow;
import core.renderer.font.Cfont;
import core.scene.CraftScene;
import core.scene.StartScene;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Consts;

public class Launcher {
    public static Launcher instance;
    private OpenGlWindow window;
    private Scene currentScene;
    private Player player;

    public Cfont font;

    public final long NANOSECOND = 1000000000;
    public final float FRAMERATE = 1000;

    private int fps;
    private static long deltaTime;
    private float frametime = 1.0f / FRAMERATE;

    private boolean isRunning;
    private GLFWErrorCallback errorCallback;

    public void start() throws Exception {
        instance = this;
        init();
        if (isRunning)
            return;

        run();
    }

    private void init() throws Exception {
        deltaTime = 0;
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = new OpenGlWindow("pmc", Consts.WINDOW_WIDTH, Consts.WINDOW_HEIGHT, true);
        TextureMapLoader mapLoader = new TextureMapLoader();
        player = new Player();
        font = new Cfont("Arial", 64);
        changeScene(0);
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

    public double getDeltaTime() {
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
        currentScene.cleanup();
        errorCallback.free();
        glfwTerminate();
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void changeScene(int sceneId){
        if(currentScene != null) {
            currentScene.cleanup();
            currentScene = null;
        }
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

    public OpenGlWindow getWindow(){
        return window;
    }

}