package core.renderer.gui;

import org.joml.Vector2i;

import core.renderer.supporters.texturePackage.TextureData;

public class Button {
    public Vector2i position;
    public Vector2i size;
    public TextureData defaultSprite;
    public TextureData hoverSprite;
    public TextureData clickSprite;
    public String text;
    public float textScale;
}
