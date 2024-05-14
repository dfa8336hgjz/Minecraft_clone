package core.components;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.joml.Random;
import org.joml.Vector2i;

import core.system.ChunkUpdateManager;
import renderer.ShaderManager;
import core.utils.Paths;

public class World {
    public static World instance;
    private Map<Vector2i, Chunk> renderingChunks;
    private ChunkUpdateManager updater;

    private int worldSeed;
    private Vector2i playerLastPos;

    private boolean onUpdate;

    public World() {
        // Random rng = new Random();
        // worldSeed = rng.nextInt(1000);
        worldSeed = 10;
        onUpdate = true;
        updater = new ChunkUpdateManager(worldSeed);
        instance = this;
    }

    public void init() throws Exception {
        renderingChunks = new HashMap<Vector2i,Chunk>();
        playerLastPos = Player.instance.getPositionInChunkCoord();
        updater.start();
        updater.beginUpdateNewChunk();
    }

    public boolean shouldUpdate(){
        return !playerLastPos.equals(Player.instance.getPositionInChunkCoord());
    }

    public void render(ShaderManager shader){
        checkUpdate();
        synchronizeChunkMap();

        ArrayList<Vector2i> keys = new ArrayList<>(renderingChunks.keySet());
        for (Vector2i pos : keys) {
            Chunk chunk = renderingChunks.get(pos);
            if(!chunk.isReadyToIn() && !chunk.isReadyToOut())
                chunk.render(shader);
        }
        keys = null;
    }

    private void checkUpdate(){
        if(shouldUpdate()){
            playerLastPos = Player.instance.getPositionInChunkCoord();
            updater.beginUpdateNewChunk();
            onUpdate = true;
        }
    }

    private void synchronizeChunkMap(){
        if(onUpdate && updater.checkUpdateDone()){
            onUpdate = false;
            renderingChunks = new HashMap<Vector2i,Chunk>(updater.getNewChunk());

            ArrayList<Vector2i> keys = new ArrayList<>(renderingChunks.keySet());
            for (Vector2i pos : keys) {
                Chunk chunk = renderingChunks.get(pos);
                if(chunk.isReadyToIn() && !chunk.isReadyToOut()){
                    chunk.setReadyToIn(false);
                    chunk.upToGPU();
                }
                else if(chunk.isReadyToOut() && !chunk.isReadyToIn()){
                    chunk.cleanup();
                    renderingChunks.remove(pos);
                }
            }
            keys = null;

        }
    }

    public void cleanup(){
        updater.cleanup();
        File folder = new File(Paths.binaryFolder);
        for (File file : folder.listFiles()) {
            file.delete();
        }
    }

    public Block getBlockAt(int worldX, int worldY, int worldZ){
        int chunkX = (int)Math.floor(worldX / 16.0f);
        int chunkZ = (int)Math.floor(worldZ / 16.0f);
        Chunk currentChunk = renderingChunks.get(new Vector2i(chunkX, chunkZ));
        if(currentChunk != null)
        {
            return currentChunk.getBlock((int)Math.floor(worldX - chunkX * 16), worldY, (int)Math.floor(worldZ - chunkZ * 16));
        }
        return null;
    }

}
