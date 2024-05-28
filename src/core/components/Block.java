package core.components;

import java.util.ArrayList;

import core.renderer.supporters.texturePackage.BlockData;
import core.utils.Consts;

public class Block {
    public int id; // id of block in blockMap.json
    public boolean isTransparent;

    public Block(int id, ArrayList<BlockData> data) {
        this.id = id;
        this.isTransparent = data.get(id).Transparent();
    }

    public Block() {
        this.id = 0;
        this.isTransparent = false;
    }

    public boolean compare(Block otherBlock) {
        return (this.id == otherBlock.id);
    }

    public boolean isNullBlock() {
        return (this.id == 0);
    }

    public int wrapData(){
        int newData = 0;
        newData |= isTransparent ? 1 : 0;
        newData |= ((id << 1) & Consts.BLOCKID_MASK);
        return newData;
    }

    public void unwrapData(int data){
        isTransparent = ((data & 1) == 0);
        id = (int)((data & Consts.BLOCKID_MASK) >> 1);
    }

}
