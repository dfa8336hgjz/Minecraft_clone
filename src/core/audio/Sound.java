package core.audio;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.openal.AL10.*;

import org.lwjgl.system.MemoryStack;

public class Sound {
    private int bufferId;
    private int sourceId;

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loop){
        ShortBuffer rawAudioBuffer;
        int channel, sampleRate;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channelBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);
            rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelBuffer, sampleRateBuffer);

            if(rawAudioBuffer == null){
                System.out.println("Cannot load sound");
            }

            channel = channelBuffer.get();
            sampleRate = sampleRateBuffer.get();
        }

        int format = -1;
        if(channel == 1){
            format = AL_FORMAT_MONO16;  
        }
        else if(channel == 2){
            format = AL_FORMAT_STEREO16;
        }
        
        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        sourceId = alGenSources();
        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_LOOPING, loop? 1: 0);
        alSourcei(sourceId, AL_POSITION, 0);
        alSourcef(sourceId, AL_GAIN, 1.4f);
    }

    public void cleanup(){
        alDeleteBuffers(bufferId);
        alDeleteSources(sourceId);
    }

    public void play(){
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if(state == AL_STOPPED){
            isPlaying = false;
            alSourcei(sourceId, AL_POSITION, 0);
        }

        if(!isPlaying){
            alSourcePlay(sourceId);
            isPlaying = true;
        }
    }

    public void stop(){
        if(isPlaying){
            alSourceStop(sourceId);
            isPlaying = false;
        }
    }
}
