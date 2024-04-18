package me.blueslime.bluegens.modules.tasks.types;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.generators.Generators;
import me.blueslime.bluegens.modules.generators.drop.GeneratorDrop;
import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.bluegens.modules.generators.level.GeneratorLevel;
import me.blueslime.bluegens.modules.listeners.api.GeneratorCorruptEvent;
import me.blueslime.bluegens.modules.tasks.task.Task;
import me.blueslime.bluegens.modules.utils.generator.GeneratorUtils;
import me.blueslime.bluegens.modules.utils.list.ReturnableArrayList;
import me.blueslime.utilitiesapi.item.ItemWrapper;
import me.blueslime.utilitiesapi.text.TextReplacer;
import net.milkbowl.vault.economy.Economy;
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
                if (!plugin.getModule(Generators.class).getHologramMap().containsKey(generator.getCode())) {
                    plugin.getModule(Generators.class).createHologram(generator);
                }
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

            Economy economy = plugin.getEconomy();

            for (GeneratorDrop drop : level.getDroppedItems()) {
                if (executeProbabilityCheck(drop.getChance(), generatedChance)) {
                    ItemWrapper original = drop.getWrapper();
                    ItemWrapper wrapper = original.copy();

                    String displayName = level.getDisplayName();

                    String uCost = economy != null ? economy.format(level.getNextCost()) : String.valueOf(level.getNextCost());
                    String rCost = economy != null ? economy.format(level.getCorruptionCost()) : String.valueOf(level.getCorruptionCost());
                    String dCost = economy != null ? economy.format(drop.getPrice()) : String.valueOf(drop.getPrice());

                    TextReplacer replacer = TextReplacer.builder()
                        .replace(
                            "<generator_displayname>",
                            displayName
                        ).replace(
                            "<generator_display_name>",
                            displayName
                        ).replace(
                            "<display_name>",
                            displayName
                        ).replace(
                            "<displayname>",
                            displayName
                        ).replace(
                            "<generator_time>",
                            String.valueOf(level.getSpawnRate())
                        ).replace(
                            "<time>",
                            String.valueOf(level.getSpawnRate())
                        ).replace(
                            "<generator_upgrade>",
                            uCost
                        ).replace(
                            "<upgrade>",
                            uCost
                        ).replace(
                            "<generator_repair>",
                            rCost
                        ).replace(
                            "<repair>",
                            rCost
                        ).replace(
                            "<drop_price>",
                            dCost
                        ).replace(
                            "<price>",
                            dCost
                        );

                    wrapper.setName(
                        replacer.apply(original.getName())
                    );

                    wrapper.setLore(
                        new ReturnableArrayList<>(original.getLore()).replace(
                            replacer::apply
                        )
                    );

                    world.dropItemNaturally(
                        block.getLocation(),
                        wrapper.getItem()
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
