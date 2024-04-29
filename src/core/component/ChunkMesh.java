package core.component;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ChunkMesh {
    private int vao, vbo;
    private int dataCount;
    private Material material;
    
    private Vector3f position, rotation;
    private Matrix4f model;
    private float scale;

    public ChunkMesh(int vao, int vbo, int dataCount) {
        this.vao = vao;
        this.vbo = vbo;
        this.dataCount = dataCount;
        this.material = new Material();

        this.position = new Vector3f(0.0f);
        this.rotation = new Vector3f(0.0f);
        this.scale = 1.0f;
        this.model = new Matrix4f();
    }

    public ChunkMesh(int vao, int vbo, int dataCount, int data2Count, int texture) {
        this.vao = vao;
        this.vbo = vbo;
        this.dataCount = dataCount;
        this.material = new Material(texture);
    }

    public ChunkMesh(ChunkMesh mesh, int texture) {
        this.vao = mesh.vao;
        this.vbo = mesh.vbo;
        this.dataCount = mesh.dataCount;
        this.material = mesh.getMaterial();
        this.material.setTexture(texture);
    }

    public int getDataCount() {
        return dataCount;
    }

    public int getVAO() {
        return vao;
    }

    public void setBuffer(int vao, int vbo) {
        this.vao = vao;
        this.vbo = vbo;
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


    public void translate(float x, float y, float z) {
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void rotate(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
        if (this.rotation.x >= 360.0f)
            this.rotation.x = 0;
        if (this.rotation.y >= 360.0f)
            this.rotation.y = 0;
        if (this.rotation.z >= 360.0f)
            this.rotation.z = 0;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Matrix4f getModelMatrix() {
        model.identity();
        model.translate(this.position)
                .rotateX((float) Math.toRadians(this.rotation.x))
                .rotateY((float) Math.toRadians(this.rotation.y))
                .rotateZ((float) Math.toRadians(this.rotation.z))
                .scale(this.scale);

        return model;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getPos() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void cleanup(){
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        vao = 0;
        vbo = 0;
        material = null;
        position = null;
        rotation = null;
        model = null;
    }
}
