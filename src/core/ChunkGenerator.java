package core;

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

                    // bot and top indices
                    for (int i = 0; i < 2; i++) {
                        data.indices[indexOffset + i * 6] = 0 + i * 4 + blockId * 8;
                        data.indices[indexOffset + i * 6 + 1] = 1 + i * 4 + blockId * 8;
                        data.indices[indexOffset + i * 6 + 2] = 2 + i * 4 + blockId * 8;
                        data.indices[indexOffset + i * 6 + 3] = 0 + i * 4 + blockId * 8;
                        data.indices[indexOffset + i * 6 + 4] = 2 + i * 4 + blockId * 8;
                        data.indices[indexOffset + i * 6 + 5] = 3 + i * 4 + blockId * 8;
                    }

                    // front
                    data.indices[indexOffset + 12] = 4 + blockId * 8;
                    data.indices[indexOffset + 13] = 5 + blockId * 8;
                    data.indices[indexOffset + 14] = 1 + blockId * 8;
                    data.indices[indexOffset + 15] = 1 + blockId * 8;
                    data.indices[indexOffset + 16] = 0 + blockId * 8;
                    data.indices[indexOffset + 17] = 4 + blockId * 8;

                    // back
                    data.indices[indexOffset + 18] = 3 + blockId * 8;
                    data.indices[indexOffset + 19] = 7 + blockId * 8;
                    data.indices[indexOffset + 20] = 6 + blockId * 8;
                    data.indices[indexOffset + 21] = 6 + blockId * 8;
                    data.indices[indexOffset + 22] = 2 + blockId * 8;
                    data.indices[indexOffset + 23] = 3 + blockId * 8;

                    // left
                    data.indices[indexOffset + 24] = 0 + blockId * 8;
                    data.indices[indexOffset + 25] = 3 + blockId * 8;
                    data.indices[indexOffset + 26] = 4 + blockId * 8;
                    data.indices[indexOffset + 27] = 4 + blockId * 8;
                    data.indices[indexOffset + 28] = 3 + blockId * 8;
                    data.indices[indexOffset + 29] = 7 + blockId * 8;

                    // right
                    data.indices[indexOffset + 30] = 2 + blockId * 8;
                    data.indices[indexOffset + 31] = 1 + blockId * 8;
                    data.indices[indexOffset + 32] = 6 + blockId * 8;
                    data.indices[indexOffset + 33] = 6 + blockId * 8;
                    data.indices[indexOffset + 34] = 1 + blockId * 8;
                    data.indices[indexOffset + 35] = 5 + blockId * 8;

                }
            }
        }

        data.positionSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 24 * 4;
        data.uvSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 12 * 4;
        data.indicesSizeByte = Consts.CHUNK_DEPTH * Consts.CHUNK_HEIGHT * Consts.CHUNK_WIDTH * 36 * 4;
        return data;
    }
}
