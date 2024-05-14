package core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Consts {
    public static final int TEXTURE_COUNT = 925;

    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;

    public static Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // Chunk data
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 256;
    public static final int CHUNK_DEPTH = 16;

    public static final int CHUNK_RADIUS = 7;

    public static final float increment = 100.0f;

    // Packaged image
    public static final int MAP_WIDTH = 752;
    public static final int MAP_HEIGHT = 512;

    // Bitmask
    public static final long POSITION_MASK = 0x1FFFF;
    public static final long TEXID_MASK = 0x7FE0000;
    public static final long NORMAL_MASK = 0x38000000;
    public static final long UV_MASK = 0xC0000000;
    public static final long BLOCKID_MASK = 0x3E;

    // Gravity
    public static final float GRAVITY = 12.0f;
    public static final Vector3f VELOCITY_BOUND = new Vector3f(50.0f, 50.0f, 50.0f);
}
