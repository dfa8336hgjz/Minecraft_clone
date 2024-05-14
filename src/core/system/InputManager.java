package core.system;

import renderer.OpenGlWindow;
import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector2d;

import core.launcher.Launcher;
import core.system.input.InputController;

public class InputManager {
    private final OpenGlWindow window;
    private InputController currentController;

    private Vector2d currentMousePos;
    private double xScrollOffset, yScrollOffset;

    private boolean insideWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;
    private boolean isHolding = false;
    private boolean playMode = true;

    public InputManager() {
        currentMousePos = new Vector2d(0, 0);
        this.window = Launcher.getWindow();
    }

    public void init() {
        glfwSetKeyCallback(this.window.getWindowHandle(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }
            
            if(action == GLFW_PRESS && !isHolding){
                
                if(key == GLFW_KEY_C){
                    playMode = !playMode;
                }
                isHolding = true;
            }
            else if(action == GLFW_RELEASE){
                isHolding = false;
            }

        });
        
        glfwSetCursorPosCallback(this.window.getWindowHandle(), (window, xpos, ypos) -> {
            currentMousePos.x = xpos;
            currentMousePos.y = ypos;
        });

        glfwSetCursorEnterCallback(this.window.getWindowHandle(), (window, entered) -> {
            insideWindow = entered;
        });

        glfwSetMouseButtonCallback(this.window.getWindowHandle(), (window, button, action, mods) -> {
            leftButtonPressed = ((button == GLFW_MOUSE_BUTTON_1) && (action == GLFW_PRESS));
            rightButtonPressed = ((button == GLFW_MOUSE_BUTTON_2) && (action == GLFW_PRESS));
        });

        glfwSetScrollCallback(this.window.getWindowHandle(), (window, xoffset, yoffset) -> {
            xScrollOffset = xoffset;
            yScrollOffset = yoffset;
        });
    }

    public boolean isKeyPressed(int keycode) {
        return glfwGetKey(window.getWindowHandle(), keycode) == GLFW_PRESS;
    }

    public void input() {
        currentController.MouseInput(currentMousePos, window);
        currentController.KeyboardInput(window);
    }

    public Vector2d getCurrentMousePos(){
        return currentMousePos;
    }

    public boolean playModeOn(){
        return playMode;
    }

    public void setCurrentInputControl(InputController controller){
        currentController = controller;
    }
}
