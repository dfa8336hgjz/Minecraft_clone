package core.renderer.gui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import org.joml.Vector2f;
import org.joml.Vector2i;

import core.components.BlockData;
import core.components.TextureData;
import core.gameplay.Player;
import core.renderer._2DRendererBatch;
import core.renderer.font.FontBatch;
import core.system.Input;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Paths;

public class CraftGUIRenderer {
    private _2DRendererBatch guiBatch;
    private _2DRendererBatch blockBatch;
    private FontBatch batchFont;

    public CraftGUIRenderer() throws Exception{
        batchFont = new FontBatch();
        batchFont.initBatch();

        guiBatch = new _2DRendererBatch();
        guiBatch.initBatch(Paths.guiTexture, 1);

        blockBatch = new _2DRendererBatch();
        blockBatch.initBatch(Paths.blockTexture, 2);
    }

    public boolean isButtonClicked(Button button){
        boolean result = false;
        TextureData texture;
        UIState state = mouseInUIComponent(button.position, button.size);
        if(state == UIState.Click){
            result = true;
            texture = button.clickSprite;
        }
        else if(state == UIState.Hover){
            texture = button.hoverSprite;
        }
        else{
            texture = button.defaultSprite;
        }

        guiBatch.drawSprite(button.position.x, button.position.y, button.size.x, button.size.y, texture);
        batchFont.drawTextOnButton(button, button.textScale, 0x636363);

        return result;
    }

    public UIState mouseInUIComponent(Vector2i position, Vector2i size){
        Vector2f playerMousePos = new Vector2f((float)Input.currentMousePos.x, (float)Input.currentMousePos.y);
       
        if(playerMousePos.x >= position.x && playerMousePos.x <= position.x + size.x
        && playerMousePos.y >= position.y && playerMousePos.y <= position.y + size.y)
        {
            if(Input.isMousePressed(GLFW_MOUSE_BUTTON_LEFT)){
                return UIState.Click;
            }
            return UIState.Hover;
        }
        return UIState.Default;
    }

    public void draw(){
        int currentBlockId = Player.instance.getCurrentBlockTypeId();
        BlockData currentBlockData = TextureMapLoader.getBlockData(currentBlockId);
        guiBatch.drawSprite(785, 465, 30, 30, TextureMapLoader.getGUITexture("picker"));
        for (int i = 0; i < 12; i++) {
            int thisBlockId = Player.instance.blockInventory[i];
            if(i == Player.instance.slotPicking)
                guiBatch.drawSprite(380 + i * 70, 890, 70, 70, TextureMapLoader.getGUITexture("inventoryPicking"));
            else
                guiBatch.drawSprite(380 + i * 70, 890, 70, 70, TextureMapLoader.getGUITexture("inventoryDefault"));
            
            blockBatch.drawSprite(397 + i * 70, 907, 36, 36, TextureMapLoader.getBlockTextureIcon(thisBlockId));
        }
        
        batchFont.drawTextHorizontalCenter(currentBlockData.getBlockName(), 850, 0.35f, 0xFFFFFF);
    }

    public void flushBatch(){
        blockBatch.flushBatch();
        guiBatch.flushBatch();
        batchFont.flushBatch();
    }

    public void cleanup(){
        guiBatch.cleanup();
        blockBatch.cleanup();
        batchFont.cleanup();
    }
}
