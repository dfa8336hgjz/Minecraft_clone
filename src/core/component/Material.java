package core.component;

import org.joml.Vector4f;

import core.utils.Consts;

public class Material {
    private Vector4f ambientColor, diffuseColor, specularColor;
    private float reflectance;
    private int texture;

    public Material() {
        this.ambientColor = Consts.DEFAULT_COLOR;
        this.diffuseColor = Consts.DEFAULT_COLOR;
        this.specularColor = Consts.DEFAULT_COLOR;
        this.texture = -1;
        this.reflectance = 0;
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance,
            int texture) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.texture = texture;
        this.reflectance = reflectance;
    }

    public Material(Vector4f color, float reflectance) {
        this(color, color, color, reflectance, -1);
    }

    public Material(int texture) {
        this(Consts.DEFAULT_COLOR, Consts.DEFAULT_COLOR, Consts.DEFAULT_COLOR, 0, texture);
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public int getTexture() {
        return texture;
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public boolean hasTexture() {
        return texture >= 0;
    }

}
