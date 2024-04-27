package core.entity;

import core.manager.RenderManager;
import core.utils.Consts;
import core.utils.Paths;

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

    public void load(ChunkRenderData newData) {
        this.data = newData;
        info = RenderManager.getLoader().loadMesh(newData.vertdata);
    }

    public void render() {
        glBindVertexArray(info.getId());
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, data.vertexCount);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void serialize(){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Paths.binaryFolder+"/chunk"+chunkX+"_"+chunkZ+".bin"));){
            for (int vertData : data.vertdata) {
                out.writeInt(vertData);
            }
		} catch (IOException i) {
			i.printStackTrace();
		}
    }

    public void deserialize(){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(Paths.binaryFolder+"/chunk"+chunkX+"_"+chunkZ+".bin"));)
        {
            ArrayList<Integer> dataRead = new ArrayList<>();
            for(int i=0; i < Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 36; i++){
                int newInt = in.readInt();

                if(((newInt & Consts.TEXID_MASK) >> 17) > 0){
                    dataRead.add(newInt);
                }
            }

            ChunkRenderData newRenderData = new ChunkRenderData();
            int[] newData = dataRead.stream().mapToInt(i-> i).toArray();
            newRenderData.vertdata = newData;
            newRenderData.vertexCount = newData.length;
            load(newRenderData);
            info = RenderManager.getLoader().loadMesh(data.vertdata);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    public int getChunkX(){
        return chunkX;
    }

    public int getChunkZ(){
        return chunkZ;
    }
}