package core.renderer.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3f;

import core.Launcher;
import core.components.Block;
import core.gameplay.Player;
import core.renderer.supporters.ShaderManager;

public class World {
    public static World instance;
    private Map<Vector2i, Chunk> renderingChunks;
    private ChunkUpdateManager updater;

    private Vector2i playerLastPos;
    private boolean onUpdate;

    public World() {
        onUpdate = true;
        updater = Launcher.instance.updater;
        instance = this;
    }

    public void init() throws Exception {
        renderingChunks = new HashMap<Vector2i, Chunk>();
        playerLastPos = Player.instance.getPositionInChunkCoord();
    }

    public boolean shouldUpdate() {
        return !playerLastPos.equals(Player.instance.getPositionInChunkCoord());
    }

    public void render(ShaderManager shader) {
        checkUpdate();
        synchronizeChunkMap();

        ArrayList<Vector2i> keys = new ArrayList<>(renderingChunks.keySet());
        for (Vector2i pos : keys) {
            Chunk chunk = renderingChunks.get(pos);
            if (!chunk.isReadyToIn() && !chunk.isReadyToOut())
                chunk.render(shader);
        }
        keys = null;
    }

    private void checkUpdate() {
        if (shouldUpdate()) {
            playerLastPos = Player.instance.getPositionInChunkCoord();
            updater.beginUpdateNewChunk();
            onUpdate = true;
        }
    }

    private void synchronizeChunkMap() {
        if (onUpdate && updater.checkUpdateDone()) {
            onUpdate = false;
            renderingChunks = new HashMap<Vector2i, Chunk>(updater.getNewChunk());

            ArrayList<Vector2i> keys = new ArrayList<>(renderingChunks.keySet());
            for (Vector2i pos : keys) {
                Chunk chunk = renderingChunks.get(pos);
                if (chunk.isReadyToIn() && !chunk.isReadyToOut()) {
                    chunk.setReadyToIn(false);
                    chunk.prepareMesh();
                    chunk.uploadToGPU();
                } else if (chunk.isReadyToOut() && !chunk.isReadyToIn()) {
                    chunk.cleanup();
                    renderingChunks.remove(pos);
                }
            }
            keys = null;
        }
    }

    public void cleanup() {
        renderingChunks = null;
        updater.cleanup();
    }

    public Block getBlockAt(int worldX, int worldY, int worldZ) {
        int chunkX = (int) Math.floor(worldX / 16.0f);
        int chunkZ = (int) Math.floor(worldZ / 16.0f);
        Chunk currentChunk = renderingChunks.get(new Vector2i(chunkX, chunkZ));
        if (currentChunk != null) {
            return currentChunk.getBlock((int) Math.floor(worldX - chunkX * 16), worldY,
                    (int) Math.floor(worldZ - chunkZ * 16));
        }
        return null;
    }

    public Block setBlockAt(Vector3f worldPos) {
        int chunkX = (int) Math.floor(worldPos.x / 16.0f);
        int chunkZ = (int) Math.floor(worldPos.z / 16.0f);
        Chunk currentChunk = renderingChunks.get(new Vector2i(chunkX, chunkZ));
        if (currentChunk != null) {
            return currentChunk.getBlock((int) Math.floor(worldPos.x - chunkX * 16), (int) worldPos.y,
                    (int) Math.floor(worldPos.z - chunkZ * 16));
        }
        return null;
    }

    public void removeBlockAt(Vector3f worldPos) {
        int chunkX = (int) Math.floor(worldPos.x / 16.0f);
        int chunkZ = (int) Math.floor(worldPos.z / 16.0f);
        Chunk currentChunk = renderingChunks.get(new Vector2i(chunkX, chunkZ));
        if (currentChunk != null) {
            currentChunk.removeBlock((int) Math.floor(worldPos.x - chunkX * 16), (int) worldPos.y,
                    (int) Math.floor(worldPos.z - chunkZ * 16));
        }
    }

    public void addBlockAt(Vector3f worldPos, Player playerInfo) {
        try {
            Vector3f max2 = ((Vector3f) worldPos.clone()).floor().add(1, 1, 1);
            Vector3f max1 = ((Vector3f) playerInfo.camera.transform.position.clone())
                    .add(playerInfo.boxSize.x * 0.5f, playerInfo.boxSize.y * 0.5f, playerInfo.boxSize.z * 0.5f);
            Vector3f min2 = ((Vector3f) worldPos.clone()).floor();
            Vector3f min1 = ((Vector3f) playerInfo.camera.transform.position.clone())
                    .sub(playerInfo.boxSize.x * 0.5f, playerInfo.boxSize.y * 0.5f, playerInfo.boxSize.z * 0.5f);

            if ((min2.x <= max1.x && min1.x <= max2.x)
                    && (min2.y <= max1.y && min1.y <= max2.y)
                    && (min2.z <= max1.z && min1.z <= max2.z)) {
                return;
            }

        } catch (CloneNotSupportedException e) {
        }

        int chunkX = (int) Math.floor(worldPos.x / 16.0f);
        int chunkZ = (int) Math.floor(worldPos.z / 16.0f);
        Chunk currentChunk = renderingChunks.get(new Vector2i(chunkX, chunkZ));
        if (currentChunk != null) {
            currentChunk.addBlock((int) Math.floor(worldPos.x - chunkX * 16),
                    (int) worldPos.y, (int) Math.floor(worldPos.z - chunkZ * 16), playerInfo.getCurrentBlockTypeId());
        }
    }

}
