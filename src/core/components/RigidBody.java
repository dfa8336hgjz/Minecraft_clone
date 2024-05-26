package core.components;

import org.joml.Vector3f;

public class RigidBody {
    public Vector3f velocity;
    public Vector3f acceleration;
    public boolean onGround;

    public RigidBody(){
        velocity = new Vector3f(0.0f);
        acceleration = new Vector3f(0.0f);
    }

    public void cleanup(){
        velocity = null;
        acceleration = null;
    }
}
