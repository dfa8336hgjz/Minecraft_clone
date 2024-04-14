package core.utils;

import org.joml.Vector4f;

public class Consts {

    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;

    public static Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // Chunk data
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 50;
    public static final int CHUNK_DEPTH = 16;

    public static final int NUM_OF_CHUNK = 100;

    public static final float increment = 100.0f;

    // Packaged image
    public static final int MAP_WIDTH = 752;
    public static final int MAP_HEIGHT = 512;
}
