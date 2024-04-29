package core.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Random;
import org.joml.Vector2i;

import core.manager.ShaderManager;
import core.utils.Consts;
import core.utils.Paths;

public class World {
    private Map<Vector2i, Chunk> loadingChunks;
    private int worldSeed;

    private Vector2i playerLastPos;

    public World() {
        Random rng = new Random();
        worldSeed = rng.nextInt(1000);
    }

    public void init() throws Exception {
        loadingChunks = new HashMap<Vector2i,Chunk>();
        playerLastPos = Player.getCamPositionInChunkCoord();
        update();
    }

    public boolean shouldUpdate(){
        return !playerLastPos.equals(Player.getCamPositionInChunkCoord());
    }

    public void update() {
        int upboundX = playerLastPos.x + Consts.CHUNK_RADIUS;
        int lowboundX = playerLastPos.x - Consts.CHUNK_RADIUS;
        int upboundZ = playerLastPos.y + Consts.CHUNK_RADIUS;
        int lowboundZ = playerLastPos.y - Consts.CHUNK_RADIUS;

        for (int i = lowboundX; i < upboundX; i++) {
            for (int j = lowboundZ; j < upboundZ; j++) {
                if(!loadingChunks.containsKey(new Vector2i(i, j)) && inPlayerZone(i, j)){
                    Chunk chunk = new Chunk(i, j);
                    if(!hasBeenSerialized(i, j)){
                        chunk.generateBlockType(worldSeed);
                        chunk.generateNewChunkData();
                        chunk.serialize();
                    }
                    else{
                        chunk.deserialize();
                    }
    
                    chunk.upToGPU();
    
                    Vector2i currentChunkPos = new Vector2i(i, j);
                    loadingChunks.put(currentChunkPos, chunk);
                }   
            }
        }

        unload();
    }

    public void unload(){
        ArrayList<Vector2i> loadedChunkPos = new ArrayList<>(loadingChunks.keySet());
        for (Vector2i pos : loadedChunkPos) {
            if(!inPlayerZone(pos.x, pos.y)){
                loadingChunks.remove(pos);
            }
        }
        
        loadedChunkPos.clear();
        loadedChunkPos = null;
    }

    public void render(ShaderManager shader){
        if(shouldUpdate()){
            playerLastPos = Player.getCamPositionInChunkCoord();
            update();
        }

        for (Chunk chunk : loadingChunks.values()) {
            chunk.render(shader);
        }
    }

    public void cleanup(){
        File folder = new File(Paths.binaryFolder);
        for (File file : folder.listFiles()) {
            file.delete();
        }
    }


    public boolean hasBeenSerialized(int x, int z){
        File file = new File(Paths.binaryFolder+"/chunk"+x+"_"+z+".bin");
        return file.exists();
    }

    public boolean inPlayerZone(int x, int z){
        return (x - playerLastPos.x) * (x - playerLastPos.x) 
            + (z - playerLastPos.y) * (z - playerLastPos.y)
        < Consts.CHUNK_RADIUS * Consts.CHUNK_RADIUS;
    }
}
