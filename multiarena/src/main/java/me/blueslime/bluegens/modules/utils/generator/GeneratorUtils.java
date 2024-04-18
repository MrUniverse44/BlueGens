package me.blueslime.bluegens.modules.utils.generator;

import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.bluegens.modules.generators.particle.GeneratorParticle;
import me.blueslime.bluegens.modules.generators.sound.GeneratorSound;
import me.blueslime.bluegens.modules.utils.PluginUtilities;
import me.blueslime.utilitiesapi.tools.PluginTools;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeneratorUtils {
    public static void spawnParticles(Generator generator, List<String> particles) {
        if (generator == null || particles == null || particles.isEmpty()) {
            return;
        }

        Location location = generator.getParticleLocation();

        if (location == null || location.getWorld() == null) {
            return;
        }

        List<GeneratorParticle> particleList = new ArrayList<>();

        for (String particle : particles) {
            String[] split = particle.replace(" ", "").split(",");

            String particleName;
            int amount;

            if (split.length >= 2) {
                if (PluginTools.isNumber(split[1])) {
                    amount = Integer.parseInt(split[1]);
                    particleName = split[0];
                } else {
                    amount = PluginTools.isNumber(split[0]) ? Integer.parseInt(split[0]) : 3;
                    particleName = PluginTools.isNumber(split[0]) ? split[1] : split[0];
                }
            } else {
                amount = 3;
                particleName = split[0];
            }

            particleList.add(new GeneratorParticle(particleName, amount));
        }

        World world = location.getWorld();

        for (GeneratorParticle particle : particleList) {
            if (particle.getParticle() == null) {
                continue;
            }
            world.spawnParticle(
                particle.getParticle(),
                location,
                particle.getAmount()
            );
        }
    }


    public static void playSounds(Generator generator, List<String> sounds) {
        if (generator == null || sounds == null || sounds.isEmpty()) {
            return;
        }

        Location location = generator.getParticleLocation();

        if (location == null || location.getWorld() == null) {
            return;
        }

        List<GeneratorSound> soundList = new ArrayList<>();

        for (String text : sounds) {
            String[] arguments = text.replace(" ", "").split(",");

            if (arguments.length == 1) {
                soundList.add(
                    new GeneratorSound(
                        text.toUpperCase(Locale.ENGLISH),
                        1,
                        1
                    )
                );
            } else if (arguments.length == 2) {
                soundList.add(
                    new GeneratorSound(
                        arguments[0].toUpperCase(Locale.ENGLISH),
                        PluginUtilities.isFloat(arguments[1]) ? Float.parseFloat(arguments[1]) : 1,
                        1
                    )
                );
            } else if (arguments.length >= 3) {
                soundList.add(
                    new GeneratorSound(
                        arguments[0],
                        PluginUtilities.isFloat(arguments[1]) ? Float.parseFloat(arguments[1]) : 1,
                        PluginUtilities.isFloat(arguments[2]) ? Float.parseFloat(arguments[2]) : 1
                    )
                );
            }
        }

        World world = location.getWorld();

        for (GeneratorSound soundTask : soundList) {
            if (soundTask.getSound() == null) {
                continue;
            }
            world.playSound(
                location,
                soundTask.getSound(),
                soundTask.getVolume(),
                soundTask.getPitch()
            );
        }
    }
}
