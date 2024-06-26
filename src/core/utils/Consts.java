package core.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Consts {
    public static final int TEXTURE_COUNT = 925;

    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 2000f;

    public static Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // Window size
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 960;
    
    // MVP
    public static float FOV = (float) Math.toRadians(60);
    public static Matrix4f GUI_PROJECTION = 
        new Matrix4f().identity().ortho(0, Consts.WINDOW_WIDTH, Consts.WINDOW_HEIGHT, 0, 1f, 10f);

    // Chunk data
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 100;
    public static final int CHUNK_DEPTH = 16;
    public static final int CHUNK_RADIUS = 7;

    public static final float increment = 100.0f;

    // Packaged image
    public static final int MAP_WIDTH = 752;
    public static final int MAP_HEIGHT = 512;

    public static final int GUI_MAP_WIDTH = 385;
    public static final int GUI_MAP_HEIGHT = 264;

    // Bitmask
    public static final long POSITION_MASK = 0x1FFFF;
    public static final long TEXID_MASK = 0x7FE0000;
    public static final long NORMAL_MASK = 0x38000000;
    public static final long UV_MASK = 0xC0000000;
    public static final long BLOCKID_MASK = 0x3E;

    // Gravity
    public static final float GRAVITY = 10.0f;
    public static final Vector3f VELOCITY_BOUND = new Vector3f(50.0f, 50.0f, 50.0f);

    // Player Input
    public static final float MouseSentivity = 0.03f;
    public static final float CREATIVE_Speed = 3f;
    public static final float SPECTATOR_Speed = 15f;
}
