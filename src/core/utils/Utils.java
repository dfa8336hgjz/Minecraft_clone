package core.utils;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class Utils {
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static boolean inRadius(int x, int z, Vector2i playerPos, int radius){
        return (x - playerPos.x) * (x - playerPos.x) 
            + (z - playerPos.y) * (z - playerPos.y)
        < radius * radius;
    }

    public static boolean hasBeenSerialized(int x, int z){
        File file = new File(Paths.binaryFolder+"/chunk"+x+"_"+z+".bin");
        return file.exists();
    }

    public static Vector3f clampVelocity(Vector3f vector){
        return new Vector3f(
            Math.max(-Consts.VELOCITY_BOUND.x, Math.min(Consts.VELOCITY_BOUND.x, vector.x)),
            Math.max(-Consts.VELOCITY_BOUND.y, Math.min(Consts.VELOCITY_BOUND.y, vector.y)),
            Math.max(-Consts.VELOCITY_BOUND.z, Math.min(Consts.VELOCITY_BOUND.z, vector.z))
        );
    }

    public static void deleteFileInFolder(String folderpath){
        File folder = new File(folderpath);
        if(!folder.exists()) return;
        for (File file : folder.listFiles()) {
            file.delete();
        }
    }
}
