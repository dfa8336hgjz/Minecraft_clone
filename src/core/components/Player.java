package core.components;

import org.joml.Vector2i;

public class Player {
    public static Player instance;
    public Camera camera;

    public Player(){
        instance = this;
    }

    public void setPlayerView(Camera camera){
        this.camera = camera;
    }

    public Vector2i getPositionInChunkCoord(){
        Vector2i currentPos = new Vector2i();
        currentPos.x = (int)camera.transform.position.x;
        currentPos.y = (int)camera.transform.position.z;
        return currentPos.div(16);
    }
}
