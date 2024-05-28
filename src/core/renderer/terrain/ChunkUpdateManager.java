package core.renderer.terrain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joml.Vector2i;

import core.Launcher;
import core.gameplay.Player;
import core.utils.Consts;
import core.utils.Utils;

public class ChunkUpdateManager extends Thread{
    private Map<Vector2i, Chunk> readyToLoadChunks;
    private ExecutorService executor;

    private boolean update;
    private boolean running;

    public ChunkUpdateManager(){
        update = false;
        running = true;
        readyToLoadChunks = new HashMap<Vector2i,Chunk>();
    }

    public void run(){
        while(!Thread.interrupted()){
            if(update && running){
                executor = Executors.newFixedThreadPool(5);
                readyToLoadChunks.clear();
                Vector2i playerPos = Player.instance.getPositionInChunkCoord();
                int upboundX = playerPos.x + Consts.CHUNK_RADIUS;
                int lowboundX = playerPos.x - Consts.CHUNK_RADIUS;
                int upboundZ = playerPos.y + Consts.CHUNK_RADIUS;
                int lowboundZ = playerPos.y - Consts.CHUNK_RADIUS;

                for (int i = lowboundX; i <= upboundX; i++) {
                    for (int j = lowboundZ; j <= upboundZ; j++) {
                        if(Utils.inRadius(i, j, playerPos, Consts.CHUNK_RADIUS + 1)){
                            Chunk chunk = new Chunk(i, j);
                            ChunkUpdateThread cThread = new ChunkUpdateThread(chunk, Launcher.instance.worldSeed, playerPos);
                            executor.execute(cThread);
                            readyToLoadChunks.put(new Vector2i(i, j), chunk);
                        }
                    }
                }

                executor.shutdown();
                try {
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

                update = false;
            }

            if(!running) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Map<Vector2i, Chunk> getNewChunk(){
        return readyToLoadChunks;
    }

    public void beginUpdateNewChunk(){
        update = true;
    }

    public boolean checkUpdateDone(){
        return !update;
    }
    
    public void cleanup(){
        running = false;
    }

}


class ChunkUpdateThread implements Runnable{
    private Chunk thisChunk;
    private int worldSeed;
    private Vector2i playerPos;
    public ChunkUpdateThread(Chunk chunk, int worldSeed, Vector2i pos){
        thisChunk = chunk;
        this.worldSeed = worldSeed;
        this.playerPos = pos;
    }

    @Override
    public void run() {
        if(Utils.inRadius(thisChunk.getChunkX(), thisChunk.getChunkZ(), playerPos, Consts.CHUNK_RADIUS)){
            thisChunk.setReadyToIn(true);
        }
        else{
            thisChunk.setReadyToOut(true);
        }

        if(!Utils.hasBeenSerialized(thisChunk.getChunkX(), thisChunk.getChunkZ())){
            thisChunk.generateBlockType(this.worldSeed);
            thisChunk.serialize();
            thisChunk.generateNewChunkData();
        }
        else{
            thisChunk.deserialize();
            thisChunk.generateNewChunkData();
        }
    }

}