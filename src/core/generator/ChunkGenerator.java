package core.generator;

import static org.lwjgl.opengl.GL11.GL_2D;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.json.simple.JSONArray;

import core.entity.BlockData;
import core.entity.ChunkData;
import core.utils.Consts;

public class ChunkGenerator {
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
                    data.blockTypeID[blockId] = 0;
                }
            }
        }
        data.blockTypeID[2] = 1;

        int indexIncrement = 0;
        for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
                    int blockId = x + y * Consts.CHUNK_WIDTH + z * Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT;
                    int positionOffset = blockId * 72; // 3 positions 1 vertex, 4 vertices 1 face, 6 faces 1
                                                       // block
                    int uvOffset = blockId * 48; // 2 uvs 1 vertex, 4 vertices 1 face, 6 faces 1 block
                    int indexOffset = blockId * 36; // 6 indices 1 face, 6 faces 1 block

                    float[][] positions = new float[8][3];

                    positions[0] = new float[] { x + 0.5f, y + 0.5f, z + 0.5f };
                    positions[1] = new float[] { x - 0.5f, y + 0.5f, z + 0.5f };
                    positions[2] = new float[] { x - 0.5f, y + 0.5f, z - 0.5f };
                    positions[3] = new float[] { x + 0.5f, y + 0.5f, z - 0.5f };
                    positions[4] = new float[] { x + 0.5f, y - 0.5f, z + 0.5f };
                    positions[5] = new float[] { x - 0.5f, y - 0.5f, z + 0.5f };
                    positions[6] = new float[] { x - 0.5f, y - 0.5f, z - 0.5f };
                    positions[7] = new float[] { x + 0.5f, y - 0.5f, z - 0.5f };

                    for (int i = 0; i < 3; i++) {
                        // top
                        data.positions[positionOffset + i] = positions[0][i];
                        data.positions[positionOffset + i + 3] = positions[1][i];
                        data.positions[positionOffset + i + 6] = positions[2][i];
                        data.positions[positionOffset + i + 9] = positions[3][i];
                        // bottom
                        data.positions[positionOffset + i + 12] = positions[5][i];
                        data.positions[positionOffset + i + 15] = positions[4][i];
                        data.positions[positionOffset + i + 18] = positions[7][i];
                        data.positions[positionOffset + i + 21] = positions[6][i];
                        // front
                        data.positions[positionOffset + i + 24] = positions[1][i];
                        data.positions[positionOffset + i + 27] = positions[0][i];
                        data.positions[positionOffset + i + 30] = positions[4][i];
                        data.positions[positionOffset + i + 33] = positions[5][i];
                        // back
                        data.positions[positionOffset + i + 36] = positions[3][i];
                        data.positions[positionOffset + i + 39] = positions[2][i];
                        data.positions[positionOffset + i + 42] = positions[6][i];
                        data.positions[positionOffset + i + 45] = positions[7][i];
                        // left
                        data.positions[positionOffset + i + 48] = positions[2][i];
                        data.positions[positionOffset + i + 51] = positions[1][i];
                        data.positions[positionOffset + i + 54] = positions[5][i];
                        data.positions[positionOffset + i + 57] = positions[6][i];
                        // right
                        data.positions[positionOffset + i + 60] = positions[0][i];
                        data.positions[positionOffset + i + 63] = positions[3][i];
                        data.positions[positionOffset + i + 66] = positions[7][i];
                        data.positions[positionOffset + i + 69] = positions[4][i];
                    }

                    for (int i = 0; i < 6; i++) {
                        JSONArray arr = BlockData.getUV(data.blockTypeID[blockId], i);
                        data.uvs[uvOffset + i * 8] = BlockData.getCoordsAt(arr, 0)[0];
                        data.uvs[uvOffset + i * 8 + 1] = BlockData.getCoordsAt(arr, 0)[1];
                        data.uvs[uvOffset + i * 8 + 2] = BlockData.getCoordsAt(arr, 2)[0];
                        data.uvs[uvOffset + i * 8 + 3] = BlockData.getCoordsAt(arr, 2)[1];
                        data.uvs[uvOffset + i * 8 + 4] = BlockData.getCoordsAt(arr, 3)[0];
                        data.uvs[uvOffset + i * 8 + 5] = BlockData.getCoordsAt(arr, 3)[1];
                        data.uvs[uvOffset + i * 8 + 6] = BlockData.getCoordsAt(arr, 1)[0];
                        data.uvs[uvOffset + i * 8 + 7] = BlockData.getCoordsAt(arr, 1)[1];

                    }

                    for (int i = 0; i < 6; i++) {
                        data.indices[indexOffset + (i * 6)] = indexIncrement + 0;
                        data.indices[indexOffset + (i * 6) + 1] = indexIncrement + 1;
                        data.indices[indexOffset + (i * 6) + 2] = indexIncrement + 2;
                        data.indices[indexOffset + (i * 6) + 3] = indexIncrement + 2;
                        data.indices[indexOffset + (i * 6) + 4] = indexIncrement + 3;
                        data.indices[indexOffset + (i * 6) + 5] = indexIncrement + 0;
                        indexIncrement += 4;
                    }
                }
            }
        }

        data.positionSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 24 * 4;
        data.uvSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 12 * 4;
        data.indicesSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 36 * 4;
        // int i = 0;
        // for (float pos : data.positions) {
        // System.out.print(pos + " ");
        // }
        // System.out.println();
        // for (int pos : data.indices) {
        // System.out.print(pos + " ");
        // i++;
        // }
        // System.out.println();
        for (float pos : data.uvs) {
            System.out.print(pos + " ");
        }
        System.out.println();

        return data;
    }
}
