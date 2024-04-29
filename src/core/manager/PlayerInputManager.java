package core.manager;

import core.component.Camera;
import core.entity.Player;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2d;

import e16craft.E16craft;

public class PlayerInputManager {
    private static PlayerViewManager playerView;
    private Vector2d currentMousePos;
    private double xScrollOffset, yScrollOffset, deltaX, deltaY;

    private boolean insideWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;
    private boolean isHolding = false;

    private float movingSpeed;
    private final MainWindow window;
    private float mouseSentivity;

    public PlayerInputManager(float speed, float mouseSentivity) {
        currentMousePos = new Vector2d(0, 0);
        this.movingSpeed = speed;
        this.mouseSentivity = mouseSentivity;

        window = E16craft.getMainWindow();
        playerView = Player.getViewController();
    }

    public void init() {
        glfwSetKeyCallback(window.getWindowHandle(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }
            
            if(key == GLFW_KEY_C){
                if(action == GLFW_PRESS && !isHolding){
                    playerView.changeMoveCamMode();
                    isHolding = true;
                }
                else if(action == GLFW_RELEASE){
                    isHolding = false;
                }
            }

        });
        
        glfwSetCursorPosCallback(E16craft.getMainWindow().getWindowHandle(), (window, xpos, ypos) -> {
            currentMousePos.x = xpos;
            currentMousePos.y = ypos;
        });

        glfwSetCursorEnterCallback(E16craft.getMainWindow().getWindowHandle(), (window, entered) -> {
            insideWindow = entered;
        });

        glfwSetMouseButtonCallback(E16craft.getMainWindow().getWindowHandle(), (window, button, action, mods) -> {
            leftButtonPressed = ((button == GLFW_MOUSE_BUTTON_1) && (action == GLFW_PRESS));
            rightButtonPressed = ((button == GLFW_MOUSE_BUTTON_2) && (action == GLFW_PRESS));
        });

        glfwSetScrollCallback(E16craft.getMainWindow().getWindowHandle(), (window, xoffset, yoffset) -> {
            xScrollOffset = xoffset;
            yScrollOffset = yoffset;
        });
    }

    public void input() {
        KeyboardInput();
        MouseInput();
    }

    public void MouseInput() {
        if(playerView.moveCamModeOn()){
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            float deltaX = (float) (currentMousePos.x - window.getWidth() / 2) * mouseSentivity;
            float deltaY = (float) (currentMousePos.y - window.getHeight() / 2) * mouseSentivity;
            playerView.rotateCam(deltaY, deltaX, 0);
            glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2);
        }
        else{
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    public void KeyboardInput() {
        if(playerView.moveCamModeOn()){
            float velocity = movingSpeed * (float) GameLauncher.getDeltaTime();
            int xMove = 0;
            int yMove = 0;
            int zMove = 0;
            if (window.isKeyPressed(GLFW_KEY_W) || window.isKeyPressed(GLFW_KEY_UP)) {
                zMove = -1;
            }
            if (window.isKeyPressed(GLFW_KEY_A) || window.isKeyPressed(GLFW_KEY_LEFT)) {
                xMove = -1;
            }
            if (window.isKeyPressed(GLFW_KEY_S) || window.isKeyPressed(GLFW_KEY_DOWN)) {
                zMove = 1;
            }
            if (window.isKeyPressed(GLFW_KEY_D) || window.isKeyPressed(GLFW_KEY_RIGHT)) {
                xMove = 1;
            }
    
            if (window.isKeyPressed(GLFW_KEY_SPACE)) {
                yMove = 1;
            }
    
            if (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
                yMove = -1;
            }
    
            playerView.moveToPositon(xMove * velocity, yMove * velocity, zMove * velocity);
        }
    }
}
