package core.system.input;

import org.joml.Vector2d;

import renderer.OpenGlWindow;

public abstract class InputController {
    public void KeyboardInput(OpenGlWindow window){};
    public void MouseInput(Vector2d currentMousePos, OpenGlWindow window){};
    public void ScrollInput(double xScrollOffset, double yScrollOffset, OpenGlWindow window){};
}
