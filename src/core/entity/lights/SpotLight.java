package core.entity.lights;

import org.joml.Vector3f;

public class SpotLight {
    private PointLight pointLight;
    private Vector3f coneDir;
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f coneDir, float cutoff) {
        this.pointLight = pointLight;
        this.coneDir = coneDir;
        this.cutoff = cutoff;
    }

    public SpotLight(SpotLight light) {
        this.coneDir = light.getConeDir();
        this.pointLight = light.getPointLight();
        this.cutoff = light.getCutOff();
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public float getCutOff() {
        return cutoff;
    }

    public void setCutOff(float cutoff) {
        this.cutoff = cutoff;
    }

    public Vector3f getConeDir() {
        return coneDir;
    }

    public void setConeDir(Vector3f coneDir) {
        this.coneDir = coneDir;
    }

}
