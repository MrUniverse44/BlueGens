package me.blueslime.bluegens.modules.listeners.list.inventory;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.listeners.api.BlueArmorPart;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import me.blueslime.bluegens.modules.utils.generator.GeneratorUtils;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryDragListener extends PluginListener {
    public InventoryDragListener(BlueGens plugin) {
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
    public void on(InventoryDragEvent event) {
        int armorPart = BlueArmorPart.ofItem(event.getOldCursor());

        if (armorPart == -1) {
            return;
        }

        if (event.getRawSlots().isEmpty()) {
            return;
        }

        if (armorPart == event.getRawSlots().stream().findFirst().orElse(0)) {
            if (GeneratorUtils.isDrop(event.getOldCursor()) || GeneratorUtils.isDrop(event.getCursor())) {
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }
}
