package me.blueslime.bluegens.modules.listeners;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.PluginModule;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import me.blueslime.bluegens.modules.listeners.list.block.BlockBreakListener;
import me.blueslime.bluegens.modules.listeners.list.block.BlockDispenseArmorListener;
import me.blueslime.bluegens.modules.listeners.list.block.BlockPlaceListener;
import me.blueslime.bluegens.modules.listeners.list.custom.SoundParticleListener;
import me.blueslime.bluegens.modules.listeners.list.entity.EntityDamageListener;
import me.blueslime.bluegens.modules.listeners.list.entity.EntityExplodeListener;
import me.blueslime.bluegens.modules.listeners.list.inventory.InventoryClickListener;
import me.blueslime.bluegens.modules.listeners.list.inventory.InventoryDragListener;
import me.blueslime.bluegens.modules.listeners.list.player.PlayerInteractListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Listeners extends PluginModule {
    private final Map<Class<? extends PluginListener>, PluginListener> listenerMap = new ConcurrentHashMap<>();

    public Listeners(BlueGens plugin) {
        super(plugin);
    }

    public void registerListener(PluginListener... listeners) {
        for (PluginListener listener : listeners) {
            listenerMap.put(
                listener.getClass(),
                listener
            );
        }
    }

    @Override
    public void initialize() {
        registerListener(
            new BlockBreakListener(plugin),
            new BlockPlaceListener(plugin),
            new EntityDamageListener(plugin),
            new EntityExplodeListener(plugin),
            new PlayerInteractListener(plugin),
            new SoundParticleListener(plugin),
            new InventoryDragListener(plugin),
            new InventoryClickListener(plugin),
            new BlockDispenseArmorListener(plugin)
        );

        for (PluginListener listener : listenerMap.values()) {
            listener.initialize();
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void reload() {
        for (PluginListener listener : listenerMap.values()) {
            listener.reload();
        }
    }
}
