package core.generator;

import java.util.Vector;

import org.joml.Vector2f;
import org.joml.Vector3f;

import core.entity.ChunkData;
import core.utils.Consts;

public class ChunkGenerator {

    public static ChunkData generate() {
        ChunkData data = new ChunkData();
        data.positions = new float[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 24];
        data.uvs = new float[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 16];
        data.indices = new int[Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 36];

        for (int x = 0; x < Consts.CHUNK_WIDTH; x++) {
            for (int y = 0; y < Consts.CHUNK_HEIGHT; y++) {
                for (int z = 0; z < Consts.CHUNK_DEPTH; z++) {
                    int blockId = x + y * Consts.CHUNK_WIDTH + z * Consts.CHUNK_WIDTH * Consts.CHUNK_HEIGHT;
                    int positionOffset = blockId * 3 * 8; // 3 positions 1 vertex, 8 vertices 1 block
                    int uvOffset = blockId * 2 * 8; // 3 positions 1 vertex, 8 vertices 1 block
                    int indexOffset = blockId * 36; // 6 indices 1 face, 6 faces 1 block

                    Vector3f[] positions = new Vector3f[8];
                    Vector2f[] uvs = new Vector2f[8];

                    positions[0] = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
                    positions[1] = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
                    positions[2] = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
                    positions[3] = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
                    positions[4] = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
                    positions[5] = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
                    positions[6] = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
                    positions[7] = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);

                    uvs[0] = new Vector2f(0.0f, 0.0f);
                    uvs[1] = new Vector2f(0.0f, 1.0f);
                    uvs[2] = new Vector2f(0.0f, 0.0f);
                    uvs[3] = new Vector2f(0.0f, 1.0f);
                    uvs[4] = new Vector2f(1.0f, 0.0f);
                    uvs[5] = new Vector2f(1.0f, 1.0f);
                    uvs[6] = new Vector2f(1.0f, 0.0f);
                    uvs[7] = new Vector2f(1.0f, 1.0f);

                    for (int i = 0; i < 8; i++) {
                        data.positions[positionOffset + i * 3] = positions[i].x;
                        data.positions[positionOffset + i * 3 + 1] = positions[i].y;
                        data.positions[positionOffset + i * 3 + 2] = positions[i].z;

                        data.uvs[uvOffset + i * 2] = uvs[i].x;
                        data.uvs[uvOffset + i * 2 + 1] = uvs[i].y;
                    }

                    // top
                    data.indices[indexOffset + 0] = 0 + blockId * 8;
                    data.indices[indexOffset + 1] = 1 + blockId * 8;
                    data.indices[indexOffset + 2] = 2 + blockId * 8;
                    data.indices[indexOffset + 3] = 2 + blockId * 8;
                    data.indices[indexOffset + 4] = 3 + blockId * 8;
                    data.indices[indexOffset + 5] = 0 + blockId * 8;

                    // bot
                    data.indices[indexOffset + 6] = 4 + blockId * 8;
                    data.indices[indexOffset + 7] = 7 + blockId * 8;
                    data.indices[indexOffset + 8] = 6 + blockId * 8;
                    data.indices[indexOffset + 9] = 6 + blockId * 8;
                    data.indices[indexOffset + 10] = 5 + blockId * 8;
                    data.indices[indexOffset + 11] = 4 + blockId * 8;

                    // front
                    data.indices[indexOffset + 12] = 4 + blockId * 8;
                    data.indices[indexOffset + 13] = 5 + blockId * 8;
                    data.indices[indexOffset + 14] = 1 + blockId * 8;
                    data.indices[indexOffset + 15] = 1 + blockId * 8;
                    data.indices[indexOffset + 16] = 0 + blockId * 8;
                    data.indices[indexOffset + 17] = 4 + blockId * 8;

                    // back
                    data.indices[indexOffset + 18] = 7 + blockId * 8;
                    data.indices[indexOffset + 19] = 3 + blockId * 8;
                    data.indices[indexOffset + 20] = 2 + blockId * 8;
                    data.indices[indexOffset + 21] = 2 + blockId * 8;
                    data.indices[indexOffset + 22] = 6 + blockId * 8;
                    data.indices[indexOffset + 23] = 7 + blockId * 8;

                    // left
                    data.indices[indexOffset + 24] = 6 + blockId * 8;
                    data.indices[indexOffset + 25] = 2 + blockId * 8;
                    data.indices[indexOffset + 26] = 1 + blockId * 8;
                    data.indices[indexOffset + 27] = 1 + blockId * 8;
                    data.indices[indexOffset + 28] = 5 + blockId * 8;
                    data.indices[indexOffset + 29] = 6 + blockId * 8;

                    // right
                    data.indices[indexOffset + 30] = 7 + blockId * 8;
                    data.indices[indexOffset + 31] = 4 + blockId * 8;
                    data.indices[indexOffset + 32] = 0 + blockId * 8;
                    data.indices[indexOffset + 33] = 0 + blockId * 8;
                    data.indices[indexOffset + 34] = 3 + blockId * 8;
                    data.indices[indexOffset + 35] = 7 + blockId * 8;

                }
            }
        }

        data.positionSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 24 * 4;
        data.uvSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 12 * 4;
        data.indicesSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 36 * 4;
        return data;
    }
}
