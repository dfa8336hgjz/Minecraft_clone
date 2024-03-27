/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package core.interfaces;

public interface IGameLogic {
    void init() throws Exception;

    void input();

    void update();

    void render();

    void cleanup();
}
