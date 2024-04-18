package me.blueslime.bluegens.modules.generators.sound;

import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.Sound;

import java.util.Locale;

public class GeneratorSound {
    private final Sound sound;

    private final float volume;
    private final float pitch;

    public GeneratorSound(String sound, float volume, float pitch) {
        this(
            PluginConsumer.ofUnchecked(
                () -> Sound.valueOf(sound.toUpperCase(Locale.ENGLISH)),
                e -> {
                    throw new IllegalArgumentException("Invalid sound: " + sound + " please change it for your correct mc version");
                },
                null
            ),
            volume,
            pitch
        );
    }

    public GeneratorSound(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
