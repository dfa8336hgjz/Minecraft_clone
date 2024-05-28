package core.gameplay;
import org.joml.Vector2i;
import org.joml.Vector3f;

import core.Launcher;
import core.utils.Utils;
import core.utils.Consts;
import core.states.GameMode;
import core.components.Block;
import core.components.Camera;
import core.states.InteractMode;
import core.renderer.terrain.World;
import core.components.RayCastResult;
import core.renderer.batches._3DRendererBatch;

public class Player {
    public static Player instance;
    
    public Camera camera;
    public GameMode gameMode;
    public int slotPicking = 0;
    public PlayerInputManager input;
    public InteractMode interactMode;
    public int[] blockInventory = new int[]{
        1, 12, 3, 4, 5, 6, 8, 10, 11, 2, 13, 14
    };

    public boolean onGround;
    public Vector3f boxSize;
    public Vector3f velocity;
    private float jumpHeight = 2.0f;
    private final int maxRaycastDistance = 5;
    private float fallingAcceleration = Consts.GRAVITY;

    public Player(){
        instance = this;
        gameMode = GameMode.GUI;
        interactMode = InteractMode.Creative;
        input = new PlayerInputManager();
        
        boxSize = new Vector3f(0.6f, 2.5f, 0.6f);
        velocity = new Vector3f();
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

    public void input(){
        input.input();
    }

    public void update(){
        float dt = (float)Launcher.instance.getDeltaTime();
        if(gameMode != GameMode.GUI && interactMode == InteractMode.Creative){
            gravityOn(dt);
            try {
                checkCollision(dt);
            } catch (CloneNotSupportedException e) {
                return;
            }
        }
    }

    public void jump(float dt){
        if(input.isJumping){
            camera.transform.position.add(0 , 10 * dt, 0);
            jumpHeight -= 10 * dt;
            if(jumpHeight < 0.0f){
                input.isJumping = false;
            }
            return;
        }
        jumpHeight = 2.0f;
    }

    public int getCurrentBlockTypeId(){
        return blockInventory[slotPicking];
    }

    public void moveRotation(float x, float y, float z){
        Vector3f rot = camera.transform.rotation;
        if(rot.x + x >= 90 || rot.x + x < -90) return;
        if(rot.z + z >= 90 || rot.z + z < -90) return;
        camera.transform.moveRotation(x, y, z);
    }

    private void gravityOn(float dt){
        camera.transform.position.add(velocity.x * dt, velocity.y * dt, velocity.z * dt);
        velocity.sub(0.0f , fallingAcceleration * dt, 0.0f);
        velocity = Utils.clampVelocity(velocity);
    }

    private void checkCollision(float dt) throws CloneNotSupportedException{
        int minX = (int)Math.floor(camera.transform.position.x - boxSize.x * 0.5f);
        int maxX = (int)Math.floor(camera.transform.position.x + boxSize.x * 0.5f);
        int minY = (int)Math.floor(camera.transform.position.y - boxSize.y * 0.5f);
        int maxY = (int)Math.floor(camera.transform.position.y + boxSize.y * 0.5f);
        int minZ = (int)Math.floor(camera.transform.position.z - boxSize.z * 0.5f);
        int maxZ = (int)Math.floor(camera.transform.position.z + boxSize.z * 0.5f);

        boolean didCollide = false;
        for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    Block currentBlock = World.instance.getBlockAt(x, y, z);
                    if(currentBlock!= null && !currentBlock.isNullBlock() &&
                    isColliding(boxSize, camera.transform.position, new Vector3f(1.0f), new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f))) {
                        CollisionTestResult result = resolveCollision(x + 0.5f, y + 0.5f, z + 0.5f);

                        Vector3f normOverlap = ((Vector3f)result.overlap.clone()).normalize();
                        Vector3f normVelocity = ((Vector3f)velocity.clone()).normalize();
                        float dotProduct = normOverlap.dot(normVelocity);
                        if(dotProduct < 0){
                            continue;
                        }
                        
                        camera.transform.position.sub(result.overlap);
                        didCollide = true;
                    }
                }
            }    
        }

        if(!didCollide && onGround && velocity.y > 0){
            onGround = false;
        }
    }

    public boolean isColliding(Vector3f box1, Vector3f pos1, Vector3f box2, Vector3f pos2){
        for (int i = 0; i < 3; i++) {
            float penetration = penetrationAmount(box1, pos1, box2, pos2, i);
            if(penetration <= 0.001f){
                return false;
            }
        }

        return true;
    }

    public float penetrationAmount(Vector3f box1, Vector3f pos1, Vector3f box2, Vector3f pos2, int textAxes){
        try {
            Vector3f min1 = ((Vector3f)pos1.clone()).sub(new Vector3f(box1.x * 0.5f, box1.y * 0.5f, box1.z * 0.5f));
            Vector3f max1 = ((Vector3f)pos1.clone()).add(new Vector3f(box1.x * 0.5f, box1.y * 0.5f, box1.z * 0.5f));
            Vector3f min2 = ((Vector3f)pos2.clone()).sub(new Vector3f(box2.x * 0.5f, box2.y * 0.5f, box2.z * 0.5f));
            Vector3f max2 = ((Vector3f)pos2.clone()).add(new Vector3f(box2.x * 0.5f, box2.y * 0.5f, box2.z * 0.5f));

            // x
            if(textAxes == 0){
                if(min2.x <= max1.x && min1.x <= max2.x){
                    return max1.x - min2.x;
                }
            }
            // y
            else if (textAxes == 1){
                if(min2.y <= max1.y && min1.y <= max2.y){
                    return max1.y - min2.y;
                }
            }
            // z
            else if (textAxes == 2){
                if(min2.z <= max1.z && min1.z <= max2.z){
                    return max1.z - min2.z;
                }
            }

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return 0.0f;
    }

    private CollisionTestResult resolveCollision(float blockX, float blockY, float blockZ){
        CollisionTestResult res = new CollisionTestResult();
        Vector3f blockCollider = new Vector3f(1, 1, 1);
        blockCollider.add(this.boxSize);

        // center to center
        float dx = camera.transform.position.x - blockX;
        float dy = camera.transform.position.y - blockY;
        float dz = camera.transform.position.z - blockZ;
        
        if (dx > 0 && dy > 0 && dz > 0)
        {
            // top right front
            resolveQuadrant(res, blockX, blockY, blockZ, blockCollider, 1, 1, 1);
        }
        else if (dx > 0 && dy > 0 && dz <= 0)
        {
            // top right back
            resolveQuadrant(res, blockX, blockY, blockZ, blockCollider, 1, 1, -1);
        }
        else if (dx > 0 && dy <= 0 && dz > 0)
        {
            // bottom right front
            resolveQuadrant(res, blockX, blockY, blockZ, blockCollider, 1, -1, 1);
        }
        else if (dx > 0 && dy <= 0 && dz <= 0)
        {
            // bottom right back
            resolveQuadrant(res, blockX, blockY, blockZ, blockCollider, 1, -1, -1);
        }
        else if (dx <= 0 && dy > 0 && dz > 0)
        {
            // top left front
            resolveQuadrant(res, blockX, blockY, blockZ, blockCollider, -1, 1, 1);
        }
        else if (dx <= 0 && dy > 0 && dz <= 0)
        {
            // top left back
            resolveQuadrant(res, blockX, blockY, blockZ, blockCollider, -1, 1, -1);
        }
        else if (dx <= 0 && dy <= 0 && dz > 0)
        {
            // bottom left front
            resolveQuadrant(res, blockX, blockY, blockZ, blockCollider, -1, -1, 1);
        }
        else if (dx <= 0 && dy <= 0 && dz <= 0)
        {
            // bottom left back
            resolveQuadrant(res, blockX, blockY, blockZ, blockCollider, -1, -1, -1);
        }

        return res;
    }

    private void resolveQuadrant(CollisionTestResult result, float blockX, float blockY, float blockZ, 
    Vector3f blockColliderExpanded, int xDirection, int yDirection, int zDirection){
        Vector3f blockColliderSizeByDirection = new Vector3f(blockColliderExpanded.x * xDirection,
                                                            blockColliderExpanded.y * yDirection,
                                                            blockColliderExpanded.z *zDirection);

        Vector3f quadrant = blockColliderSizeByDirection.mul(0.5f).add(new Vector3f(blockX, blockY, blockZ));
        
        Vector3f delta = new Vector3f(camera.transform.position.x - quadrant.x, camera.transform.position.y - quadrant.y,
                                        camera.transform.position.z - quadrant.z);

        if (Math.abs(delta.x) < Math.abs(delta.y) && Math.abs(delta.x) < Math.abs(delta.z))
        {
            velocity.x = 0.0f;
            result.overlap = new Vector3f(delta.x, 0.0f, 0.0f);
        }
        else if (Math.abs(delta.y) < Math.abs(delta.x) && Math.abs(delta.y) < Math.abs(delta.z))
        {
            velocity.y = 0.0f;
            result.overlap = new Vector3f(0.0f, delta.y, 0.0f);
            onGround = onGround || yDirection == -1;
        }
        else
        {
            velocity.z = 0.0f;
            result.overlap = new Vector3f(0.0f, 0.0f, delta.z);
        }
    }

    public RayCastResult checkRaycast(){
        RayCastResult result = new RayCastResult();
        result.hit = false;
        try {
            Vector3f direction = camera.getForwardVector();
            Vector3f currentOrigin = (Vector3f)camera.transform.position.clone();
            Vector3f pointOnRay = (Vector3f)camera.transform.position.clone();

            for (float i = 0f; i < maxRaycastDistance; i+= 0.1f) {
                    Vector3f pointOnRayCopy = (Vector3f)pointOnRay.clone();
                    if(pointOnRayCopy.floor() != currentOrigin)
                    {
                        currentOrigin = pointOnRayCopy;
                        Block currentBlock = World.instance.getBlockAt((int)currentOrigin.x, (int)currentOrigin.y, (int)currentOrigin.z);
                        if(currentBlock != null && !currentBlock.isNullBlock()){
                            Vector3f max = ((Vector3f)currentOrigin.clone()).add(1.0f, 1.0f, 1.0f);
                            Vector3f min = (Vector3f)currentOrigin.clone();

                            float t1 = (min.x - camera.transform.position.x) / direction.x;
                            float t2 = (max.x - camera.transform.position.x) / direction.x;

                            float t3 = (min.y - camera.transform.position.y) / direction.y;
                            float t4 = (max.y - camera.transform.position.y) / direction.y;

                            float t5 = (min.z - camera.transform.position.z) / direction.z;
                            float t6 = (max.z - camera.transform.position.z) / direction.z;

                            float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
                            float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));
                            if (tmax < 0 || tmin > tmax) 
                            { 
                                // No intersection
                                return result;
                            }
                            float depth = 0.0f;
                            if (tmin < 0.0f)
                            {
                                // The ray's origin is inside the AABB
                                depth = tmax;
                            }
                            else
                            {
                                depth = tmin;
                            }

                            result.hitPoint = new Vector3f(camera.transform.position.x + direction.x * depth,
                                                        camera.transform.position.y + direction.y * depth,
                                                        camera.transform.position.z + direction.z * depth);
                            result.hit = true;
                            result.hitFaceNormal = new Vector3f(result.hitPoint.x - currentOrigin.x - 0.5f,
                                                                result.hitPoint.y - currentOrigin.y - 0.5f,
                                                                result.hitPoint.z - currentOrigin.z - 0.5f);
                            float maxSide = Math.max(
                                Math.max(Math.abs(result.hitFaceNormal.x), Math.abs(result.hitFaceNormal.y)),
                                Math.abs(result.hitFaceNormal.z));
                            
                            result.hitFaceNormal = (Math.abs(result.hitFaceNormal.x) == maxSide) ? new Vector3f(Math.signum(result.hitFaceNormal.x), 0, 0):
                                                    (Math.abs(result.hitFaceNormal.y) == maxSide) ? new Vector3f(0, Math.signum(result.hitFaceNormal.y), 0):
                                                    new Vector3f(0, 0, Math.signum(result.hitFaceNormal.z));
                                                
                            result.hitAtBlock = currentOrigin;

                            _3DRendererBatch.instance.drawBox(currentOrigin.add(0.5f, 0.5f, 0.5f), new Vector3f(1.0f));
                            return result;
                        }
                    }
                    pointOnRay.add(direction.x * 0.1f, direction.y * 0.1f, direction.z * 0.1f);
                }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }
}


class CollisionTestResult{
    Vector3f overlap;
}