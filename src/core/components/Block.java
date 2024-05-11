package core.components;

public class Block {
    private int id; // id of block in blockMap.json
    private int lightLevel;
    private int rotation;
    private long padding;

    public Block(int id, int lightLevel, int rotation, long padding) {
        this.id = id;
        this.lightLevel = lightLevel;
        this.rotation = rotation;
        this.padding = padding;
    }

    public Block() {
        this.id = -1;
        this.lightLevel = 0;
        this.rotation = 0;
        this.padding = 0;
    }

    public boolean compare(Block otherBlock) {
        return (this.id == otherBlock.id) && (this.lightLevel == otherBlock.lightLevel)
                && (this.rotation == otherBlock.rotation) && (this.padding == otherBlock.padding);
    }

    public boolean isNullBlock() {
        return (this.id == -1) && (this.lightLevel == 0)
                && (this.rotation == 0) && (this.padding == 0);
    }

    public int getId() {
        return id;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public int getRotation() {
        return rotation;
    }

    public long getPadding() {
        return padding;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPadding() {

    }

    public void setRotation() {

    }

    public void setLightLevel() {

    }

}
