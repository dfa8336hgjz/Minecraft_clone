package core.manager;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import core.component.Camera;

public class PlayerViewManager {
    private Camera camera;
    private boolean moveCamMode = true;

    public PlayerViewManager(float x0, float y0, float z0){
        camera = new Camera();
        camera.movePosition(x0, y0, z0);
    }

    public void init(){

    }

    public Matrix4f getViewMatrix(){
        return camera.getViewMatrix();
    }

    public Vector3f getCamPosition(){
        return camera.getPosition();
    }

    public void moveToPositon(float x, float y, float z){
        camera.movePosition(x, y, z);
    }

    public void rotateCam(float x, float y, float z){
        camera.moveRotation(x, y, z);
    }
    
    public boolean moveCamModeOn(){
        return moveCamMode;
    }

    public void changeMoveCamMode(){
        moveCamMode = !moveCamMode;
    }
}
