package core.components;

import org.joml.Vector2i;
import org.joml.Vector3f;

import core.launcher.Launcher;
import core.utils.Consts;
import core.utils.Utils;

public class Player {
    public static Player instance;
    public Camera camera;
    private BoxCollider boxCollider;
    private RigidBody rigidBody;

    public Player(){
        instance = this;
    }

    public void setPlayerView(Camera camera){
        this.camera = camera;
        boxCollider = new BoxCollider(new Vector3f(0.6f, 3.0f, 0.6f));
        rigidBody = new RigidBody();
    }

    public Vector2i getPositionInChunkCoord(){
        Vector2i currentPos = new Vector2i();
        currentPos.x = (int)camera.transform.position.x;
        currentPos.y = (int)camera.transform.position.z;
        return currentPos.div(16);
    }

    public void update(){
        float dt = (float)Launcher.getDeltaTime();
        camera.transform.position.add(rigidBody.velocity.x * dt, rigidBody.velocity.y * dt, rigidBody.velocity.z * dt);
        rigidBody.velocity.add(rigidBody.acceleration.x * dt, rigidBody.acceleration.y * dt, rigidBody.acceleration.z * dt);
        rigidBody.velocity.sub(0.0f , Consts.GRAVITY * dt, 0.0f);
        rigidBody.velocity = Utils.clampVelocity(rigidBody.velocity);
        
        checkCollision(dt);
        
    }

    private void checkCollision(float dt){
        int minX = (int)Math.ceil(camera.transform.position.x - boxCollider.size.x * 0.5f);
        int maxX = (int)Math.ceil(camera.transform.position.x + boxCollider.size.x * 0.5f);
        int minY = (int)Math.ceil(camera.transform.position.y - boxCollider.size.y * 0.5f);
        int maxY = (int)Math.ceil(camera.transform.position.y + boxCollider.size.y * 0.5f);
        int minZ = (int)Math.ceil(camera.transform.position.z - boxCollider.size.z * 0.5f);
        int maxZ = (int)Math.ceil(camera.transform.position.z + boxCollider.size.z * 0.5f);

        for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    Block currentBlock = World.instance.getBlockAt(x - 1, y - 1, z - 1);
                    if(currentBlock!= null && !currentBlock.isNullBlock()) {
                        Vector3f overlap = resolveCollision(x - 0.5f, y - 0.5f, z - 0.5f);
                        camera.transform.position.sub(overlap);
                        rigidBody.velocity.set(0.0f);
                        rigidBody.acceleration.set(0.0f);
                    }
                }
            }    
        }

    }

    private Vector3f resolveCollision(float blockX, float blockY, float blockZ){
        Vector3f blockCollider = new Vector3f(1, 1, 1);
        blockCollider.add(this.boxCollider.size);
        float dx = camera.transform.position.x - blockX;
        float dy = camera.transform.position.y - blockY;
        float dz = camera.transform.position.z - blockZ;
        
        if (dx > 0 && dy > 0 && dz > 0)
        {
            // top-right-front quadrant
            return resolveQuadrant(blockX, blockY, blockZ, blockCollider, 1, 1, 1);
        }
        else if (dx > 0 && dy > 0 && dz < 0)
        {
            // top-right-back quadrant
            return resolveQuadrant(blockX, blockY, blockZ, blockCollider, 1, 1, -1);
        }
        else if (dx > 0 && dy < 0 && dz > 0)
        {
            // bottom-right-front quadrant
            return resolveQuadrant(blockX, blockY, blockZ, blockCollider, 1, -1, 1);
        }
        else if (dx > 0 && dy < 0 && dz < 0)
        {
            // bottom-right-back quadrant
            return resolveQuadrant(blockX, blockY, blockZ, blockCollider, 1, -1, -1);
        }
        else if (dx < 0 && dy > 0 && dz > 0)
        {
            // top-left-front quadrant
            return resolveQuadrant(blockX, blockY, blockZ, blockCollider, -1, 1, 1);
        }
        else if (dx < 0 && dy > 0 && dz < 0)
        {
            // top-left-back quadrant
            return resolveQuadrant(blockX, blockY, blockZ, blockCollider, -1, 1, -1);
        }
        else if (dx < 0 && dy < 0 && dz > 0)
        {
            // bottom-left-front quadrant
            return resolveQuadrant(blockX, blockY, blockZ, blockCollider, -1, -1, 1);
        }
        else if (dx < 0 && dy < 0 && dz < 0)
        {
            // bottom-left-back quadrant
            return resolveQuadrant(blockX, blockY, blockZ, blockCollider, -1, -1, -1);
        }

        return new Vector3f(0.0f, 0.0f, 0.0f);
    }

    private Vector3f resolveQuadrant(float blockX, float blockY, float blockZ, 
    Vector3f blockColliderExpanded, int xFactor, int yFactor, int zFactor){
        Vector3f blockColliderSizeByDirection = new Vector3f(blockColliderExpanded.x * xFactor,
                                                            blockColliderExpanded.y * yFactor,
                                                            blockColliderExpanded.z *zFactor);

        Vector3f quadrant = blockColliderSizeByDirection.mul(0.5f);
        quadrant.add(new Vector3f(blockX, blockY, blockZ));
        Vector3f delta = new Vector3f(camera.transform.position.x - quadrant.x, camera.transform.position.y - quadrant.y,
                                        camera.transform.position.z - quadrant.z);

        if (Math.abs(delta.x) < Math.abs(delta.y) && Math.abs(delta.x) < Math.abs(delta.z))
        {
            return new Vector3f(delta.x, 0.0f, 0.0f);
        }
        else if (Math.abs(delta.y) < Math.abs(delta.x) && Math.abs(delta.y) < Math.abs(delta.z))
        {
            return new Vector3f(0.0f, delta.y, 0.0f);
        }
        else
        {
            return new Vector3f(0.0f, 0.0f, delta.z);
        }
    }

}
