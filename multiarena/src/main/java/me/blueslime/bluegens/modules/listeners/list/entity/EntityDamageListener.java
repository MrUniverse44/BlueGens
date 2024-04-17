package me.blueslime.bluegens.modules.listeners.list.entity;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener extends PluginListener {
    public EntityDamageListener(BlueGens plugin) {
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
    public void on(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand)event.getEntity();
            if (armorStand.isInvulnerable()) {
                event.setCancelled(true);
            }
        }
    }
}
