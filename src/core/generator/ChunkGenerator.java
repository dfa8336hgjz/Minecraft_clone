package core.generator;

import core.entity.Block;
import core.entity.BlockData;
import core.entity.ChunkData;
import core.utils.Consts;

public class ChunkGenerator {

    private static int NULLBLOCK = -1;

    public static ChunkData generate() {
        ChunkData data = new ChunkData();
        data.blockTypeID = new int[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH];
        data.positions = new float[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 72];
        data.uvs = new float[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 48];
        data.indices = new int[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 36];

        for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
                    int blockId = x + y * Consts.CHUNK_WIDTH + z * Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT;
                    if (y % 3 == 0)
                        data.blockTypeID[blockId] = 1;
                    else if (y % 3 == 1)
                        data.blockTypeID[blockId] = 2;
                    else
                        data.blockTypeID[blockId] = 0;
                }
            }
        }

        int vertexCursor = 0;
        int indexCursor = 0;
        int indexOffset = 0;
        int uvCursor = 0;

        for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {

                    int thisBlock = getBlock(data.blockTypeID, x, y, z);
                    float[][] positions = new float[8][3];

                    positions[0] = new float[] { x + 0.5f, y + 0.5f, z + 0.5f };
                    positions[1] = new float[] { x - 0.5f, y + 0.5f, z + 0.5f };
                    positions[2] = new float[] { x - 0.5f, y + 0.5f, z - 0.5f };
                    positions[3] = new float[] { x + 0.5f, y + 0.5f, z - 0.5f };
                    positions[4] = new float[] { x + 0.5f, y - 0.5f, z + 0.5f };
                    positions[5] = new float[] { x - 0.5f, y - 0.5f, z + 0.5f };
                    positions[6] = new float[] { x - 0.5f, y - 0.5f, z - 0.5f };
                    positions[7] = new float[] { x + 0.5f, y - 0.5f, z - 0.5f };

                    // for (int i = 0; i < 3; i++) {
                    // // top
                    // data.positions[positionOffset + i] = positions[0][i];
                    // data.positions[positionOffset + i + 3] = positions[1][i];
                    // data.positions[positionOffset + i + 6] = positions[2][i];
                    // data.positions[positionOffset + i + 9] = positions[3][i];
                    // // bottom
                    // data.positions[positionOffset + i + 12] = positions[5][i];
                    // data.positions[positionOffset + i + 15] = positions[4][i];
                    // data.positions[positionOffset + i + 18] = positions[7][i];
                    // data.positions[positionOffset + i + 21] = positions[6][i];
                    // // front
                    // data.positions[positionOffset + i + 24] = positions[1][i];
                    // data.positions[positionOffset + i + 27] = positions[0][i];
                    // data.positions[positionOffset + i + 30] = positions[4][i];
                    // data.positions[positionOffset + i + 33] = positions[5][i];
                    // // back
                    // data.positions[positionOffset + i + 36] = positions[3][i];
                    // data.positions[positionOffset + i + 39] = positions[2][i];
                    // data.positions[positionOffset + i + 42] = positions[6][i];
                    // data.positions[positionOffset + i + 45] = positions[7][i];
                    // // left
                    // data.positions[positionOffset + i + 48] = positions[2][i];
                    // data.positions[positionOffset + i + 51] = positions[1][i];
                    // data.positions[positionOffset + i + 54] = positions[5][i];
                    // data.positions[positionOffset + i + 57] = positions[6][i];
                    // // right
                    // data.positions[positionOffset + i + 60] = positions[0][i];
                    // data.positions[positionOffset + i + 63] = positions[3][i];
                    // data.positions[positionOffset + i + 66] = positions[7][i];
                    // data.positions[positionOffset + i + 69] = positions[4][i];
                    // }

                    // for (int i = 0; i < 6; i++) {
                    // BlockData.setCurrentTexture(data.blockTypeID[blockId], i);
                    // data.uvs[uvOffset + i * 8] = BlockData.getCoordsAt(0)[0];
                    // data.uvs[uvOffset + i * 8 + 1] = BlockData.getCoordsAt(0)[1];
                    // data.uvs[uvOffset + i * 8 + 2] = BlockData.getCoordsAt(2)[0];
                    // data.uvs[uvOffset + i * 8 + 3] = BlockData.getCoordsAt(2)[1];
                    // data.uvs[uvOffset + i * 8 + 4] = BlockData.getCoordsAt(3)[0];
                    // data.uvs[uvOffset + i * 8 + 5] = BlockData.getCoordsAt(3)[1];
                    // data.uvs[uvOffset + i * 8 + 6] = BlockData.getCoordsAt(1)[0];
                    // data.uvs[uvOffset + i * 8 + 7] = BlockData.getCoordsAt(1)[1];

                    // }

                    // for (int i = 0; i < 6; i++) {
                    // data.indices[indexOffset + (i * 6)] = indexIncrement + 0;
                    // data.indices[indexOffset + (i * 6) + 1] = indexIncrement + 1;
                    // data.indices[indexOffset + (i * 6) + 2] = indexIncrement + 2;
                    // data.indices[indexOffset + (i * 6) + 3] = indexIncrement + 2;
                    // data.indices[indexOffset + (i * 6) + 4] = indexIncrement + 3;
                    // data.indices[indexOffset + (i * 6) + 5] = indexIncrement + 0;
                    // indexIncrement += 4;
                    // }

                    int topNeighborBlock = getBlock(data.blockTypeID, x, y + 1, z);
                    if (topNeighborBlock == NULLBLOCK || BlockData.getBlock(topNeighborBlock).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock, 0);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 0, 1, 2, 3);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    int bottomNeighborBlock = getBlock(data.blockTypeID, x, y - 1, z);
                    if (bottomNeighborBlock == NULLBLOCK || BlockData.getBlock(bottomNeighborBlock).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock, 1);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 5, 4, 7, 6);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    int frontNeighborBlock = getBlock(data.blockTypeID, x, y, z + 1);
                    if (frontNeighborBlock == NULLBLOCK || BlockData.getBlock(frontNeighborBlock).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock, 2);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 1, 0, 4, 5);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    int backNeighborBlock = getBlock(data.blockTypeID, x, y, z - 1);
                    if (backNeighborBlock == NULLBLOCK || BlockData.getBlock(backNeighborBlock).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock, 3);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 3, 2, 6, 7);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    int leftNeighborBlock = getBlock(data.blockTypeID, x - 1, y, z);
                    if (leftNeighborBlock == NULLBLOCK || BlockData.getBlock(leftNeighborBlock).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock, 4);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 2, 1, 5, 6);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                    int rightNeighborBlock = getBlock(data.blockTypeID, x + 1, y, z);
                    if (rightNeighborBlock == NULLBLOCK || BlockData.getBlock(rightNeighborBlock).isTransparent()) {
                        BlockData.setCurrentTexture(thisBlock, 5);
                        loadFace(data, vertexCursor, uvCursor, indexCursor, indexOffset, positions, 0, 3, 7, 4);
                        vertexCursor += 12;
                        uvCursor += 8;
                        indexCursor += 6;
                        indexOffset += 4;
                    }

                }
            }
        }

        return data;
    }

    private static int getflattenedID(int x, int y, int z) {
        return x + y * Consts.CHUNK_WIDTH + z * Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT;
    }

    private static int getBlock(int[] blockTypeMap, int x, int y, int z) {
        int id = getflattenedID(x, y, z);
        return (x >= Consts.CHUNK_WIDTH || x < 0 || z >= Consts.CHUNK_DEPTH || z < 0 || y >= Consts.CHUNK_HEIGHT
                || y < 0) ? NULLBLOCK
                        : blockTypeMap[id];
    }

    private static void loadFace(ChunkData data, int vertexCursor, int uvCursor, int indexCursor, int indexOffset,
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
}