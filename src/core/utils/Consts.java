package core.utils;

import org.joml.Vector4f;

public class Consts {
    public static final String vsPath = "src\\core\\shader\\vertex.shader";
    public static final String fsPath = "src\\core\\shader\\fragment.shader";

    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;

    public static Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
}
