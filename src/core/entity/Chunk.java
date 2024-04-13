package core.entity;

import core.generator.ChunkGenerator;
import core.manager.RenderManager;
import core.utils.Consts;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL11.*;

public class Chunk {
    private Mesh info;

    private int chunkX, chunkZ;
    private ChunkRenderData data;
    public Block[] blocks;

    public Chunk() {
        blocks = new Block[Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_DEPTH];
    }

    public void generate(int chunkX, int chunkZ, ChunkRenderData newData) {
        this.data = newData;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        info = RenderManager.getLoader().loadMesh(newData.positions, newData.indices, newData.uvs, null);
    }

    public void render() {
        glBindVertexArray(info.getId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, info.getIndexCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}