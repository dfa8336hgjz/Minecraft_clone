package core.entity;

import core.component.Block;
import core.component.ChunkRenderData;
import core.component.ChunkMesh;
import core.manager.RenderManager;
import core.manager.ShaderManager;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Consts;
import core.utils.Paths;
import core.utils.SimplexNoise;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.joml.Vector3i;

import static org.lwjgl.opengl.GL11.*;


enum FACE{
    TOP,
    BOTTOM,
    FRONT,
    BACK,
    LEFT,
    RIGHT
};

enum UV_INDEX{
    _00,
    _01,
    _10,
    _11
}


public class Chunk {
    private ChunkMesh mesh;
    private int chunkX, chunkZ;
    private ChunkRenderData data;
    private boolean readyToOut;

    public Block[] blocks;

    public Chunk(int chunkX, int chunkZ) {
        blocks = new Block[Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_DEPTH];
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        readyToOut = false;
    }

    public void upToGPU(){
        if(mesh == null)
            mesh = RenderManager.getLoader().loadMesh(data.vertdata);
    }

    public void render(ShaderManager shader) {
        shader.setMat4f("model", mesh.getModelMatrix());
        shader.set2i("chunkPos", chunkX, chunkZ);
        glBindVertexArray(mesh.getVAO());
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
            while(true){
                try {
                    int newInt = in.readInt();
    
                    if(((newInt & Consts.TEXID_MASK) >> 17) > 0){
                        dataRead.add(newInt);
                    }
                } catch (Exception e) {
                    break;
                }
            }

            ChunkRenderData newRenderData = new ChunkRenderData();
            int[] newData = dataRead.stream().mapToInt(i-> i).toArray();
            newRenderData.vertdata = newData;
            newRenderData.vertexCount = newData.length;
            
            data = newRenderData;
            RenderManager.getLoader().loadMesh(data.vertdata);
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


    public void generateBlockType(int seed) {
        data = new ChunkRenderData();
        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
                    int blockId = getflattenedID(x, y, z);
                    this.blocks[blockId] = new Block();
                    float MountainHeight = convertRange(
                            (float) SimplexNoise.noise(((double) x + chunkX * Consts.CHUNK_WIDTH + seed) / Consts.increment,
                                    ((double) z + chunkZ * Consts.CHUNK_DEPTH + seed) / Consts.increment))
                            * 80 + 20;
                    float GroundHeight = convertRange(
                            (float) SimplexNoise.noise(((double) x + chunkX * Consts.CHUNK_WIDTH + seed) / Consts.increment,
                                    ((double) z + chunkZ * Consts.CHUNK_DEPTH + seed) / Consts.increment))
                            * 10 + 70;

                    if (y < MountainHeight) {
                        this.blocks[blockId].setId(3);
                    }
                    if(y < GroundHeight){
                        this.blocks[blockId].setId(8);
                    }
                }
            }
        }


        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
                    Block thisBlock = getBlock(this.blocks, x, y, z);
                    if (thisBlock.isNullBlock()) {
                        continue;
                    }

                    Block topNeighborBlock = getBlock(this.blocks, x, y + 1, z);
                    if (topNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(topNeighborBlock.getId()).isTransparent()) {
                        data.vertexCount += 6;
                    }

                    Block bottomNeighborBlock = getBlock(this.blocks, x, y - 1, z);
                    if (bottomNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(bottomNeighborBlock.getId()).isTransparent()) {
                        data.vertexCount += 6;
                    }

                    Block frontNeighborBlock = getBlock(this.blocks, x, y, z + 1);
                    if (frontNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(frontNeighborBlock.getId()).isTransparent()) {
                        data.vertexCount += 6;
                    }

                    Block backNeighborBlock = getBlock(this.blocks, x, y, z - 1);
                    if (backNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(backNeighborBlock.getId()).isTransparent()) {
                        data.vertexCount += 6;
                    }

                    Block leftNeighborBlock = getBlock(this.blocks, x - 1, y, z);
                    if (leftNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(leftNeighborBlock.getId()).isTransparent()) {
                        data.vertexCount += 6;
                    }

                    Block rightNeighborBlock = getBlock(this.blocks, x + 1, y, z);
                    if (rightNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(rightNeighborBlock.getId()).isTransparent()) {
                        data.vertexCount += 6;
                    }
                }
            }
        }

        data.vertdata = new int[data.vertexCount];

    }
    
    public void generateNewChunkData() {
        int vertexCursor = 0;

        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
                    Block thisBlock = getBlock(this.blocks, x, y, z);
                    if (thisBlock.isNullBlock()) {
                        continue;
                    }

                    Vector3i vert0 = new Vector3i(x + 1, y + 1, z + 1);
                    Vector3i vert1 = new Vector3i(x, y + 1, z + 1);
                    Vector3i vert2 = new Vector3i(x , y + 1, z);
                    Vector3i vert3 = new Vector3i(x + 1, y + 1, z);
                    Vector3i vert4 = new Vector3i(x + 1, y, z + 1);
                    Vector3i vert5 = new Vector3i(x, y, z + 1);
                    Vector3i vert6 = new Vector3i(x, y, z);
                    Vector3i vert7 = new Vector3i(x + 1, y, z);

                    Block topNeighborBlock = getBlock(this.blocks, x, y + 1, z);
                    if (topNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(topNeighborBlock.getId()).isTransparent()) {
                        loadBlock(vert0, vert1, vert2, vert3, thisBlock, FACE.TOP, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block bottomNeighborBlock = getBlock(this.blocks, x, y - 1, z);
                    if (bottomNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(bottomNeighborBlock.getId()).isTransparent()) {
                        loadBlock(vert5, vert4, vert7, vert6, thisBlock, FACE.BOTTOM, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block frontNeighborBlock = getBlock(this.blocks, x, y, z + 1);
                    if (frontNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(frontNeighborBlock.getId()).isTransparent()) {
                        loadBlock(vert1, vert0, vert4, vert5, thisBlock, FACE.FRONT, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block backNeighborBlock = getBlock(this.blocks, x, y, z - 1);
                    if (backNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(backNeighborBlock.getId()).isTransparent()) {
                        loadBlock(vert3, vert2, vert6, vert7, thisBlock, FACE.BACK, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block leftNeighborBlock = getBlock(this.blocks, x - 1, y, z);
                    if (leftNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(leftNeighborBlock.getId()).isTransparent()) {
                        loadBlock(vert2, vert1, vert5, vert6, thisBlock, FACE.LEFT, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block rightNeighborBlock = getBlock(this.blocks, x + 1, y, z);
                    if (rightNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(rightNeighborBlock.getId()).isTransparent()) {
                        loadBlock(vert0, vert3, vert7, vert4, thisBlock, FACE.RIGHT, vertexCursor);
                        vertexCursor += 6;
                    }
                }
            }
        }

    }

    private int getflattenedID(int x, int y, int z) {
        return x + y * Consts.CHUNK_WIDTH + z * Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT;
    }

    private int getEncodedID(int x, int y, int z) {
        return x + y * (Consts.CHUNK_WIDTH+1) + z * (Consts.CHUNK_WIDTH+1) * (Consts.CHUNK_HEIGHT+1);
    }

    private Block getBlock(Block[] blockTypeMap, int x, int y, int z) {
        int id = getflattenedID(x, y, z);
        Block NULLBLOCK = new Block(-1, 0, 0, 0);
        return (x >= Consts.CHUNK_WIDTH || x < 0 || z >= Consts.CHUNK_DEPTH || z < 0 || y >= Consts.CHUNK_HEIGHT
                || y < 0) ? NULLBLOCK
                        : blockTypeMap[id];
    }

    private void loadBlock(Vector3i vert0, Vector3i vert1, Vector3i vert2, Vector3i vert3, 
        Block block, FACE face, int vertexCursor) {
            data.vertdata[vertexCursor] = loadFace(vert0, face, block, UV_INDEX._00);
            data.vertdata[vertexCursor + 1] = loadFace(vert1, face, block, UV_INDEX._10);
            data.vertdata[vertexCursor + 2] = loadFace(vert2, face, block, UV_INDEX._11);
            data.vertdata[vertexCursor + 3] = loadFace(vert2, face, block, UV_INDEX._11);
            data.vertdata[vertexCursor + 4] = loadFace(vert3, face, block, UV_INDEX._01);
            data.vertdata[vertexCursor + 5] = loadFace(vert0, face, block, UV_INDEX._00);
    }

    private int loadFace(Vector3i pos, FACE face, Block block, UV_INDEX uvId){
        int newData = 0;
        int posId = getEncodedID(pos.x, pos.y, pos.z);
        int texId = TextureMapLoader.getFaceTextureId(block.getId(), face.ordinal());
        newData |= ((posId << 0) & Consts.POSITION_MASK);
        newData |= ((texId << 17) & Consts.TEXID_MASK);
        newData |= ((face.ordinal() << 27) & Consts.NORMAL_MASK);
        newData |= ((uvId.ordinal() << 30) & Consts.UV_MASK);
        
        return newData;
    }

    private static float convertRange(float val) {
        return (val + 1) / 2;
    }

    public void unload(){
        readyToOut = true;
    }

    public boolean isReadyToOut(){
        return readyToOut;
    }

    public void cleanup(){
        mesh.cleanup();
        data = null;
        blocks = null;
    }
}