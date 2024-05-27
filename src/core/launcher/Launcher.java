package core.launcher;

import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import core.components.Camera;
import core.gameplay.Player;
import core.renderer.OpenGlWindow;
import core.renderer.font.Cfont;
import core.scene.CraftScene;
import core.scene.CreateWorldScene;
import core.scene.LoadingScene;
import core.scene.StartScene;
import core.system.ChunkUpdateManager;
import core.system.Input;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Consts;
import core.utils.Paths;

public class Launcher {
    public static Launcher instance;
    private OpenGlWindow window;
    private Scene currentScene;
    private Player player;
    private Matrix4f view;
    private Input input;

    public Cfont font;
    public int worldSeed;
    public ChunkUpdateManager updater;

    public final float FRAMERATE = 1000;
    public final long NANOSECOND = 1000000000;

    private int fps;
    private float yRotate;
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
        yRotate = 0;
        view = new Matrix4f();

        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = new OpenGlWindow("pmc", Consts.WINDOW_WIDTH, Consts.WINDOW_HEIGHT, true);
        updater = new ChunkUpdateManager();
        input = new Input();

        TextureMapLoader mapLoader = new TextureMapLoader();

        player = new Player();
        font = new Cfont("Arial", 64);
        changeScene(0);
    }

    public void run() {
        isRunning = true;
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
        player.input();
    }

    public void stop() {
        isRunning = false;
    }

    public void update() {
        window.update();
        player.update();
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
        }
        switch (sceneId) {
            case 0:
                currentScene = new StartScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new CreateWorldScene();
                currentScene.init();
                break;
            case 2:
                currentScene = new CraftScene();
                currentScene.init();
                break;
            case 3:
                currentScene = new LoadingScene(2);
                currentScene.init();
                break;
            case 4:
                currentScene = new LoadingScene(0);
                currentScene.init();
                break;
        
            default:
                break;
        }
    }

    public OpenGlWindow getWindow(){
        return window;
    }

    public Matrix4f updatedView(){
        yRotate += 10f * getDeltaTime();
        if(yRotate >= 360.0f) yRotate = 0.0f;
        view.identity().rotate((float) Math.toRadians(10.0f), new Vector3f(1.0f, 0.0f, 0.0f))
                        .rotate((float) Math.toRadians(yRotate), new Vector3f(0.0f, 1.0f, 0.0f))
                        .rotate((float) Math.toRadians(0.0f), new Vector3f(0.0f, 0.0f, 1.0f));
        return view;
    }


    // cam pos, rot, worldseed
    public boolean readPlayerLastData(){
        try (Reader reader = new FileReader(Paths.playerData)) {
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(reader);
            Number num = (Number)data.get("PosX"); float px = num.floatValue();
                   num = (Number)data.get("PosY"); float py = num.floatValue();
                   num = (Number)data.get("PosZ"); float pz = num.floatValue();
                   num = (Number)data.get("RotX"); float rx = num.floatValue();
                   num = (Number)data.get("RotY"); float ry = num.floatValue();
                   num = (Number)data.get("RotZ"); float rz = num.floatValue();
            player.camera = new Camera(new Vector3f(px, py + 1, pz), new Vector3f(rx, ry, rz));

            num = (Number)data.get("WorldSeed");
            worldSeed = num.intValue();

            reader.close();
        } catch (IOException | ParseException e) {
            return false;
        }

        return true;
    }

    public void writePlayerData(){
        try (FileWriter jsonFile = new FileWriter(Paths.playerData)) {
            JSONObject data = new JSONObject();
            data.put("PosX", player.camera.transform.position.x);
            data.put("PosY", player.camera.transform.position.y);
            data.put("PosZ", player.camera.transform.position.z);

            data.put("RotX", player.camera.transform.rotation.x);
            data.put("RotY", player.camera.transform.rotation.y);
            data.put("RotZ", player.camera.transform.rotation.z);

            data.put("WorldSeed", worldSeed);

            jsonFile.write(data.toJSONString());
            jsonFile.flush();
            jsonFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}