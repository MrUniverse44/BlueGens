package me.blueslime.bluegens.modules.generators;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.PluginModule;
import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.bluegens.modules.generators.hologram.GeneratorHologram;
import me.blueslime.bluegens.modules.generators.level.GeneratorLevel;
import me.blueslime.bluegens.modules.generators.location.Coordinate;
import me.blueslime.bluegens.modules.listeners.api.GeneratorBreakEvent;
import me.blueslime.bluegens.modules.listeners.api.GeneratorPlaceEvent;
import me.blueslime.bluegens.modules.tasks.types.GenTask;
import me.blueslime.bluegens.modules.utils.location.LocationUtils;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import me.blueslime.utilitiesapi.item.nbt.ItemNBT;
import me.blueslime.utilitiesapi.text.TextReplacer;
import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Generators extends PluginModule {
    private final Map<String, GeneratorHologram> hologramMap = new ConcurrentHashMap<>();
    private final Map<Coordinate, Generator> generatorMap = new ConcurrentHashMap<>();
    private final Map<String, GeneratorLevel> levelMap = new ConcurrentHashMap<>();

    private final Map<Integer, GenTask> taskMap = new ConcurrentHashMap<>();

    public Generators(BlueGens plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        ConfigurationSection section = getGeneratorConfiguration().getConfigurationSection("generators");

        if (section != null) {
            List<String> keys = new ArrayList<>(section.getKeys(false));

            for (String key : keys) {
                ConfigurationSection keySection = section.getConfigurationSection(key);

                if (keySection == null) {
                    continue;
                }

                GeneratorLevel level = new GeneratorLevel(keySection, key);

                levelMap.put(
                    key,
                    level
                );

                taskMap.computeIfAbsent(
                    level.getSpawnRate(),
                    (t) -> new GenTask(plugin)
                );
            }
        }

        taskMap.forEach(
            (k, v) -> {
                int spawnRate = k * 20;

                v.runTaskTimer(spawnRate, 0L);
                plugin.getLogger().info("Created task for generators with interval of " + k + " seconds (" + spawnRate + ")");
            }
        );

        loadGenerators();
    }

    private void loadGenerators() {
        if (!generatorMap.isEmpty()) {
            return;
        }

        File folder = new File(plugin.getDataFolder(), "users");

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) {
            return;
        }

        for (File file : files) {

            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            ConfigurationSection generators = configuration.getConfigurationSection("generators");

            if (generators == null) {
                continue;
            }

            List<String> keys = new ArrayList<>(generators.getKeys(false));

            for (String key : keys) {

                Location location = LocationUtils.fromString(generators.getString(key + ".location", "NOT_SET"));
                String owner = generators.getString(key + ".owner", "NOT_SET");
                String levelID = generators.getString(key + ".level", plugin.getSettings().getString("settings.first-generator-level", "hay_generator"));

                boolean corrupt = generators.getBoolean(key + ".corruption", false);

                GeneratorLevel level = levelMap.get(levelID);

                if (level == null || owner.equalsIgnoreCase("NOT_SET") || location == null || location.getWorld() == null) {
                    continue;
                }

                Coordinate coordinate = Coordinate.of(location);

                Generator generator = Generator.builder(
                    owner,
                    level,
                    location.getWorld().getName(),
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    corrupt
                );

                generatorMap.put(
                    coordinate,
                    generator
                );

                GenTask task = taskMap.get(level.getSpawnRate());

                if (task == null) {
                    continue;
                }

                plugin.getLogger().info("Registered a new generator in task with spawn rate: " + level.getSpawnRate());

                task.getGenerators().add(generator);
            }
        }
    }

    @Override
    public void shutdown() {
        levelMap.clear();

        taskMap.forEach(
            (k, v) -> v.silencedCancellation()
        );

        hologramMap.forEach(
            (k, v) -> v.remove()
        );

        taskMap.clear();
    }

    @Override
    public void reload() {
        shutdown();
        initialize();
    }

    public Map<Integer, GenTask> getTaskMap() {
        return taskMap;
    }

    public Map<Coordinate, Generator> getGeneratorMap() {
        return generatorMap;
    }

    public Map<String, GeneratorLevel> getLevelMap() {
        return levelMap;
    }

    public Generator fetchGenerator(Block block) {
        Coordinate coordinate = Coordinate.of(block.getLocation());

        if (coordinate == null) {
            return null;
        }

        return generatorMap.get(coordinate);
    }

    public boolean placeGenerator(GeneratorLevel level, Player player, ItemStack item, Block block) {
        FileConfiguration configuration = getPlayerConfiguration(player);

        if (configuration == null) {
            return true;
        }

        if (fetchGenerator(block) != null) {
            return true;
        }

        ConfigurationSection section = configuration.getConfigurationSection("generators");

        int current = 0;
        int max = getMaxGenerators(player);

        if (section != null) {
            current = section.getKeys(false).size();
            if (current >= max) {
                Sender.build(player).send(
                    player,
                    plugin.getMessages(),
                    "messages.max-generators",
                    "&cYou already have all your max generators allowed <current>/<max>",
                    TextReplacer.builder()
                        .replace("<current>", current)
                        .replace("<max>", max)
                );
                return true;
            }
        }

        String id = player.getUniqueId().toString().replace("-", "");

        String nbtResult = ItemNBT.fromString(item, "bluegens-corrupt-phase");

        if (nbtResult == null || nbtResult.isEmpty()) {
            nbtResult = "false";
        }

        boolean corrupt = Boolean.parseBoolean(nbtResult);

        Generator generator = Generator.builder(
            id,
            level,
            block,
            corrupt
        );

        if (generator == null) {
            return true;
        }

        configuration.set("generators." + generator.getCode() + ".location", LocationUtils.fromLocation(block.getLocation()));
        configuration.set("generators." + generator.getCode() + ".owner", id);
        configuration.set("generators." + generator.getCode() + ".level", generator.getLevel().getId());
        configuration.set("generators." + generator.getCode() + ".corruption", corrupt);

        GenTask task = taskMap.get(level.getSpawnRate());

        if (task != null) {
            task.getGenerators().add(generator);
        }

        generatorMap.put(
            generator.getCoordinate(), generator
        );

        savePlayerConfiguration(configuration, player);

        callEvent(new GeneratorPlaceEvent(generator, player));

        Sender.build(player).send(
            player,
            plugin.getMessages(),
            "messages.placed-generator",
            "&aPlaced a new generator. (<current>/<max>)",
            TextReplacer.builder()
                .replace("<current>", current)
                .replace("<max>", max)
        );
        return false;
    }

    public int getMaxGenerators(Player player) {
        ConfigurationSection section = getSettings().getConfigurationSection("settings.max");

        if (section == null) {
            return 9;
        }

        List<String> keys = new ArrayList<>(section.getKeys(false));

        int defAmount = 9;
        int result = -1;

        for (String key : keys) {
            int amount = section.getInt(key, 9);
            if (key.equalsIgnoreCase("default")) {
                defAmount = amount;
            } else {
                if (player.hasPermission("bluegens.max.generators." + key)) {
                    result = amount;
                }
            }
        }
        if (result == -1) {
            return defAmount;
        }
        return result;
    }

    public void removeGenerator(Generator generator, Player player) {
        GenTask task = taskMap.get(generator.getLevel().getSpawnRate());

        if (task == null) {
            return;
        }

        task.getGenerators().remove(generator);
        generatorMap.remove(generator.getCoordinate());

        FileConfiguration configuration = getPlayerConfiguration(player);
        configuration.set("generators." + generator.getCode(), null);
        removeHologram(null, generator);

        Block block = generator.getBlock();

        if (block != null) {
            block.setType(Material.AIR);
        }

        callEvent(new GeneratorBreakEvent(generator, player));
        savePlayerConfiguration(configuration, player);
    }

    public Map<String, GeneratorHologram> getHologramMap() {
        return hologramMap;
    }

    public File getPlayerConfigurationFile(Player player) {
        File folder = new File(plugin.getDataFolder(), "users");

        return new File(folder, player.getUniqueId().toString().replace("-", "") + ".yml");
    }

    public File getPlayerConfigurationFile(String player) {
        File folder = new File(plugin.getDataFolder(), "users");

        return new File(folder, player.replace("-", "") + ".yml");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public FileConfiguration getPlayerConfiguration(Player player) {
        File file = getPlayerConfigurationFile(player);
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            if (file.getParentFile().mkdirs()) {
                PluginConsumer.process(
                    file::createNewFile
                );
            }
            return new YamlConfiguration();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public FileConfiguration getPlayerConfiguration(Generator generator) {
        File file = getPlayerConfigurationFile(generator.getOwnerId());
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            PluginConsumer.process(
                file::createNewFile
            );
            return new YamlConfiguration();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void savePlayerConfiguration(FileConfiguration configuration, Player player) {
        File file = getPlayerConfigurationFile(player);

        if (!file.exists()) {
            PluginConsumer.process(
                file::createNewFile
            );
        }

        PluginConsumer.process(
            () -> configuration.save(file)
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void savePlayerConfiguration(FileConfiguration configuration, Generator generator) {
        File file = getPlayerConfigurationFile(generator.getOwnerId());

        if (!file.exists()) {
            PluginConsumer.process(
                file::createNewFile
            );
        }

        PluginConsumer.process(
            () -> configuration.save(file)
        );
    }

    public GeneratorLevel fetchLevel(String nextLevelID) {
        return levelMap.get(nextLevelID);
    }

    public void upgradeGenerator(Generator generator, Player player, int currentSpawnRate, GeneratorLevel nextLevel) {
        GenTask currentTask = taskMap.get(currentSpawnRate);

        if (currentTask != null) {
            currentTask.getGenerators().remove(generator);
        }

        GenTask nextTask = taskMap.get(nextLevel.getSpawnRate());

        if (nextTask != null) {
            generator.setLevel(nextLevel);
            nextTask.getGenerators().add(generator);
        }

        FileConfiguration configuration = getPlayerConfiguration(player);

        configuration.set("generators." + generator.getCode() + ".level", nextLevel.getId());

        savePlayerConfiguration(configuration, player);
    }

    public void createHologram(Generator generator) {
        if (!plugin.getMessages().getBoolean("hologram.corrupt.enabled", true)) {
            return;
        }

        FileConfiguration configuration = getPlayerConfiguration(generator);

        configuration.set("generators." + generator.getCode() + ".corruption", generator.isCorrupted());

        savePlayerConfiguration(configuration, generator);

        hologramMap.computeIfAbsent(
            generator.getCode(),
            (g) -> new GeneratorHologram(generator, plugin.getMessages().getStringList("hologram.corrupt.lines"))
        );
    }

    public void removeHologram(Player player, Generator generator) {
        GeneratorHologram hologram = hologramMap.remove(generator.getCode());

        if (player != null) {
            FileConfiguration configuration = getPlayerConfiguration(player);
            configuration.set("generators." + generator.getCode() + ".corruption", generator.isCorrupted());
            savePlayerConfiguration(configuration, player);
        }

        if (hologram != null) {
            hologram.remove();
        }
    }
}
