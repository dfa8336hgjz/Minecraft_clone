package core.entity;

public class Mesh {
    private int id;
    private int dataCount;
    private Material material;

    public Mesh(int id, int dataCount) {
        this.id = id;
        this.dataCount = dataCount;
        this.material = new Material();
    }

    public Mesh(int id, int dataCount, int data2Count, int texture) {
        this.id = id;
        this.dataCount = dataCount;
        this.material = new Material(texture);
    }

    public Mesh(Mesh mesh, int texture) {
        this.id = mesh.id;
        this.dataCount = mesh.dataCount;
        this.material = mesh.getMaterial();
        this.material.setTexture(texture);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public int getTexture() {
        return material.getTexture();
    }

    public void setTexture(int texture) {
        this.material.setTexture(texture);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setMaterial(int texture, float reflectance) {
        this.material.setTexture(texture);
        this.material.setReflectance(reflectance);
    }

}
