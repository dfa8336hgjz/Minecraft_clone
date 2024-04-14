package core.entity;

import core.manager.RenderManager;
import core.utils.Consts;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Chunk {
    private Mesh info;

    private int chunkX, chunkZ;
    private ChunkRenderData data;
    public Block[] blocks;

    public Chunk(int chunkX, int chunkZ) {
        blocks = new Block[Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_DEPTH];
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public void generate( ChunkRenderData newData) {
        this.data = newData;
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

    public void serialize(){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/assets/data/binary/chunk"+chunkX+"_"+chunkZ+".bin"));){
            ArrayList<Object> serializeList = new ArrayList<>();
            serializeList.add(blocks);
            serializeList.add(data);

            out.writeObject(serializeList);
			out.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
    }

    public void deserialize(){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/assets/data/binary/chunk"+chunkX+"_"+chunkZ+".bin")))
        {
            ArrayList<Object> deserializeList = (ArrayList<Object>)in.readObject();

            blocks = (Block[]) deserializeList.get(0);
            data = (ChunkRenderData)deserializeList.get(1);
            info = RenderManager.getLoader().loadMesh(data.positions, data.indices, data.uvs, null);

            in.close();
        } catch (IOException | ClassNotFoundException e) { 
            e.printStackTrace(); 
        } 
    }
}