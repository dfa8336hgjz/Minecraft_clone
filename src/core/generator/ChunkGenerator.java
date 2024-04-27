package core.generator;

import org.joml.Vector3i;

import core.entity.Block;
import core.entity.Chunk;
import core.entity.ChunkRenderData;
import core.utils.Consts;

import core.utils.SimplexNoise;

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

public class ChunkGenerator {
    private static Block NULLBLOCK = new Block(-1, 0, 0, 0);

    public static Chunk generate(int chunkX, int chunkZ, int seed) {
        Chunk chunk = new Chunk(chunkX, chunkZ);
        ChunkRenderData data = new ChunkRenderData();
        data.vertdata = new int[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 36];
        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
                    int blockId = getflattenedID(x, y, z);
                    chunk.blocks[blockId] = new Block();
                    float height = convertRange(
                            (float) SimplexNoise.noise(((double) x + chunkX * Consts.CHUNK_WIDTH + seed) / Consts.increment,
                                    ((double) z + chunkZ * Consts.CHUNK_DEPTH + seed) / Consts.increment))
                            * Consts.CHUNK_HEIGHT;

                    if (height - 4 > y) {
                        chunk.blocks[blockId].setId(0);
                    }
                    else if(y < height)
                        chunk.blocks[blockId].setId(3);

                }
            }
        }

        int vertexCursor = 0;

        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
                    Block thisBlock = getBlock(chunk.blocks, x, y, z);
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

                    Block topNeighborBlock = getBlock(chunk.blocks, x, y + 1, z);
                    if (topNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(topNeighborBlock.getId()).isTransparent()) {
                        loadBlock(data, vert0, vert1, vert2, vert3, thisBlock, FACE.TOP, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block bottomNeighborBlock = getBlock(chunk.blocks, x, y - 1, z);
                    if (bottomNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(bottomNeighborBlock.getId()).isTransparent()) {
                        loadBlock(data, vert5, vert4, vert7, vert6, thisBlock, FACE.BOTTOM, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block frontNeighborBlock = getBlock(chunk.blocks, x, y, z + 1);
                    if (frontNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(frontNeighborBlock.getId()).isTransparent()) {
                        loadBlock(data, vert1, vert0, vert4, vert5, thisBlock, FACE.FRONT, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block backNeighborBlock = getBlock(chunk.blocks, x, y, z - 1);
                    if (backNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(backNeighborBlock.getId()).isTransparent()) {
                        loadBlock(data, vert3, vert2, vert6, vert7, thisBlock, FACE.BACK, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block leftNeighborBlock = getBlock(chunk.blocks, x - 1, y, z);
                    if (leftNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(leftNeighborBlock.getId()).isTransparent()) {
                        loadBlock(data, vert2, vert1, vert5, vert6, thisBlock, FACE.LEFT, vertexCursor);
                        vertexCursor += 6;
                    }

                    Block rightNeighborBlock = getBlock(chunk.blocks, x + 1, y, z);
                    if (rightNeighborBlock.isNullBlock()
                            || TextureMapLoader.getBlock(rightNeighborBlock.getId()).isTransparent()) {
                        loadBlock(data, vert0, vert3, vert7, vert4, thisBlock, FACE.RIGHT, vertexCursor);
                        vertexCursor += 6;
                    }
                }
            }
        }

        data.vertexCount = vertexCursor;
        chunk.load(data);
        return chunk;
    }

    private static int getflattenedID(int x, int y, int z) {
        return x + y * Consts.CHUNK_WIDTH + z * Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT;
    }

    private static int getEncodedID(int x, int y, int z) {
        return x + y * (Consts.CHUNK_WIDTH+1) + z * (Consts.CHUNK_WIDTH+1) * (Consts.CHUNK_HEIGHT+1);
    }

    private static Block getBlock(Block[] blockTypeMap, int x, int y, int z) {
        int id = getflattenedID(x, y, z);
        return (x >= Consts.CHUNK_WIDTH || x < 0 || z >= Consts.CHUNK_DEPTH || z < 0 || y >= Consts.CHUNK_HEIGHT
                || y < 0) ? NULLBLOCK
                        : blockTypeMap[id];
    }

    private static void loadBlock(ChunkRenderData data, Vector3i vert0, Vector3i vert1, Vector3i vert2, Vector3i vert3, 
        Block block, FACE face, int vertexCursor) {
            data.vertdata[vertexCursor] = loadFace(vert0, face, block, UV_INDEX._00);
            data.vertdata[vertexCursor + 1] = loadFace(vert1, face, block, UV_INDEX._10);
            data.vertdata[vertexCursor + 2] = loadFace(vert2, face, block, UV_INDEX._11);
            data.vertdata[vertexCursor + 3] = loadFace(vert2, face, block, UV_INDEX._11);
            data.vertdata[vertexCursor + 4] = loadFace(vert3, face, block, UV_INDEX._01);
            data.vertdata[vertexCursor + 5] = loadFace(vert0, face, block, UV_INDEX._00);
    }

    private static int loadFace(Vector3i pos, FACE face, Block block, UV_INDEX uvId){
        int newData = 0;
        int posId = getEncodedID(pos.x, pos.y, pos.z);
        int texId = TextureMapLoader.getFaceTextureId(block.getId(), face);
        newData |= ((posId << 0) & Consts.POSITION_MASK);
        newData |= ((texId << 17) & Consts.TEXID_MASK);
        newData |= ((face.ordinal() << 27) & Consts.NORMAL_MASK);
        newData |= ((uvId.ordinal() << 30) & Consts.UV_MASK);
        
        return newData;
    }

    private static float convertRange(float val) {
        return (val + 1) / 2;
    }
}