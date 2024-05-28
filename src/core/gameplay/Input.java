package core.gameplay;

import org.joml.Vector2d;
import static org.lwjgl.glfw.GLFW.*;

import core.Launcher;
import core.renderer.supporters.OpenGlWindow;

public class Input {
    private final OpenGlWindow window;

    public static Vector2d currentMousePos;
    public static float yScrollOffset;
    public static boolean[] mouseButtonPressed;
    public static boolean[] keyPressed;

    public Input() {
        mouseButtonPressed = new boolean[GLFW_MOUSE_BUTTON_LAST];
        keyPressed = new boolean[GLFW_KEY_LAST];
        currentMousePos = new Vector2d(0, 0);
        this.window = Launcher.instance.getWindow();
        init();
    }

    public void init() {
        glfwSetKeyCallback(this.window.getWindowHandle(), (window, key, scancode, action, mods) -> {
            if(key >= 0 && key < GLFW_KEY_LAST){
                if(action == GLFW_PRESS){
                    keyPressed[key] = true;
                }
    
                else if(action == GLFW_RELEASE){
                    keyPressed[key] = false;
                }
            }

        });

        glfwSetMouseButtonCallback(this.window.getWindowHandle(), (window, button, action, mods) -> {
            if(action == GLFW_PRESS){
                mouseButtonPressed[button] = true;
            }
            else if(action == GLFW_RELEASE){
                mouseButtonPressed[button] = false;
            }
        });
        
        glfwSetCursorPosCallback(this.window.getWindowHandle(), (window, xpos, ypos) -> {
            currentMousePos.x = xpos;
            currentMousePos.y = ypos;
        });

        glfwSetScrollCallback(this.window.getWindowHandle(), (window, xoffset, yoffset) -> {
            yScrollOffset = (float)yoffset;
        });
    }

    public static boolean isKeyPressedOnce(int key){
        if(keyPressed[key]) {
            keyPressed[key] = false;
            return true;
        }

        return false;
    }

    public static boolean isKeyPressed(int key){
        return keyPressed[key];
    }

    public static boolean isMousePressed(int button){
        if(mouseButtonPressed[button]) {
            mouseButtonPressed[button] = false;
            return true;
        }

        return false;
    }
}
