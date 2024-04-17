package me.blueslime.bluegens.modules.listeners.list.block;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.generators.Generators;
import me.blueslime.bluegens.modules.generators.level.GeneratorLevel;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import me.blueslime.bluegens.modules.utils.reflect.PluginReflect;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import me.blueslime.utilitiesapi.item.nbt.ItemNBT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener extends PluginListener {
    public BlockPlaceListener(BlueGens plugin) {
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
    public void on(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack item = PluginReflect.getItemInHand(player);

        if (PluginReflect.isItemAir(item)) {
            return;
        }

        String nbt = ItemNBT.fromString(item, "bluegens-generator");

        if (nbt == null || nbt.isEmpty()) {
            return;
        }

        GeneratorLevel level = plugin.getModule(Generators.class).fetchLevel(nbt);

        if (level == null) {
            Sender.build(player).send(
                player,
                plugin.getMessages(),
                "messages.can-not-find-level-id",
                "&cGenerator level for this upgrade doesn't exists, please contact a server admin to get help!"
            );
            event.setCancelled(true);
            return;
        }

        event.setCancelled(
            plugin.getModule(Generators.class).placeGenerator(level, player, item, event.getBlock())
        );


    }
}
