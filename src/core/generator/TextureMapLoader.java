package core.generator;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import core.entity.BlockData;
import core.utils.Paths;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class TextureMapLoader {
    private JSONParser parser;

    public TextureMapLoader() {
        parser = new JSONParser();
        try {
            loadBlockMap();
            loadTextureUV();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void loadTextureUV() throws IOException, ParseException {
        Reader reader = new FileReader(Paths.textureData);
        JSONArray jsonArray = (JSONArray) parser.parse(reader);
        BlockData.setTextureMap(jsonArray);
        reader.close();
        reader = null;
    }

    public void loadBlockMap() throws IOException, ParseException {
        Reader reader = new FileReader(Paths.blockData);
        JSONArray jsonArray = (JSONArray) parser.parse(reader);
        BlockData.setBlockMap(jsonArray);
        reader.close();
        reader = null;
    }
}
