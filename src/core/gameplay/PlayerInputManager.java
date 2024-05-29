package core.gameplay;

import static org.lwjgl.glfw.GLFW.*;

import core.Launcher;
import core.utils.Consts;
import core.states.GameMode;
import core.states.InteractMode;
import core.renderer.terrain.World;
import core.components.RayCastResult;
import core.renderer.supporters.OpenGlWindow;

public class PlayerInputManager {
    private final OpenGlWindow window;
    private final Player player;

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
        this.player = Player.instance;

        isJumping = false;
        speed = Consts.CREATIVE_Speed;
        mouseSentivity = Consts.MouseSentivity;
    }

    public void input(){
        generalInput();
        if(player.gameMode == GameMode.GUI){
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            return;
        }
        else{
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

            float deltaX = (float) (Input.currentMousePos.x - window.getWidth() / 2) * mouseSentivity;
            float deltaY = (float) (Input.currentMousePos.y - window.getHeight() / 2) * mouseSentivity;
            
            player.moveRotation(deltaY, deltaX, 0);
            glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2);

            if(player.interactMode == InteractMode.Creative){
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
            player.velocity.y += 6;
        }

        player.velocity.x = xMove * speed;
        player.velocity.z = zMove * speed;

        RayCastResult rayCastResult = player.checkRaycast();
        if(rayCastResult.hit){
            if (Input.isMousePressed(GLFW_MOUSE_BUTTON_LEFT) && mouseClickTime <= 0) {
                World.instance.removeBlockAt(rayCastResult.hitAtBlock);
                player.modBlockSound.play();
                mouseClickTime = mouseClickInterval;
            }
       
            else if (Input.isMousePressed(GLFW_MOUSE_BUTTON_RIGHT) && mouseClickTime <= 0) {
                World.instance.addBlockAt(rayCastResult.hitPoint.add(rayCastResult.hitFaceNormal.mul(0.4f)), player);
                player.modBlockSound.play();
                mouseClickTime = mouseClickInterval;
            }
        }
        player.slotPicking += Input.yScrollOffset;
        if(player.slotPicking > 11) player.slotPicking = 11;
        if(player.slotPicking < 0) player.slotPicking = 0;
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

        player.transform.movePosition(xMove * velocity, yMove * velocity, zMove * velocity);
        player.updateCamPosition();
    }

}
