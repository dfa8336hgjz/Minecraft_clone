package core.system;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector2d;

import core.components.Player;
import core.components.RayCastResult;
import core.components.World;
import core.launcher.Launcher;
import core.renderer.OpenGlWindow;
import core.utils.Consts;

public class PlayerInputManager {
    private final OpenGlWindow window;

    public Vector2d currentMousePos;
    private double xScrollOffset, yScrollOffset;

    public boolean keyIsHolding = false;
    public boolean mouseIsHolding = false;
    public boolean isGUIMode = true;
    public boolean leftButtonPressed = false;
    public boolean rightButtonPressed = false;
    public boolean mouseClick = false;
    public boolean isSpectatorMode = false;

    private float mouseSentivity;
    private float speed;
    private float mouseClickTime = 0.0f;
    private float mouseClickInterval = 0.3f;

    public PlayerInputManager() {
        currentMousePos = new Vector2d(0, 0);
        this.window = Launcher.instance.getWindow();
        init();

        speed = Consts.CREATIVE_Speed;
        mouseSentivity = Consts.MouseSentivity;
    }

    public void init() {
        glfwSetKeyCallback(this.window.getWindowHandle(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }
            
            if(action == GLFW_PRESS && !keyIsHolding){
                if(key == GLFW_KEY_K){
                    isGUIMode = !isGUIMode;
                    mouseClick = !isGUIMode;
                }
                
                if(key == GLFW_KEY_C && isGUIMode == false){
                    isSpectatorMode = !isSpectatorMode;
                }

                keyIsHolding = true;
            }
            else if(action == GLFW_RELEASE){
                keyIsHolding = false;
            }

        });

        glfwSetMouseButtonCallback(this.window.getWindowHandle(), (window, button, action, mods) -> {
            leftButtonPressed = ((button == GLFW_MOUSE_BUTTON_1) && (action == GLFW_PRESS));
            rightButtonPressed = ((button == GLFW_MOUSE_BUTTON_2) && (action == GLFW_PRESS));
        });
        
        glfwSetCursorPosCallback(this.window.getWindowHandle(), (window, xpos, ypos) -> {
            currentMousePos.x = xpos;
            currentMousePos.y = ypos;
        });

        glfwSetScrollCallback(this.window.getWindowHandle(), (window, xoffset, yoffset) -> {
            xScrollOffset = xoffset;
            yScrollOffset = yoffset;
        });
    }

    public boolean isKeyPressed(int keycode) {
        return glfwGetKey(window.getWindowHandle(), keycode) == GLFW_PRESS;
    }

    public void updateOnGUI(){
        if (leftButtonPressed) {
            if(!mouseIsHolding){
                mouseClick = true;
                mouseIsHolding = true;
            }
            else{
                mouseClick = false;
            } 
        }
        else{
            mouseClick = false;
            mouseIsHolding = false;
        }    
    }

    public void input() {
        if(isGUIMode){
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            return;
        }

        glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        float deltaX = (float) (currentMousePos.x - window.getWidth() / 2) * mouseSentivity;
        float deltaY = (float) (currentMousePos.y - window.getHeight() / 2) * mouseSentivity;
        Player.instance.moveRotation(deltaY, deltaX, 0);
        glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2);

        if(isSpectatorMode) SpectatorModeInput();
        else CreativeModeInput();
    }


    public void CreativeModeInput(){
        mouseClickTime -= Launcher.instance.getDeltaTime();
        speed = Consts.CREATIVE_Speed;

        float velocity = speed * (float) Launcher.instance.getDeltaTime();
        int xMove = 0;
        int yMove = 0;
        int zMove = 0;
        if (isKeyPressed(GLFW_KEY_W) || isKeyPressed(GLFW_KEY_UP)) {
            zMove = -1;
        }
        if (isKeyPressed(GLFW_KEY_A) || isKeyPressed(GLFW_KEY_LEFT)) {
            xMove = -1;
        }
        if (isKeyPressed(GLFW_KEY_S) || isKeyPressed(GLFW_KEY_DOWN)) {
            zMove = 1;
        }
        if (isKeyPressed(GLFW_KEY_D) || isKeyPressed(GLFW_KEY_RIGHT)) {
            xMove = 1;
        }

        if (isKeyPressed(GLFW_KEY_SPACE)) {
            yMove = 1;
        }

        if (isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            yMove = -1;
        }

        Player.instance.camera.transform.movePosition(xMove * velocity, yMove * velocity, zMove * velocity);

        RayCastResult rayCastResult = Player.instance.checkRaycast();
        if(rayCastResult.hit){
            if (leftButtonPressed && mouseClickTime <= 0) {
                World.instance.removeBlockAt(rayCastResult.hitAtBlock);
                mouseClickTime = mouseClickInterval;
            }
    
            else if (rightButtonPressed && mouseClickTime <= 0) {
                World.instance.addBlockAt(rayCastResult.hitPoint.add(rayCastResult.hitFaceNormal.mul(0.4f)));
                mouseClickTime = mouseClickInterval;
            }
        }
    }

    public void SpectatorModeInput(){
        mouseClickTime -= Launcher.instance.getDeltaTime();
        speed = Consts.SPECTATOR_Speed;

        float velocity = speed * (float) Launcher.instance.getDeltaTime();
        int xMove = 0;
        int yMove = 0;
        int zMove = 0;
        if (isKeyPressed(GLFW_KEY_W) || isKeyPressed(GLFW_KEY_UP)) {
            zMove = -1;
        }
        if (isKeyPressed(GLFW_KEY_A) || isKeyPressed(GLFW_KEY_LEFT)) {
            xMove = -1;
        }
        if (isKeyPressed(GLFW_KEY_S) || isKeyPressed(GLFW_KEY_DOWN)) {
            zMove = 1;
        }
        if (isKeyPressed(GLFW_KEY_D) || isKeyPressed(GLFW_KEY_RIGHT)) {
            xMove = 1;
        }

        if (isKeyPressed(GLFW_KEY_SPACE)) {
            yMove = 1;
        }

        if (isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            yMove = -1;
        }

        Player.instance.camera.transform.movePosition(xMove * velocity, yMove * velocity, zMove * velocity);

    }

}
