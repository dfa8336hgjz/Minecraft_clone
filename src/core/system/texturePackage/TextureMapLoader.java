package core.system.texturePackage;

import org.joml.Vector2f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import core.component.BlockData;
import core.component.TextureData;
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
    private static ArrayList<BlockData> blockDataMap;
    private static Map<String, Integer> textureMap;
    private static ArrayList<TextureData> textureDataMap;

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
        textureDataMap = new ArrayList<>();
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
            textureDataMap.add(texture);
            textureMap.put(jsonObj.get("name").toString(), count++);

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
        return textureDataMap.get(id).getTextureName();
    }

    public static float[] getTexCoordList(){
        float[] texCoordList = new float[8 * Consts.TEXTURE_COUNT];
        int texCursor = 0;

        for (TextureData f : textureDataMap) {
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
