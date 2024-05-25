package core.system.texturePackage;

import org.joml.Vector2f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import core.components.BlockData;
import core.components.TextureData;
import core.utils.Consts;
import core.utils.Paths;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextureMapLoader {
    private JSONParser parser;
    private static Map<String, Integer> textureMap;
    private static Map<String, TextureData> guiTextureMap;
    private static ArrayList<BlockData> blockDataMap;
    private static ArrayList<TextureData> blockTextureDataMap;

    public TextureMapLoader() {
        parser = new JSONParser();
        try {
            loadblockDataMap();
            loadTextureUV();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void loadTextureUV() throws IOException, ParseException {
        Reader reader = new FileReader(Paths.textureData);
        JSONArray jsonArray = (JSONArray) parser.parse(reader);
        setTextureMap(jsonArray);

        reader = new FileReader(Paths.guiTextureData);
        jsonArray = (JSONArray) parser.parse(reader);
        setGUITextureMap(jsonArray);

        reader.close();
        reader = null;
    }

    public void loadblockDataMap() throws IOException, ParseException {
        Reader reader = new FileReader(Paths.blockData);
        JSONArray jsonArray = (JSONArray) parser.parse(reader);

        setblockDataMap(jsonArray);

        reader.close();
        reader = null;
    }

    private void setblockDataMap(JSONArray map){
        blockDataMap = new ArrayList<>();

        for (Object object : map) {
            JSONObject jsonObj = (JSONObject) object;
            BlockData block = new BlockData(jsonObj.get("name").toString(), false,
                    jsonObj.get("top").toString(),
                    jsonObj.get("bottom").toString(), 
                    jsonObj.get("front").toString(),
                    jsonObj.get("back").toString(),
                    jsonObj.get("left").toString(), 
                    jsonObj.get("right").toString());
            blockDataMap.add(block);
        }
    }

    private void setTextureMap(JSONArray map){
        textureMap = new HashMap<>();
        blockTextureDataMap = new ArrayList<>();
        int count = 0;
        for (Object obj : map) {
            JSONObject jsonObj = (JSONObject) obj;
            JSONArray currentTexture = (JSONArray)jsonObj.get("coords");
            Vector2f[] uvs = new Vector2f[4];
            for (int i = 0; i < 4; i++) {
                JSONObject result = (JSONObject) currentTexture.get(i);
                Number x = (Number) result.get("x");
                Number y = (Number) result.get("y");
                uvs[i] = new Vector2f(x.floatValue(), y.floatValue());
            }

            TextureData texture = new TextureData(jsonObj.get("name").toString(), uvs);
            blockTextureDataMap.add(texture);
            textureMap.put(jsonObj.get("name").toString(), count++);
        }
    }

    private void setGUITextureMap(JSONArray map){
        guiTextureMap = new HashMap<>();
        for (Object obj : map) {
            JSONObject jsonObj = (JSONObject) obj;
            JSONArray currentTexture = (JSONArray)jsonObj.get("coords");
            Vector2f[] uvs = new Vector2f[4];
            for (int i = 0; i < 4; i++) {
                JSONObject result = (JSONObject) currentTexture.get(i);
                Number x = (Number) result.get("x");
                Number y = (Number) result.get("y");
                uvs[i] = new Vector2f(x.floatValue(), y.floatValue());
            }

            TextureData texture = new TextureData(jsonObj.get("name").toString(), uvs);
            guiTextureMap.put(jsonObj.get("name").toString(), texture);
        }

    }

    public static BlockData getBlock(int blockId){
        return blockDataMap.get(blockId);
    }

    public static int getFaceTextureId(int blockId, int face){
        BlockData block = blockDataMap.get(blockId);
        return textureMap.get(block.getTextureNameAtSide(face));
    }

    public static String getTextureName(int id){
        return blockTextureDataMap.get(id).getTextureName();
    }

    public static Vector2f getGUIUV(String name, int id){
        return guiTextureMap.get(name).getCoordsAt(id);
    }

    public static TextureData getGUITexture(String name){
        return guiTextureMap.get(name);
    }

    public static float[] getTexCoordList(){
        float[] texCoordList = new float[8 * Consts.TEXTURE_COUNT];
        int texCursor = 0;

        for (TextureData f : blockTextureDataMap) {
            texCoordList[texCursor] = f.getCoordsAt(0).x;
            texCoordList[texCursor + 1] = f.getCoordsAt(0).y;
            texCoordList[texCursor + 2] = f.getCoordsAt(1).x;
            texCoordList[texCursor + 3] = f.getCoordsAt(1).y;
            texCoordList[texCursor + 4] = f.getCoordsAt(2).x;
            texCoordList[texCursor + 5] = f.getCoordsAt(2).y;
            texCoordList[texCursor + 6] = f.getCoordsAt(3).x;
            texCoordList[texCursor + 7] = f.getCoordsAt(3).y;

            texCursor += 8;
        }

        return texCoordList;
    }
}
