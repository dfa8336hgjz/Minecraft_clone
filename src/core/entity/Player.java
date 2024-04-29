package core.entity;

import org.joml.Matrix4f;
import org.joml.Vector2i;

import core.manager.PlayerInputManager;
import core.manager.PlayerViewManager;

public class Player {
    private PlayerInputManager input;
    private static PlayerViewManager view;

    public Player(float x0, float y0, float z0){
        view = new PlayerViewManager(x0, y0, z0);
        input = new PlayerInputManager(15.0f, 0.03f);
    }

    public void init(){
        input.init();
    }

    public void update(){
        input.input();
    }

    public Matrix4f getView(){
        return view.getViewMatrix();
    }

    public static PlayerViewManager getViewController(){
        return view;
    }

    public static Vector2i getCamPositionInChunkCoord(){
        Vector2i currentPos = new Vector2i();
        currentPos.x = (int)view.getCamPosition().x;
        currentPos.y = (int)view.getCamPosition().z;
        return currentPos.div(16);
    }
}
