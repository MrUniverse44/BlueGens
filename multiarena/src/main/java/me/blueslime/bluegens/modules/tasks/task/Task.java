package me.blueslime.bluegens.modules.tasks.task;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.storage.player.GenPlayer;
import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.logging.Logger;

public abstract class Task extends BukkitRunnable {

    protected final BlueGens plugin;

    private boolean initialized = false;

    public Task(BlueGens plugin) {
        this.plugin = plugin;
    }

    public GenPlayer getGamePlayer(Player player) {
        return plugin.getGamePlayer(player);
    }

    public abstract void run();

    public void update() {

    }

    public void silencedCancellation() {
        initialized = false;
        try {
            cancel();
        } catch (Exception ignored) {
        }
    }

    @Override
    public synchronized boolean isCancelled() throws IllegalStateException {
        return PluginConsumer.ofUnchecked(
            super::isCancelled,
            e -> getLogs().info("Task is not running (This is not an error)"),
            !initialized
        );
    }

    public FileConfiguration getSettings() {
        return plugin.getSettings();
    }

    public FileConfiguration getMessages() {
        return plugin.getMessages();
    }

    public Server getServer() {
        return plugin.getServer();
    }

    public Collection<? extends Player> getOnlinePlayers() {
        return plugin.getServer().getOnlinePlayers();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void callEvent(Event event) {
        getServer().getPluginManager().callEvent(event);
    }

    public BlueGens getPlugin() {
        return plugin;
    }

    public Logger getLogs() {
        return plugin.getLogger();
    }

    public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
        initialized = true;
        return super.runTask(plugin);
    }

    public synchronized BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
        initialized = true;
        return super.runTaskAsynchronously(plugin);
    }

    public synchronized BukkitTask runTaskLater(long delay) throws IllegalArgumentException, IllegalStateException {
        initialized = true;
        return super.runTaskLater(plugin, delay);
    }

    public synchronized BukkitTask runTaskLaterAsynchronously(long delay) throws IllegalArgumentException, IllegalStateException {
        initialized = true;
        return super.runTaskLaterAsynchronously(plugin, delay);
    }

    public synchronized BukkitTask runTaskTimer(long delay, long period) throws IllegalArgumentException, IllegalStateException {
        initialized = true;
        return super.runTaskTimer(plugin, delay, period);
    }

    public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period) throws IllegalArgumentException, IllegalStateException {
        initialized = true;
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }
}