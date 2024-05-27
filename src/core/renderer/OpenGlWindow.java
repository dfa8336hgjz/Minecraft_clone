package core.renderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import core.utils.Consts;

public class OpenGlWindow {
    public float FOV = (float) Math.toRadians(60);

    private final String title;

    private int width, height;
    private long window;

    private boolean resize, vsync;
    private final Matrix4f projection;

    public OpenGlWindow(String title, int width, int height, boolean vsync) {
        this.width = width;
        this.height = height;
        this.vsync = vsync;
        this.title = title;
        this.projection = new Matrix4f();
        init();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize glfw");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Cannot create window");
        }

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResize(true);
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        glfwMakeContextCurrent(window);

        if (vsync) {
            glfwSwapInterval(1);
        }

        glfwShowWindow(window);
        GL.createCapabilities();

        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);

        glViewport(0, 0, width, height);
        glClearColor(0.5f, 0.5f, 1.0f, 1.0f);

    }

    public void update() {
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void setMouseCenter(){
        glfwSetCursorPos(window, width / 2, height / 2);
    }

    public void cleanup() {
        glfwDestroyWindow(window);
    }

    public void setClearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resize;
    }

    public boolean isVsync() {
        return vsync;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    public void setFOV(float radian) {
        FOV = radian;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Matrix4f updateProjection() {
        float aspectRatio = (float) width / height;
        return projection.setPerspective(FOV, aspectRatio, Consts.Z_NEAR, Consts.Z_FAR);
    }

    public Matrix4f updateProjection(Matrix4f matrix) {
        float aspectRatio = (float) width / height;
        return matrix.setPerspective(FOV, aspectRatio, Consts.Z_NEAR, Consts.Z_FAR);
    }

    public void swapBuffer() {
        glfwSwapBuffers(window); // swap the color buffers
    }

    public void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public long getWindowHandle() {
        return window;
    }
}
