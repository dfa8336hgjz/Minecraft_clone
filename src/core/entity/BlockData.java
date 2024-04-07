package core.entity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class BlockData {
    private static Map<String, Integer> textureId;
    private static JSONArray blockMap;
    private static JSONArray textureUV;

    public static void setBlockMap(JSONArray map) {
        blockMap = map;
    }

    public static void setTextureUV(JSONArray map) {
        textureUV = map;
        textureId = new HashMap<>();
        int id = 0;
        for (Object obj : map) {
            JSONObject jsonObj = (JSONObject) obj;
            textureId.put(jsonObj.get("name").toString(), id);
            id++;
        }
    }

    public static JSONArray getUV(int blockId, int side) {
        JSONObject block = (JSONObject) blockMap.get(blockId);
        String thisSide;
        switch (side) {
            case 0:
                thisSide = "top";
                break;
            case 1:
                thisSide = "bottom";
                break;
            case 2:
                thisSide = "front";
                break;
            case 3:
                thisSide = "back";
                break;
            case 4:
                thisSide = "left";
                break;
            case 5:
                thisSide = "right";
                break;
            default:
                thisSide = "";
                break;
        }
        JSONObject texture = (JSONObject) textureUV.get(textureId.get(block.get(thisSide)));
        JSONArray uvCoords = (JSONArray) texture.get("coords");

        return uvCoords;
    }

    public static float[] getCoordsAt(JSONArray array, int index) {
        JSONObject result = (JSONObject) array.get(index);
        Number x = (Number) result.get("x");
        Number y = (Number) result.get("y");
        System.out.println(index + ": " + x + " " + y);
        return new float[] { x.floatValue(), y.floatValue() };
    }

}
