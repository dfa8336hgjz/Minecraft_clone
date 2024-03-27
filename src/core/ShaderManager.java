/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

/**
 *
 * @author ASUS
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import utils.Consts;

import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author ASUS
 */
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

    public void setU1i(String variable, int value) {
        glUniform1i(getUniformLocation(variable), value);
    }

    public void set1f(String variable, float value) {
        glUniform1f(getUniformLocation(variable), value);
    }

    public void set3f(String variable, float a, float b, float c) {
        glUniform3f(getUniformLocation(variable), a, b, c);
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
