package core.components;

import org.joml.Vector3f;

public class BoxCollider {
    public Vector3f size;

    public BoxCollider(Vector3f size){
        this.size = size;
    }

    public boolean isCollidedWith(BoxCollider collider){
        return false;
    }

    public void cleanup(){
        size = null;
    }
}
