package core.components;


import org.joml.Matrix4f;
import org.joml.Vector3f;


public class Camera {
    public Transform transform;
    private Matrix4f viewMatrix;

    public Camera(){
        viewMatrix = new Matrix4f();
        viewMatrix.identity();

        transform = new Transform();
    }
    
    public Camera(Vector3f position, Vector3f rotation) {
        transform = new Transform(position, rotation, 1.0f);
        viewMatrix = new Matrix4f();
        viewMatrix.identity();
    }
    
    public Camera(float x, float y, float z) {
        transform = new Transform(new Vector3f(x, y, z), new Vector3f(0.0f), 1.0f);
        viewMatrix = new Matrix4f();
        viewMatrix.identity();
    }

    public Matrix4f getViewMatrix() {
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(transform.rotation.x), new Vector3f(1.0f, 0.0f, 0.0f))
                .rotate((float) Math.toRadians(transform.rotation.y), new Vector3f(0.0f, 1.0f, 0.0f))
                .rotate((float) Math.toRadians(transform.rotation.z), new Vector3f(0.0f, 0.0f, 1.0f));

        viewMatrix.translate(-transform.position.x, -transform.position.y, -transform.position.z);
        return viewMatrix;
    }

    public Matrix4f getViewMatrixForCubemap() {
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(transform.rotation.x), new Vector3f(1.0f, 0.0f, 0.0f))
                .rotate((float) Math.toRadians(transform.rotation.y), new Vector3f(0.0f, 1.0f, 0.0f))
                .rotate((float) Math.toRadians(transform.rotation.z), new Vector3f(0.0f, 0.0f, 1.0f));

        return viewMatrix;
    }

    public Vector3f getForwardVector(){
        viewMatrix = getViewMatrix();
        return new Vector3f(-viewMatrix.get(0, 2), -viewMatrix.get(1, 2), -viewMatrix.get(2, 2)).normalize();
    }

    public void cleanup(){
        viewMatrix = null;
        transform.cleanup();
    }
}
