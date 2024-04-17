package me.blueslime.bluegens.modules.tasks.types;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.generators.Generators;
import me.blueslime.bluegens.modules.generators.drop.GeneratorDrop;
import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.bluegens.modules.generators.level.GeneratorLevel;
import me.blueslime.bluegens.modules.listeners.api.GeneratorCorruptEvent;
import me.blueslime.bluegens.modules.tasks.task.Task;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GenTask extends Task {
    private final List<Generator> generators = new ArrayList<>();

    public GenTask(BlueGens plugin) {
        super(plugin);
    }

    @Override
    public void run() {
        for (Generator generator : generators) {
            Block block = generator.getUpperBlock();

            if (block == null) {
                continue;
            }

            if (!block.getWorld().isChunkLoaded(block.getChunk())) {
                continue;
            }

            if (generator.isCorrupted()) {
                continue;
            }

            GeneratorLevel level = generator.getLevel();

            if (level == null) {
                continue;
            }

            if (level.isCorruptionPhase() && executeProbabilityCheck(level.getCorruptionChance())) {
                callEvent(new GeneratorCorruptEvent(generator, null));
                generator.setCorruption(true);
                plugin.getModule(Generators.class).createHologram(generator);
                continue;
            }

            World world = block.getWorld();

            double generatedChance = generateChance();

            for (GeneratorDrop drop : level.getDroppedItems()) {
                if (executeProbabilityCheck(drop.getChance(), generatedChance)) {
                    world.dropItemNaturally(
                        block.getLocation(),
                        drop.getWrapper().copy().getItem()
                    );
                }
            }
        }
    }

    public List<Generator> getGenerators() {
        return generators;
    }

    private boolean executeProbabilityCheck(double probability) {
        double fraction = probability / 100.0;
        double chance = generateChance();

        return chance < fraction;
    }

    private boolean executeProbabilityCheck(double probability, double generatedChance) {
        double fraction = probability / 100.0;

        return generatedChance < fraction;
    }

    private double generateChance() {
        return ThreadLocalRandom.current().nextDouble();
    }
}
