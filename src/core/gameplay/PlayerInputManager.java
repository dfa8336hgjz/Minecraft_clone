package core.gameplay;

import static org.lwjgl.glfw.GLFW.*;

import core.components.RayCastResult;
import core.components.World;
import core.enums.GameMode;
import core.enums.InteractMode;
import core.launcher.Launcher;
import core.renderer.OpenGlWindow;
import core.system.Input;
import core.utils.Consts;

public class PlayerInputManager {
    private final OpenGlWindow window;

    public boolean[] mouseButtonPressed;
    public boolean[] keyPressed;
    public boolean[] keyBeginPressed;
    public boolean isJumping;
    private float mouseSentivity;
    private float speed;
    private float mouseClickTime = 0.0f;
    private float mouseClickInterval = 0.3f;

    public PlayerInputManager() {
        this.window = Launcher.instance.getWindow();

        isJumping = false;
        speed = Consts.CREATIVE_Speed;
        mouseSentivity = Consts.MouseSentivity;
    }

    public void input(){
        generalInput();
        if(Player.instance.gameMode == GameMode.GUI){
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            return;
        }
        else{
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

            float deltaX = (float) (Input.currentMousePos.x - window.getWidth() / 2) * mouseSentivity;
            float deltaY = (float) (Input.currentMousePos.y - window.getHeight() / 2) * mouseSentivity;
            
            Player.instance.moveRotation(deltaY, deltaX, 0);
            glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2);

            if(Player.instance.interactMode == InteractMode.Creative){
                creativeModeInput();
                return;
            }
            else spectatorModeInput();
        }
    }

    public void generalInput(){
        if(Input.isKeyPressedOnce(GLFW_KEY_ESCAPE)){
            window.close();
        }
    }

    public void creativeModeInput(){
        float dt = (float)Launcher.instance.getDeltaTime();
        speed = Consts.CREATIVE_Speed;
        mouseClickTime -= dt;

        float velocity = speed * dt;
        int xMove = 0;
        int zMove = 0;
        if (Input.isKeyPressed(GLFW_KEY_W) || Input.isKeyPressed(GLFW_KEY_UP)) {
            zMove = -1;
        }
        if (Input.isKeyPressed(GLFW_KEY_A) || Input.isKeyPressed(GLFW_KEY_LEFT)) {
            xMove = -1;
        }
        if (Input.isKeyPressed(GLFW_KEY_S) || Input.isKeyPressed(GLFW_KEY_DOWN)) {
            zMove = 1;
        }
        if (Input.isKeyPressed(GLFW_KEY_D) || Input.isKeyPressed(GLFW_KEY_RIGHT)) {
            xMove = 1;
        }

        if (Input.isKeyPressedOnce(GLFW_KEY_SPACE)) {
            isJumping = true;
        }

        Player.instance.camera.transform.movePosition(xMove * velocity, 0, zMove * velocity);
        Player.instance.jump(dt);

        RayCastResult rayCastResult = Player.instance.checkRaycast();
        if(rayCastResult.hit){
            if (Input.isMousePressed(GLFW_MOUSE_BUTTON_LEFT) && mouseClickTime <= 0) {
                World.instance.removeBlockAt(rayCastResult.hitAtBlock);
                mouseClickTime = mouseClickInterval;
            }
    
            else if (Input.isMousePressed(GLFW_MOUSE_BUTTON_RIGHT) && mouseClickTime <= 0) {
                World.instance.addBlockAt(rayCastResult.hitPoint.add(rayCastResult.hitFaceNormal.mul(0.4f)));
                mouseClickTime = mouseClickInterval;
            }
        }
        Player.instance.slotPicking += Input.yScrollOffset;
        if(Player.instance.slotPicking > 11) Player.instance.slotPicking = 11;
        if(Player.instance.slotPicking < 0) Player.instance.slotPicking = 0;
        Input.yScrollOffset = 0;
    }

    public void spectatorModeInput(){
        speed = Consts.SPECTATOR_Speed;
        mouseClickTime -= Launcher.instance.getDeltaTime();

        float velocity = speed * (float) Launcher.instance.getDeltaTime();
        int xMove = 0;
        int yMove = 0;
        int zMove = 0;
        if (Input.isKeyPressed(GLFW_KEY_W) || Input.isKeyPressed(GLFW_KEY_UP)) {
            zMove = -1;
        }
        if (Input.isKeyPressed(GLFW_KEY_A) || Input.isKeyPressed(GLFW_KEY_LEFT)) {
            xMove = -1;
        }
        if (Input.isKeyPressed(GLFW_KEY_S) || Input.isKeyPressed(GLFW_KEY_DOWN)) {
            zMove = 1;
        }
        if (Input.isKeyPressed(GLFW_KEY_D) || Input.isKeyPressed(GLFW_KEY_RIGHT)) {
            xMove = 1;
        }

        if (Input.isKeyPressed(GLFW_KEY_SPACE)) {
            yMove = 1;
        }

        if (Input.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            yMove = -1;
        }

        Player.instance.camera.transform.movePosition(xMove * velocity, yMove * velocity, zMove * velocity);
    }

}
