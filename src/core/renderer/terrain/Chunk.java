package core.renderer.terrain;

import core.components.Block;
import core.components.ChunkRenderData;
import core.components.Mesh;
import core.renderer.supporters.ShaderManager;
import core.renderer.supporters.texturePackage.BlockData;
import core.renderer.supporters.texturePackage.TextureMapLoader;
import core.utils.Consts;
import core.utils.Paths;
import core.utils.SimplexNoise;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.joml.Vector3i;



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
    private Mesh mesh;
    private int chunkX, chunkZ;
    private ChunkRenderData data;
    private boolean readyToOut;
    private boolean readyToIn;
    private boolean isChanged;
    private ArrayList<BlockData> blockMap;

    public Block[] blocks;

    public Chunk(int chunkX, int chunkZ) {
        blocks = new Block[Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_DEPTH];
        blockMap = TextureMapLoader.getBlockDataMap();
        data = new ChunkRenderData();
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        readyToOut = false;
        readyToIn = false;
        isChanged = false;
    }

    public void prepareMesh(){
        if(mesh == null){
            int vao = glGenVertexArrays();
            glBindVertexArray(vao);
            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, 30000 * Integer.BYTES, GL_DYNAMIC_DRAW);
            glVertexAttribIPointer(0, 1, GL_UNSIGNED_INT, Integer.BYTES, 0);
            glEnableVertexAttribArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            mesh = new Mesh(vao, vbo, data.vertexCount, 0);
        }
    }

    public void uploadToGPU(){
        glBindBuffer(GL_ARRAY_BUFFER, mesh.vbo);
        glBufferData(GL_ARRAY_BUFFER, data.vertdata, GL_DYNAMIC_DRAW);
    }


    public void render(ShaderManager shader) {
        if(isChanged){
            isChanged = !isChanged;
            serialize();
        }

        shader.set2i("chunkPos", chunkX, chunkZ);
        glBindVertexArray(mesh.vao);
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, data.vertexCount);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void generateBlockType(int seed) {
        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
                float MountainHeight = convertRange(
                        (float) SimplexNoise.noise(((double) x + chunkX * Consts.CHUNK_WIDTH + seed) / Consts.increment,
                                ((double) z + chunkZ * Consts.CHUNK_DEPTH + seed) / Consts.increment))
                        * 80 + 20;
                float GroundHeight = convertRange(
                        (float) SimplexNoise.noise(((double) x + chunkX * Consts.CHUNK_WIDTH + seed) / Consts.increment,
                                ((double) z + chunkZ * Consts.CHUNK_DEPTH + seed) / Consts.increment))
                        * 10 + 70;

                for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                    int blockId = getflattenedID(x, y, z);
                    this.blocks[blockId] = new Block();

                    if(y == 0){
                        this.blocks[blockId].id = 9;
                    }
                    else if (y < GroundHeight) {
                        this.blocks[blockId].id = 1;
                    }
                    else if (y == GroundHeight){
                        this.blocks[blockId].id = 12;
                    }
                    else if(y < MountainHeight){
                        this.blocks[blockId].id = 4;
                    }
                }
            }
        }
    }
    
    public void generateNewChunkData() {
        int vertexCursor = 0;
        ArrayList<Integer> newVertData = new ArrayList<>();
        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
                    Block thisBlock = getBlock(x, y, z);
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

                    Block topNeighborBlock = getBlock(x, y + 1, z);
                    if (topNeighborBlock.isNullBlock()
                            || blockMap.get(topNeighborBlock.id).Transparent()) {
                        loadBlock(vert0, vert1, vert2, vert3, thisBlock, FACE.TOP, newVertData);
                        vertexCursor += 6;
                    }

                    Block bottomNeighborBlock = getBlock(x, y - 1, z);
                    if (bottomNeighborBlock.isNullBlock()
                            || blockMap.get(bottomNeighborBlock.id).Transparent()) {
                        loadBlock(vert5, vert4, vert7, vert6, thisBlock, FACE.BOTTOM, newVertData);
                        vertexCursor += 6;
                    }

                    Block frontNeighborBlock = getBlock(x, y, z + 1);
                    if (frontNeighborBlock.isNullBlock()
                            || blockMap.get(frontNeighborBlock.id).Transparent()) {
                        loadBlock(vert1, vert0, vert4, vert5, thisBlock, FACE.FRONT, newVertData);
                        vertexCursor += 6;
                    }

                    Block backNeighborBlock = getBlock(x, y, z - 1);
                    if (backNeighborBlock.isNullBlock()
                            || blockMap.get(backNeighborBlock.id).Transparent()) {
                        loadBlock(vert3, vert2, vert6, vert7, thisBlock, FACE.BACK, newVertData);
                        vertexCursor += 6;
                    }

                    Block leftNeighborBlock = getBlock(x - 1, y, z);
                    if (leftNeighborBlock.isNullBlock()
                            || blockMap.get(leftNeighborBlock.id).Transparent()) {
                        loadBlock(vert2, vert1, vert5, vert6, thisBlock, FACE.LEFT, newVertData);
                        vertexCursor += 6;
                    }

                    Block rightNeighborBlock = getBlock(x + 1, y, z);
                    if (rightNeighborBlock.isNullBlock()
                            || blockMap.get(rightNeighborBlock.id).Transparent()) {
                        loadBlock(vert0, vert3, vert7, vert4, thisBlock, FACE.RIGHT, newVertData);
                        vertexCursor += 6;
                    }
                }
            }
        }

        data.vertdata = newVertData.stream().mapToInt(i-> i).toArray();
        data.vertexCount = vertexCursor;

    }

    private int getflattenedID(int x, int y, int z) {
        return x + y * Consts.CHUNK_WIDTH + z * Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT;
    }

    private int getEncodedID(int x, int y, int z) {
        return x + y * (Consts.CHUNK_WIDTH+1) + z * (Consts.CHUNK_WIDTH+1) * (Consts.CHUNK_HEIGHT+1);
    }

    public Block getBlock(int x, int y, int z) {
        int id = getflattenedID(x, y, z);
        Block NULLBLOCK = new Block(0, blockMap);
        return (x >= Consts.CHUNK_WIDTH || x < 0 || z >= Consts.CHUNK_DEPTH || z < 0 || y >= Consts.CHUNK_HEIGHT || y < 0) 
        ? NULLBLOCK : blocks[id];
    }

    public void removeBlock(int x, int y, int z) {
        int id = getflattenedID(x, y, z);
        if(blocks[id].id == 9) return; // bedrock
        Block NULLBLOCK = new Block(0, blockMap);
        blocks[id] = NULLBLOCK;
        isChanged = true;

        generateNewChunkData();
        uploadToGPU();
    }

    public void addBlock(int x, int y, int z, int typeId) {
        int id = getflattenedID(x, y, z);
        if(blocks[id].isNullBlock()) {
            blocks[id] = new Block(typeId, blockMap);
            isChanged = true;
    
            generateNewChunkData();
            uploadToGPU();
        }
    }

    private void loadBlock(Vector3i vert0, Vector3i vert1, Vector3i vert2, Vector3i vert3, 
        Block block, FACE face, ArrayList<Integer> newVertData) {
            newVertData.add(loadFace(vert0, face, block, UV_INDEX._00));
            newVertData.add(loadFace(vert1, face, block, UV_INDEX._10));
            newVertData.add(loadFace(vert2, face, block, UV_INDEX._11));
            newVertData.add(loadFace(vert2, face, block, UV_INDEX._11));
            newVertData.add(loadFace(vert3, face, block, UV_INDEX._01));
            newVertData.add(loadFace(vert0, face, block, UV_INDEX._00));
    }

    private int loadFace(Vector3i pos, FACE face, Block block, UV_INDEX uvId){
        int newData = 0;
        int posId = getEncodedID(pos.x, pos.y, pos.z);
        int texId = TextureMapLoader.getFaceTextureId(block.id, face.ordinal());
        newData |= ((posId << 0) & Consts.POSITION_MASK);
        newData |= ((texId << 17) & Consts.TEXID_MASK);
        newData |= ((face.ordinal() << 27) & Consts.NORMAL_MASK);
        newData |= ((uvId.ordinal() << 30) & Consts.UV_MASK);
        
        return newData;
    }
 
    public void serialize(){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Paths.binaryFolder+"/chunk"+chunkX+"_"+chunkZ+".bin"));){
            for (Block block : blocks) {
                out.writeByte(block.wrapData());
            }

		} catch (IOException e) {
            File directory = new File(Paths.binaryFolder);
            directory.mkdirs();
			serialize();
		}
    }

    public void deserialize(){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(Paths.binaryFolder+"/chunk"+chunkX+"_"+chunkZ+".bin"));)
        {
            for (int j = 0; j < Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH; j++) {
                blocks[j] = new Block();
                blocks[j].unwrapData(in.readByte());
            }
            
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private static float convertRange(float val) {
        return (val + 1) / 2;
    }


    public int getChunkX(){
        return chunkX;
    }

    public int getChunkZ(){
        return chunkZ;
    }

    public void setReadyToIn(boolean ready){
        readyToIn = ready;
    }

    public void setReadyToOut(boolean ready){
        readyToOut = ready;
    }

    public boolean isReadyToIn(){
        return readyToIn;
    }

    public boolean isReadyToOut(){
        return readyToOut;
    }

    public void cleanup(){
        if(mesh != null) mesh.cleanup();
        data = null;
        blocks = null;
    }

    public Mesh getmesh(){
        return mesh;
    }
}