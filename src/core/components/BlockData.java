package core.components;

public class BlockData {
    private String blockName;
    private String[] textureName;

    private boolean isSolid;

    public BlockData() {
        textureName = new String[6];
        blockName = "";
        isSolid = true;
    }

    public BlockData(String name, boolean isSolid, String top, String bottom, String front, String back, String left,
            String right) {
        textureName = new String[] { top, bottom, front, back, left, right };
        this.blockName = name;
        this.isSolid = isSolid;
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

    public boolean isSolid() {
        return isSolid;
    }

    public void setTransparent(boolean isSolid) {
        this.isSolid = isSolid;
    }

}
