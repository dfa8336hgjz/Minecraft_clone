package core.system.texturePackage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import core.utils.Consts;
import core.utils.Paths;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class TextureMapGenerator {
    public TextureMapGenerator() {
        generateMap();
        generateGUIMap();
    }

    private void generateMap() {
        try (FileWriter jsonFile = new FileWriter(Paths.textureData)) {
            File folder = new File(Paths.imageFolder);
            File[] fileList = folder.listFiles();
            JSONArray uvList = new JSONArray();
            BufferedImage imgMap = new BufferedImage(Consts.MAP_WIDTH, Consts.MAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            int currentX = 0, currentY = 0;

            for (File file : fileList) {
                BufferedImage image = ImageIO.read(file);
                int w = image.getWidth();
                int h = image.getHeight();
                JSONObject uvObject = new JSONObject();
                if (currentY + h > 512) {
                    currentX += 16;
                    currentY = 0;
                }

                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        int color = image.getRGB(x, y);
                        imgMap.setRGB(currentX + x, currentY + y, color);
                    }
                }

                JSONArray coordArray = new JSONArray();
                for (int x = 0; x < 2; x++) {
                    for (int y = 0; y < 2; y++) {
                        JSONObject coords = new JSONObject();
                        coords.put("x", (float) (currentX + w * x) / Consts.MAP_WIDTH);
                        coords.put("y", (float) (currentY + h * y) / Consts.MAP_HEIGHT);
                        coordArray.add(coords);
                    }
                }
                uvObject.put("name", file.getName().substring(0, file.getName().lastIndexOf('.')));
                uvObject.put("coords", coordArray);

                currentY += image.getHeight();

                uvList.add(uvObject);
            }

            jsonFile.write(uvList.toJSONString());
            jsonFile.flush();
            jsonFile.close();

            ImageIO.write(imgMap, "png", new File(Paths.blockTexture));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateGUIMap() {
        try (FileWriter jsonFile = new FileWriter(Paths.guiTextureData)) {
            File folder = new File(Paths.guiImageFolder);
            File[] fileList = folder.listFiles();
            JSONArray uvList = new JSONArray();
            BufferedImage imgMap = new BufferedImage(Consts.GUI_MAP_WIDTH, Consts.GUI_MAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            int currentX = 0, currentY = 0;

            for (File file : fileList) {
                BufferedImage image = ImageIO.read(file);
                int w = image.getWidth();
                int h = image.getHeight();
                JSONObject uvObject = new JSONObject();

                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        int color = image.getRGB(x, y);
                        imgMap.setRGB(currentX + x, currentY + y, color);
                    }
                }

                JSONArray coordArray = new JSONArray();
                for (int x = 0; x < 2; x++) {
                    for (int y = 0; y < 2; y++) {
                        JSONObject coords = new JSONObject();
                        coords.put("x", (float) (currentX + w * x) / Consts.GUI_MAP_WIDTH);
                        coords.put("y", (float) (currentY + h * y) / Consts.GUI_MAP_HEIGHT);
                        coordArray.add(coords);
                    }
                }
                uvObject.put("name", file.getName().substring(0, file.getName().lastIndexOf('.')));
                uvObject.put("coords", coordArray);

                currentY += image.getHeight();

                uvList.add(uvObject);
            }

            jsonFile.write(uvList.toJSONString());
            jsonFile.flush();
            jsonFile.close();

            ImageIO.write(imgMap, "png", new File(Paths.guiTexture));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
