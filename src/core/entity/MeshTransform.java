package core.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MeshTransform {
    private Mesh mesh;
    private Vector3f position, rotation;
    private Matrix4f model;
    private float scale;

    public MeshTransform(Mesh mesh, Vector3f position, Vector3f rotation, float scale) {
        this.mesh = mesh;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.model = new Matrix4f();
    }

    public void translate(float x, float y, float z) {
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void rotate(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
        if (this.rotation.x >= 360.0f)
            this.rotation.x = 0;
        if (this.rotation.y >= 360.0f)
            this.rotation.y = 0;
        if (this.rotation.z >= 360.0f)
            this.rotation.z = 0;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Matrix4f getModelMatrix() {
        model.identity();
        model.translate(this.position)
                .rotateX((float) Math.toRadians(this.rotation.x))
                .rotateY((float) Math.toRadians(this.rotation.y))
                .rotateZ((float) Math.toRadians(this.rotation.z))
                .scale(this.scale);

        return model;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getPos() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

}
