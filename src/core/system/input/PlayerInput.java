package core.system.input;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector2d;

import core.components.Player;
import core.launcher.Launcher;
import core.renderer.OpenGlWindow;
import core.system.InputManager;

public class PlayerInput extends InputController{
    private Player player;
    private final InputManager inputManager;
    private float mouseSentivity;
    private float speed;

    public PlayerInput(float speed, float mouseSentivity) {
        this.player = Player.instance;
        this.speed = speed;
        this.mouseSentivity = mouseSentivity;
        inputManager = Launcher.getInputManager();
    }

    @Override
    public void MouseInput(Vector2d currentMousePos, OpenGlWindow window) {
        if(inputManager.playModeOn()){
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            float deltaX = (float) (currentMousePos.x - Launcher.getWindow().getWidth() / 2) * mouseSentivity;
            float deltaY = (float) (currentMousePos.y - Launcher.getWindow().getHeight() / 2) * mouseSentivity;
            player.camera.transform.moveRotation(deltaY, deltaX, 0);
            glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2);
        }
        else glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    @Override
    public void KeyboardInput(OpenGlWindow window) {
        if(Launcher.getInputManager().playModeOn()){
            float velocity = speed * (float) Launcher.getDeltaTime();
            int xMove = 0;
            int yMove = 0;
            int zMove = 0;
            if (inputManager.isKeyPressed(GLFW_KEY_W) || inputManager.isKeyPressed(GLFW_KEY_UP)) {
                zMove = -1;
            }
            if (inputManager.isKeyPressed(GLFW_KEY_A) || inputManager.isKeyPressed(GLFW_KEY_LEFT)) {
                xMove = -1;
            }
            if (inputManager.isKeyPressed(GLFW_KEY_S) || inputManager.isKeyPressed(GLFW_KEY_DOWN)) {
                zMove = 1;
            }
            if (inputManager.isKeyPressed(GLFW_KEY_D) || inputManager.isKeyPressed(GLFW_KEY_RIGHT)) {
                xMove = 1;
            }
    
            if (inputManager.isKeyPressed(GLFW_KEY_SPACE)) {
                yMove = 1;
            }
    
            if (inputManager.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
                yMove = -1;
            }
    
            player.camera.transform.movePosition(xMove * velocity, yMove * velocity, zMove * velocity);
        }
    }
}
