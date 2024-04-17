package me.blueslime.bluegens.modules.plugin;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.PluginModule;
import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.bluegens.modules.storage.player.GenPlayer;
import me.blueslime.bluegens.modules.utils.file.FileUtilities;
import me.blueslime.utilitiesapi.reflection.utils.storage.PluginStorage;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Plugin extends JavaPlugin {
    private final PluginStorage<UUID, GenPlayer> playerStorage = PluginStorage.initAsConcurrentHash();
    private final Map<Class<?>, PluginModule> moduleMap = new ConcurrentHashMap<>();
    private final List<Generator> generatorList = new ArrayList<>();

    private FileConfiguration generators;
    private FileConfiguration settings;
    private FileConfiguration messages;

    protected void initialize(BlueGens plugin) {
        build();

        registerModules();

        loadModules();
    }

    public void build() {
        generators = loadConfiguration(getDataFolder(), "generators.yml");
        settings = loadConfiguration(getDataFolder(), "settings.yml");
        messages = loadConfiguration(getDataFolder(), "messages.yml");
    }

    public abstract void registerModules();

    private void loadModules() {
        for (PluginModule module : moduleMap.values()) {
            module.initialize();
        }
    }

    public void shutdown() {
        for (PluginModule module : moduleMap.values()) {
            module.shutdown();
        }
    }

    public void reload() {
        build();

        for (PluginModule module : moduleMap.values()) {
            module.reload();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends PluginModule> T getModule(Class<T> module) {
        return (T) moduleMap.get(module);
    }

    public Plugin registerModule(PluginModule... modules) {
        if (modules != null && modules.length >= 1) {
            for (PluginModule module : modules) {
                moduleMap.put(module.getClass(), module);
            }
        }
        return this;
    }

    public void finish() {
        getLogger().info("Registered " + moduleMap.size() + " module(s).");
    }

    public Map<Class<?>, PluginModule> getModules() {
        return moduleMap;
    }

    private FileConfiguration loadConfiguration(File folder, String child) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, child);

        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            FileUtilities.createFile(file, child);
            try {
                if (file.createNewFile()) {
                    return YamlConfiguration.loadConfiguration(file);
                }
            } catch (IOException ignored) { }
            return new YamlConfiguration();
        }
    }

    public FileConfiguration getGeneratorConfiguration() {
        return generators;
    }
    public FileConfiguration getSettings() {
        return settings;
    }

    public FileConfiguration getMessages() {
        return messages;
    }


    public boolean isPluginEnabled(String pluginName) {
        return getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public List<Generator> getGenerators() {
        return generatorList;
    }

    public Generator getGeneratorByLocation(Location location) {
        int x2 = location.getBlockX();
        int y2 = location.getBlockY();
        int z2 = location.getBlockZ();

        for (Generator sign : generatorList) {
            int x1 = (int)sign.getX();
            int y1 = (int)sign.getY();
            int z1 = (int)sign.getZ();

            if (x1 == x2 && y1 == y2 && z1 == z2) {
                return sign;
            }
        }
        return null;
    }

    public PluginStorage<UUID, GenPlayer> getPlayers() {
        return playerStorage;
    }

    public GenPlayer getGamePlayer(Player player) {
        return playerStorage.get(player.getUniqueId(), (g) -> new GenPlayer(player));
    }
}