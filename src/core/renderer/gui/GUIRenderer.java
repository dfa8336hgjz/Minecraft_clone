package core.renderer.gui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import org.joml.Vector2f;
import org.joml.Vector2i;

import core.components.TextureData;
import core.renderer._2DRendererBatch;
import core.renderer.font.FontBatch;
import core.system.Input;
import core.system.texturePackage.TextureMapLoader;
import core.utils.Paths;

enum UIState{
    Default,
    Hover,
    Click
}

public class GUIRenderer {
    private _2DRendererBatch batch2d;
    private FontBatch batchFont;

    public GUIRenderer() throws Exception{
        batchFont = new FontBatch();
        batchFont.initBatch();

        batch2d = new _2DRendererBatch();
        batch2d.initBatch(Paths.guiTexture, 1);
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

        batch2d.drawSprite(button.position.x, button.position.y, button.size.x, button.size.y, texture);
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

    public void drawSprite(float x, float y, float sizeX, float sizeY, String spriteName){
        batch2d.drawSprite(x, y, sizeX, sizeY, TextureMapLoader.getGUITexture(spriteName));
    }

    public void drawTextHorizontalCenter(String text, int y, float scale, int rgb){
        batchFont.drawTextHorizontalCenter(text, y, scale, rgb);
    }

    public void flushBatch(){
        batch2d.flushBatch();
        batchFont.flushBatch();
    }

    public void cleanup(){
        batch2d.cleanup();
        batchFont.cleanup();
    }
}
