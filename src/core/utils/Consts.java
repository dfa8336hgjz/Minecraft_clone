package core.utils;

import org.joml.Vector4f;

public class Consts {
    public static final String fsPath = "src\\assets\\shader\\fragment.shader";
    public static final String vsPath = "src\\assets\\shader\\vertex.shader";

    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;

    public static Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // Chunk data
    public static final int CHUNK_WIDTH = 4;
    public static final int CHUNK_HEIGHT = 4;
    public static final int CHUNK_DEPTH = 4;
}
