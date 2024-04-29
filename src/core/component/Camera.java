package core.component;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position, rotation;
    private Matrix4f viewMatrix;

    public Camera() {
        this.position = new Vector3f(0.0f);
        this.rotation = new Vector3f(0.0f);
        viewMatrix = new Matrix4f();
        viewMatrix.identity();
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = new Vector3f(0.0f);
        this.rotation = new Vector3f(0.0f);
        viewMatrix = new Matrix4f();
        viewMatrix.identity();
    }

    public void movePosition(float x, float y, float z) {
        if (z != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
        }
        if (x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }
        position.y += y;
    }

    public void movePosition(Vector3f inc) {
        if (inc.z != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * inc.z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * inc.z;
        }
        if (inc.x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * inc.x;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * inc.x;
        }
        position.y = inc.y;
    }

    public void moveRotation(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    public void setPositon(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Vector3f getPosition(){
        return position;
    }

    public Matrix4f getViewMatrix() {
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1.0f, 0.0f, 0.0f))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0.0f, 1.0f, 0.0f))
                .rotate((float) Math.toRadians(rotation.z), new Vector3f(0.0f, 0.0f, 1.0f));

        viewMatrix.translate(-position.x, -position.y, -position.z);
        return viewMatrix;
    }
}
