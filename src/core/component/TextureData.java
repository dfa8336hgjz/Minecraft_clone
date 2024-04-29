package core.component;

import org.joml.Vector2f;

public class TextureData {
    private Vector2f uvs[];
    private String name;

    public TextureData(String name, Vector2f uv0, Vector2f uv1, Vector2f uv2, Vector2f uv3){
        this.uvs = new Vector2f[]{uv0, uv1, uv2, uv3};
        this.name = name;
    }

    public TextureData(String name, Vector2f[] uvs){
        this.uvs = uvs;
        this.name = name;
    }

    public Vector2f getCoordsAt(int index){
        return uvs[index];
    }

    public String getTextureName(){
        return name;
    }
}
