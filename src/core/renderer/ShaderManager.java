package core.renderer;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;

public class ShaderManager {
    public final int programID;
    private int vsID, fsID;
    private Map<String, Integer> locationMap;

    public ShaderManager(String vsFilePath, String fsFilePath) throws Exception {
        programID = glCreateProgram();
        if (programID == 0)
            throw new Exception("Failed to create shader program");

        vsID = compileShader(loadShadersFromFile(vsFilePath), GL_VERTEX_SHADER);
        fsID = compileShader(loadShadersFromFile(fsFilePath), GL_FRAGMENT_SHADER);

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

        // if (glGetShaderi(id, GL_COMPILE_STATUS) <= 0) {
        //     throw new RuntimeException("Failed to compile shader " + shaderType);
        // }

        // Check for compilation errors
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer compiled = stack.mallocInt(1);
            GL20.glGetShaderiv(id, GL20.GL_COMPILE_STATUS, compiled);
            if (compiled.get(0) == GL20.GL_FALSE) {
                IntBuffer maxLength = stack.mallocInt(1);
                GL20.glGetShaderiv(id, GL20.GL_INFO_LOG_LENGTH, maxLength);

                // The maxLength includes the NULL character
                ByteBuffer infoLog = stack.malloc(maxLength.get(0));
                GL20.glGetShaderInfoLog(id, maxLength, infoLog);

                // Convert the info log to a string
                byte[] infoLogBytes = new byte[infoLog.remaining()];
                infoLog.get(infoLogBytes);
                String infoLogString = new String(infoLogBytes);

                // We don't need the shader anymore
                GL20.glDeleteShader(id);

                // Log the error
                System.err.println("Shader compilation failed: " + infoLogString);
                throw new RuntimeException("Shader compilation failed!");
            }
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

    public void set4f(String variable, float x, float y, float z, float w) {
        glUniform4f(getUniformLocation(variable), x, y, z, w);
    }

    public void set4f(String variable, Vector4f vec) {
        glUniform4f(getUniformLocation(variable), vec.x, vec.y, vec.z, vec.w);
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
