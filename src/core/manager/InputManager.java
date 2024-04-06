package core.manager;

import core.entity.Camera;
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2d;

import e16craft.E16craft;

public class InputManager {
    private Vector2d currentMousePos;
    private double xScrollOffset, yScrollOffset, deltaX, deltaY;

    private boolean insideWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    private float movingSpeed;
    private final MainWindow window;
    private float mouseSentivity;

    public InputManager(float speed, float mouseSentivity) {
        currentMousePos = new Vector2d(0, 0);
        this.movingSpeed = speed;
        this.mouseSentivity = mouseSentivity;
        window = E16craft.getMainWindow();
    }

    public void init() {
        glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
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

    public void input(Camera camera) {
        KeyboardInput(camera);
        MouseInput(camera);
    }

    public void MouseInput(Camera camera) {
        float deltaX = (float) (currentMousePos.x - window.getWidth() / 2) * mouseSentivity;
        float deltaY = (float) (currentMousePos.y - window.getHeight() / 2) * mouseSentivity;
        camera.moveRotation(deltaY, deltaX, 0.0f);
        glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2);
    }

    public void KeyboardInput(Camera camera) {
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

        if (window.isKeyPressed(GLFW_KEY_BACKSPACE)) {
            yMove = -1;
        }
        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
            window.close();
        }

        camera.movePosition(xMove * velocity, yMove * velocity, zMove * velocity);
    }
}
