package me.blueslime.bluegens.modules.listeners.list.entity;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.generators.Generators;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener extends PluginListener {
    public EntityExplodeListener(BlueGens plugin) {
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

    @EventHandler(priority = EventPriority.HIGH)
    public void on(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.getModule(Generators.class).fetchGenerator(block) != null);
    }
}
