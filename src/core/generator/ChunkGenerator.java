package core.generator;

import core.entity.Block;
import core.entity.BlockData;
import core.entity.Chunk;
import core.entity.ChunkRenderData;
import core.utils.Consts;

import core.utils.SimplexNoise;

public class ChunkGenerator {
    private static Block NULLBLOCK = new Block(-1, 0, 0, 0);

    public static Chunk generate(int chunkX, int chunkZ, int seed) {
        Chunk chunk = new Chunk();

        ChunkRenderData data = new ChunkRenderData();
        data.positions = new float[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 72];
        data.uvs = new float[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 48];
        data.indices = new int[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 36];
        int pmc = 0;
        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
                    int blockId = getflattenedID(x, y, z);
                    chunk.blocks[blockId] = new Block();
                    float height = convertRange(
                            (float) SimplexNoise.noise(((double) x + chunkX * Consts.CHUNK_WIDTH) / 400.0f,
                                    ((double) z + chunkZ * Consts.CHUNK_DEPTH) / 400.0f))
                            * Consts.CHUNK_HEIGHT;

                    if (height > y) {
                        chunk.blocks[blockId].setId(0);
                    }
                }
            }
        }

        int vertexCursor = 0;
        int indexCursor = 0;
        int indexOffset = 0;
        int uvCursor = 0;
        int posX = chunkX * Consts.CHUNK_WIDTH;
        int posZ = chunkZ * Consts.CHUNK_DEPTH;

        for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {

                    Block thisBlock = getBlock(chunk.blocks, x, y, z);
                    if (thisBlock.isNullBlock()) {
                        continue;
                    }
                    float[][] positions = new float[8][3];

                    positions[0] = new float[] { x + posX + 0.5f, y + 0.5f, z + posZ + 0.5f };
                    positions[1] = new float[] { x + posX - 0.5f, y + 0.5f, z + posZ + 0.5f };
                    positions[2] = new float[] { x + posX - 0.5f, y + 0.5f, z + posZ - 0.5f };
                    positions[3] = new float[] { x + posX + 0.5f, y + 0.5f, z + posZ - 0.5f };
                    positions[4] = new float[] { x + posX + 0.5f, y - 0.5f, z + posZ + 0.5f };
                    positions[5] = new float[] { x + posX - 0.5f, y - 0.5f, z + posZ + 0.5f };
                    positions[6] = new float[] { x + posX - 0.5f, y - 0.5f, z + posZ - 0.5f };
                    positions[7] = new float[] { x + posX + 0.5f, y - 0.5f, z + posZ - 0.5f };

                    Block topNeighborBlock = getBlock(chunk.blocks, x, y + 1, z);
                    if (topNeighborBlock.isNullBlock()
                            || BlockData.getBlock(topNeighborBlock.getId()).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock.getId(), 0);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 0, 1, 2, 3);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    Block bottomNeighborBlock = getBlock(chunk.blocks, x, y - 1, z);
                    if (bottomNeighborBlock.isNullBlock()
                            || BlockData.getBlock(bottomNeighborBlock.getId()).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock.getId(), 1);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 5, 4, 7, 6);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    Block frontNeighborBlock = getBlock(chunk.blocks, x, y, z + 1);
                    if (frontNeighborBlock.isNullBlock()
                            || BlockData.getBlock(frontNeighborBlock.getId()).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock.getId(), 2);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 1, 0, 4, 5);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    Block backNeighborBlock = getBlock(chunk.blocks, x, y, z - 1);
                    if (backNeighborBlock.isNullBlock()
                            || BlockData.getBlock(backNeighborBlock.getId()).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock.getId(), 3);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 3, 2, 6, 7);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    Block leftNeighborBlock = getBlock(chunk.blocks, x - 1, y, z);
                    if (leftNeighborBlock.isNullBlock()
                            || BlockData.getBlock(leftNeighborBlock.getId()).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock.getId(), 4);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 2, 1, 5, 6);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    Block rightNeighborBlock = getBlock(chunk.blocks, x + 1, y, z);
                    if (rightNeighborBlock.isNullBlock()
                            || BlockData.getBlock(rightNeighborBlock.getId()).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock.getId(), 5);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 0, 3, 7, 4);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }
                }
            }
        }

        chunk.generate(chunkX, chunkZ, data);
        return chunk;
    }

    private static int getflattenedID(int x, int y, int z) {
        return x + y * Consts.CHUNK_WIDTH + z * Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT;
    }

    private static Block getBlock(Block[] blockTypeMap, int x, int y, int z) {
        int id = getflattenedID(x, y, z);
        return (x >= Consts.CHUNK_WIDTH || x < 0 || z >= Consts.CHUNK_DEPTH || z < 0 || y >= Consts.CHUNK_HEIGHT
                || y < 0) ? NULLBLOCK
                        : blockTypeMap[id];
    }

    private static void loadFace(ChunkRenderData data, int vertexCursor, int uvCursor, int indexCursor, int indexOffset,
            float[][] positions, int v0, int v1, int v2, int v3) {
        for (int i = 0; i < 3; i++) {
            data.positions[vertexCursor + i] = positions[v0][i];
            data.positions[vertexCursor + i + 3] = positions[v1][i];
            data.positions[vertexCursor + i + 6] = positions[v2][i];
            data.positions[vertexCursor + i + 9] = positions[v3][i];
        }

        for (int i = 0; i < 2; i++) {
            data.uvs[uvCursor + i] = BlockData.getCoordsAt(0)[i];
            data.uvs[uvCursor + i + 2] = BlockData.getCoordsAt(2)[i];
            data.uvs[uvCursor + i + 4] = BlockData.getCoordsAt(3)[i];
            data.uvs[uvCursor + i + 6] = BlockData.getCoordsAt(1)[i];
        }

        data.indices[indexCursor] = indexOffset;
        data.indices[indexCursor + 1] = indexOffset + 1;
        data.indices[indexCursor + 2] = indexOffset + 2;
        data.indices[indexCursor + 3] = indexOffset + 2;
        data.indices[indexCursor + 4] = indexOffset + 3;
        data.indices[indexCursor + 5] = indexOffset;
    }

    private static float convertRange(float val) {
        return (val + 1) / 2;
    }

}