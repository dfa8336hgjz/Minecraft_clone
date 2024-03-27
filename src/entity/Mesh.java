/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class Mesh {
    private int id;
    private int vertexCount;
    private int indexCount;
    private Texture texture;

    public Mesh(int id, int vertexCount, int indexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.indexCount = indexCount;
    }

    public Mesh(Mesh mesh, Texture texture) {
        this.id = mesh.id;
        this.vertexCount = mesh.vertexCount;
        this.indexCount = mesh.indexCount;
        this.texture = texture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public void setIndexCount(int indexCount) {
        this.indexCount = indexCount;
    }

    public int getTexture() {
        return texture.getId();
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

}
