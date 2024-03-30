/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.entity;

public class Mesh {
    private int id;
    private int vertexCount;
    private int indexCount;
    private Material material;

    public Mesh(int id, int vertexCount, int indexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.indexCount = indexCount;
        this.material = new Material();
    }

    public Mesh(int id, int vertexCount, int indexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.indexCount = indexCount;
        this.material = new Material(texture);
    }

    public Mesh(Mesh mesh, Texture texture) {
        this.id = mesh.id;
        this.vertexCount = mesh.vertexCount;
        this.indexCount = mesh.indexCount;
        this.material = mesh.getMaterial();
        this.material.setTexture(texture);
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
        return material.getTexture().getId();
    }

    public void setTexture(Texture texture) {
        this.material.setTexture(texture);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setMaterial(Texture texture, float reflectance) {
        this.material.setTexture(texture);
        this.material.setReflectance(reflectance);
    }

}
