package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import core.entity.lights.DirectionalLight;
import core.entity.lights.PointLight;
import core.entity.lights.SpotLight;
import core.utils.Consts;

import static org.lwjgl.opengl.GL20.*;

public class ShaderManager {
    public final int programID;
    private int vsID, fsID;
    private Map<String, Integer> locationMap;

    public ShaderManager() throws Exception {
        programID = glCreateProgram();
        if (programID == 0)
            throw new Exception("Failed to create shader program");

        vsID = compileShader(loadShadersFromFile(Consts.vsPath), GL_VERTEX_SHADER);
        fsID = compileShader(loadShadersFromFile(Consts.fsPath), GL_FRAGMENT_SHADER);

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

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) <= 0) {
            throw new Exception("Cannot validate shader program");
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

    // Set uniform light variable
    public void setLight(String variable, DirectionalLight light) {
        set3f(variable + ".direction", light.getDirection());
        set3f(variable + ".color", light.getColor());
        set1f(variable + ".intensity", light.getIntensity());
    }

    public void setLight(String variable, PointLight light) {
        set3f(variable + ".color", light.getColor());
        set3f(variable + ".position", light.getPosition());
        set1f(variable + ".intensity", light.getIntensity());
        set1f(variable + ".constant", light.getConstant());
        set1f(variable + ".linear", light.getLinear());
        set1f(variable + ".exponent", light.getExponent());
    }

    public void setLight(String variable, SpotLight light) {
        set3f(variable + ".coneDir", light.getConeDir());
        set1f(variable + ".cutoff", light.getCutOff());
        setLight(variable + ".pl", light.getPointLight());
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
