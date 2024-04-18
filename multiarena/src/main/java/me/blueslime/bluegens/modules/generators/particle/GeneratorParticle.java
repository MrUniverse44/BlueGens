package me.blueslime.bluegens.modules.generators.particle;

import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.Particle;

import java.util.Locale;

public class GeneratorParticle {
    private final Particle particle;
    private final int amount;

    public GeneratorParticle(String particle, int amount) {
        this(
            PluginConsumer.ofUnchecked(
                () -> Particle.valueOf(particle.toUpperCase(Locale.ENGLISH)),
                e -> {
                    throw new IllegalArgumentException("Invalid particle: " + particle + " please change it for your correct mc version");
                },
                null
            ),
            amount
        );
    }

    public GeneratorParticle(Particle particle, int amount) {
        this.particle = particle;
        this.amount = amount;
    }

    public Particle getParticle() {
        return particle;
    }

    public int getAmount() {
        return amount;
    }
}
