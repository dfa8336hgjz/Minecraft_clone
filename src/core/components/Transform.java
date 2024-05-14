package core.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f position, rotation;
    public Vector3f up, right, forward;
    public float scale;
    public Matrix4f modelMatrix;

    public Transform(){
        position = new Vector3f(0.0f, 0.0f, 0.0f);
        rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        modelMatrix = new Matrix4f();
        modelMatrix.identity();
    }

    public Transform(Vector3f position, Vector3f rotation, float scale){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        modelMatrix = new Matrix4f();
        modelMatrix.identity();
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
        if (this.rotation.x >= 360.0f)
            this.rotation.x = 0;
        if (this.rotation.y >= 360.0f)
            this.rotation.y = 0;
        if (this.rotation.z >= 360.0f)
            this.rotation.z = 0;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void setScale(float scale){
        this.scale = scale;
    }

    public Vector3f getPosition(){
        return position;
    }

    public Matrix4f getModelMatrix() {
        modelMatrix.identity();
        modelMatrix.translate(this.position)
                .rotateX((float) Math.toRadians(this.rotation.x))
                .rotateY((float) Math.toRadians(this.rotation.y))
                .rotateZ((float) Math.toRadians(this.rotation.z))
                .scale(this.scale);

        return modelMatrix;
    }
}
