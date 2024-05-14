package renderer;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import core.utils.Paths;

import static org.lwjgl.opengl.GL20.*;

public class ShaderManager {
    public final int programID;
    private int vsID, fsID;
    private Map<String, Integer> locationMap;

    public ShaderManager() throws Exception {
        programID = glCreateProgram();
        if (programID == 0)
            throw new Exception("Failed to create shader program");

        vsID = compileShader(loadShadersFromFile(Paths.vsPath), GL_VERTEX_SHADER);
        fsID = compileShader(loadShadersFromFile(Paths.fsPath), GL_FRAGMENT_SHADER);

    }

    private String loadShadersFromFile(String path) throws IOException {
        FileInputStream istream = new FileInputStream(path);
        StringBuilder shader = new StringBuilder();
        Scanner scanner = new Scanner(istream);

        while (scanner.hasNextLine()) {
            shader.append(scanner.nextLine());
            shader.append("\n");
        }
        scanner.close();
        istream.close();

        return shader.toString();
    }

    private int compileShader(String shader, int shaderType) throws Exception {
        int id = glCreateShader(shaderType);
        glShaderSource(id, shader);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) <= 0) {
            throw new RuntimeException("Failed to compile shader " + shaderType);
        }
        return id;
    }

    public void init() throws Exception {
        glAttachShader(programID, vsID);
        glAttachShader(programID, fsID);
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) <= 0) {
            throw new Exception("Cannot link shader program");
        }

        glDeleteShader(vsID);
        glDeleteShader(fsID);

        locationMap = new HashMap<String, Integer>();
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programID != 0)
            glDeleteProgram(programID);
    }

    public void set1i(String variable, int value) {
        glUniform1i(getUniformLocation(variable), value);
    }

    public void set1f(String variable, float value) {
        glUniform1f(getUniformLocation(variable), value);
    }

    public void set2i(String variable, int x, int y) {
        glUniform2i(getUniformLocation(variable), x, y);
    }

    public void set3f(String variable, float a, float b, float c) {
        glUniform3f(getUniformLocation(variable), a, b, c);
    }

    public void set3f(String variable, Vector3f vec) {
        glUniform3f(getUniformLocation(variable), vec.x, vec.y, vec.z);
    }

    public void setMat4f(String variable, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(getUniformLocation(variable), false, matrix.get(stack.mallocFloat(16)));
        }
    }

    private int getUniformLocation(String variable) {
        if (locationMap.containsKey(variable)) {
            return locationMap.get(variable);
        }

        int location = glGetUniformLocation(programID, variable);
        if (location < 0) {
            throw new RuntimeException("Cannot get " + variable + "'s location");
        }
        locationMap.put(variable, location);
        return location;
    }
}
