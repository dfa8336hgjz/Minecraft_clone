package core.renderer.supporters.texturePackage;

public class BlockData {
    private String blockName;
    private String[] textureName;

    private boolean isTransparent;

    public BlockData() {
        textureName = new String[6];
        blockName = "";
        isTransparent = true;
    }

    public BlockData(String name, String top, String bottom, String front, String back, String left,
            String right, boolean isTransparent) {
        textureName = new String[] { top, bottom, front, back, left, right };
        this.blockName = name;
        this.isTransparent = isTransparent;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getTextureNameAtSide(int side) {
        return textureName[side];
    }

    public void setTextureName(String[] textureName) {
        this.textureName = textureName;
    }

    public boolean Transparent() {
        return isTransparent;
    }

    public void setTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }
}
