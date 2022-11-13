package com.dndcraft.atlas.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

/**
 * This enum stores all of DNDCrafts custom sounds, JUST BECAUSE ITS REGISTERED HERE DOESNT MEAN ITS AUTO REGISTERED
 * IN THE RESOURCE PACK! The client will additionally ignore any play requests if a resource pack's assets aren't present
 * */

public enum AtlasSounds {
    DOOR_LOCKED("door_locked", 1, 1, Sound.Source.BLOCK),
    ;

    private final String soundName;
    private final int pitch, volume;
    private final Sound.Source source;

    /**
     * This doc is here to explain some of the nuances of registering a sound in here
     * @param soundName is the name of the sound file spare the .ogg ending (this enum will always assume the namespace is dndcraft)
     * @param source is the sound source that the server tells the client, this is so they can actually use their sound settings to lower the categories individually
     * @param pitch is the pitch of the sound (Range 0f-2f) If you go higher or lower it just defaults to 1f
     * @param volume is the volume of the sound, realistically nothing should be higher than 3 unless its a global sound
     * */
    AtlasSounds(String soundName, int pitch, int volume, Sound.Source source){
        this.soundName = soundName;
        this.pitch = pitch;
        this.volume = volume;
        this.source = source;
    }

    /**
     * Gets an AtlasSound at a standard volume (1f) and pitch (1f)
     * */
    public Sound getSoundAtDefault(){
        return getSoundWithModified(1f,1f);
    }

    /**
     * Gets a Sound with the pre-specified values registered in this enum
     * */
    public Sound getSound(){
        return getSoundWithModified(volume, pitch);
    }

    /**
     * Gets a Sound with overrided values for volume and pitch
     * @param pitch pitch of the sound (Range 0f-2f)
     * @param volume volume of the sound (Range 0f-10f)
     * */
    public Sound getSoundWithModified(float volume, float pitch){
        return Sound.sound(Key.key("dndcraft",soundName), source, volume, pitch);
    }

}
