package core.audio;

import static org.lwjgl.openal.ALC10.*;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenALInitializer {
    private long device;
    private long context;

    public void init() {
        // Open the default device
        device = alcOpenDevice(alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER));
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        int[] attributes = {0};
        context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);
        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities al = AL.createCapabilities(alcCapabilities);
        if(al == null){
            System.out.println(1);
        }
    }

    public void cleanup() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

}
