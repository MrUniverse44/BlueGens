package me.blueslime.bluegens.modules.listeners.list.custom;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.listeners.api.*;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import me.blueslime.bluegens.modules.utils.generator.GeneratorUtils;
import org.bukkit.event.EventHandler;

public class SoundParticleListener extends PluginListener {
    public SoundParticleListener(BlueGens plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void reload() {

    }

    @EventHandler
    public void on(GeneratorCorruptEvent event) {
        GeneratorUtils.spawnParticles(event.getCurrentGenerator(), plugin.getSettings().getStringList("particles.on-corrupt"));
    }

    @EventHandler
    public void on(GeneratorPlaceEvent event) {
        GeneratorUtils.spawnParticles(event.getCurrentGenerator(), plugin.getSettings().getStringList("particles.on-place"));
    }

    @EventHandler
    public void on(GeneratorBreakEvent event) {
        GeneratorUtils.spawnParticles(event.getCurrentGenerator(), plugin.getSettings().getStringList("particles.on-remove"));
    }

    @EventHandler
    public void on(GeneratorRepairEvent event) {
        GeneratorUtils.spawnParticles(event.getCurrentGenerator(), plugin.getSettings().getStringList("particles.on-repair"));
    }

    @EventHandler
    public void on(GeneratorUpgradeEvent event) {
        GeneratorUtils.spawnParticles(event.getCurrentGenerator(), plugin.getSettings().getStringList("particles.on-upgrade"));
    }
}
