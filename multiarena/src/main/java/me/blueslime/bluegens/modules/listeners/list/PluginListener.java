package me.blueslime.bluegens.modules.listeners.list;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.bluegens.modules.storage.player.GenPlayer;
import me.blueslime.utilitiesapi.reflection.utils.storage.PluginStorage;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class PluginListener implements Listener {
    protected final BlueGens plugin;

    public PluginListener(BlueGens plugin) {
        this.plugin = plugin;
    }

    public abstract void initialize();
    public abstract void shutdown();
    public abstract void reload();

    protected FileConfiguration loadConfiguration(File folder, String child) {
        File file = new File(folder, child);
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            try {
                if (file.createNewFile()) {
                    return YamlConfiguration.loadConfiguration(file);
                }
            } catch (IOException ignored) { }
            return new YamlConfiguration();
        }
    }

    public FileConfiguration getGeneratorsConfiguration() {
        return plugin.getGeneratorConfiguration();
    }
    public FileConfiguration getSettings() {
        return plugin.getSettings();
    }
    public FileConfiguration getSell() {
        return plugin.getSell();
    }

    public FileConfiguration getMessages() {
        return plugin.getMessages();
    }

    public boolean isPluginEnabled(String pluginName) {
        return plugin.isPluginEnabled(pluginName);
    }

    public List<Generator> getGenerators() {
        return plugin.getGenerators();
    }

    public Generator getGeneratorByLocation(Location location) {
        return plugin.getGeneratorByLocation(location);
    }

    public PluginStorage<UUID, GenPlayer> getPlayers() {
        return plugin.getPlayers();
    }

    public GenPlayer getGamePlayer(Player player) {
        return plugin.getGamePlayer(player);
    }

    public void callEvent(Event event) {
        getServer().getPluginManager().callEvent(event);
    }

    public Server getServer() {
        return plugin.getServer();
    }

    public Collection<? extends Player> getOnlinePlayers() {
        return getServer().getOnlinePlayers();
    }
}
