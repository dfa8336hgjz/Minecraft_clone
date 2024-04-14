package core;

import org.joml.Random;

import core.entity.Chunk;
import core.generator.ChunkGenerator;
import core.manager.RenderManager;
import core.utils.Consts;
import core.utils.Paths;

public class World {
    private int texture;
    private Chunk[] chunks;
    private int chunkNum = Consts.NUM_OF_CHUNK;
    private int worldSeed;

    public World() {
        Random rng = new Random();
        worldSeed = rng.nextInt(1000);
    }

    public void generate() {
        double sideCount = Math.sqrt((double) chunkNum);
        int bound = (int) sideCount;
        int i = 0;
        for (int z = 0; z < bound; z++) {
            for (int x = 0; x < bound; x++) {
                // chunks[i] = ChunkGenerator.generate(x, z, worldSeed);
                // chunks[i].serialize();
                chunks[i] = new Chunk(x, z);
                chunks[i].deserialize();
                i++;
            }
        }
    }

    public void init() throws Exception {
        chunks = new Chunk[chunkNum];
        texture = RenderManager.getLoader().loadTexture(Paths.blockTexture);

        generate();
    }

    public void render() {
        for (Chunk chunk : chunks) {
            if (chunk != null)
                chunk.render();
        }
    }

}
