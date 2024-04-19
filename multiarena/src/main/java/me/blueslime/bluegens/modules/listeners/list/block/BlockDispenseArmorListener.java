package me.blueslime.bluegens.modules.listeners.list.block;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.listeners.api.BlueArmorPart;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import me.blueslime.bluegens.modules.utils.generator.GeneratorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;

public class BlockDispenseArmorListener extends PluginListener {
    public BlockDispenseArmorListener(BlueGens plugin) {
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
    public void on(BlockDispenseEvent event) {
        int armorType = BlueArmorPart.ofItem(event.getItem());

        if (armorType == -1) {
            return;
        }

        if (GeneratorUtils.isDrop(event.getItem())) {
            event.setCancelled(true);
        }
    }
}
