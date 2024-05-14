package core.components;

import core.utils.Consts;

public class Block {
    private int id; // id of block in blockMap.json
    private boolean isSolid;

    public Block(int id, boolean isSolid) {
        this.id = id;
        this.isSolid = isSolid;
    }

    public Block() {
        this.id = 0;
    }

    public boolean compare(Block otherBlock) {
        return (this.id == otherBlock.id);
    }

    public boolean isNullBlock() {
        return (this.id == 0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.isSolid = true;
    }

    public boolean isSolid(){
        return isSolid;
    }

    public byte wrapData(){
        byte newData = 0;
        newData |= isSolid ? 1: 0;
        newData |= ((id << 1) & Consts.BLOCKID_MASK);
        return newData;
    }

    public void unwrapData(byte data){
        isSolid = ((data & 1) == 0);
        id = (int)((data & Consts.BLOCKID_MASK) >> 1);
    }

}
