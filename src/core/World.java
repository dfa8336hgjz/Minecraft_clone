package core;

import java.io.File;

import org.joml.Random;

import core.entity.Chunk;
import core.generator.ChunkGenerator;
import core.manager.RenderManager;
import core.utils.Consts;
import core.utils.Paths;

public class World {
    private Chunk[] chunks;
    private int chunkNum = Consts.NUM_OF_CHUNK;
    private int worldSeed;

    public World() {
        Random rng = new Random();
        worldSeed = rng.nextInt(1000);
    }

    public void init() throws Exception {
        chunks = new Chunk[chunkNum];
        SerialGenerate();
    }

    public void render() {
        for (Chunk chunk : chunks) {
            if (chunk != null){
                RenderManager.getShader().bind();
                RenderManager.getShader().set2i("chunkPos", chunk.getChunkX(), chunk.getChunkZ());
                chunk.render();
                RenderManager.getShader().unbind();
            }
        }
    }

    public void cleanup(){
        File folder = new File(Paths.binaryFolder);
        
        for (File file : folder.listFiles()) {
            file.delete();
        }
    }

    public void SerialGenerate(){
        double sideCount = Math.sqrt((double) chunkNum);
        int bound = (int) sideCount/2;
        int i = 0;
        for (int z = -bound; z < bound; z++) {
            for (int x = -bound; x < bound; x++) {
                chunks[i] = ChunkGenerator.generate(x, z, worldSeed);
                chunks[i].serialize();
                i++;
            }
        }
    }

    public void DeserialGenerate(){
        double sideCount = Math.sqrt((double) chunkNum);
        int bound = (int) sideCount/2;
        int i = 0;
        for (int z = -bound; z < bound; z++) {
            for (int x = -bound; x < bound; x++) {
                chunks[i] = new Chunk(x, z);
                chunks[i].deserialize();
                i++;
            }
        }
    }
}
