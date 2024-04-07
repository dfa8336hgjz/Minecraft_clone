package core.entity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockData {
    private static Map<String, Integer> textureId;
    private static ArrayList<Block> blockMap;
    private static JSONArray textureMap;
    private static JSONArray currentTexture;

    public static void setBlockMap(JSONArray map) {
        blockMap = new ArrayList<>();

        for (Object object : map) {
            JSONObject jsonObj = (JSONObject) object;
            Block block = new Block(jsonObj.get("name").toString(), false, jsonObj.get("top").toString(),
                    jsonObj.get("bottom").toString(), jsonObj.get("front").toString(), jsonObj.get("back").toString(),
                    jsonObj.get("left").toString(), jsonObj.get("right").toString());
            blockMap.add(block);
        }
    }

    public static void setTextureMap(JSONArray map) {
        textureMap = map;
        textureId = new HashMap<>();
        int id = 0;
        for (Object obj : map) {
            JSONObject jsonObj = (JSONObject) obj;
            textureId.put(jsonObj.get("name").toString(), id);
            id++;
        }
    }

    public static void setCurrentTexture(int blockId, int side) {
        JSONObject texture = (JSONObject) textureMap.get(textureId.get(blockMap.get(blockId).getTextureAt(side)));
        currentTexture = (JSONArray) texture.get("coords");
    }

    public static float[] getCoordsAt(int index) {
        JSONObject result = (JSONObject) currentTexture.get(index);
        Number x = (Number) result.get("x");
        Number y = (Number) result.get("y");
        return new float[] { x.floatValue(), y.floatValue() };
    }

    public static Block getBlock(int index) {
        return blockMap.get(index);
    }
}
