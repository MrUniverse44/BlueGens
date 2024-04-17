package me.blueslime.bluegens.modules.listeners.list.block;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.generators.Generators;
import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener extends PluginListener {
    public BlockBreakListener(BlueGens plugin) {
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
    public void on(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Generator generator = plugin.getModule(Generators.class).fetchGenerator(event.getBlock());

        if (generator == null) {
            return;
        }

        event.setCancelled(true);
    }
}
