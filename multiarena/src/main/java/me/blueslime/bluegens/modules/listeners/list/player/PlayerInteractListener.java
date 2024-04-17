package me.blueslime.bluegens.modules.listeners.list.player;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.generators.Generators;
import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.bluegens.modules.generators.level.GeneratorLevel;
import me.blueslime.bluegens.modules.listeners.api.GeneratorRepairEvent;
import me.blueslime.bluegens.modules.listeners.api.GeneratorUpgradeEvent;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import me.blueslime.bluegens.modules.storage.player.GenPlayer;
import me.blueslime.bluegens.modules.utils.player.PlayerUtilities;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import me.blueslime.utilitiesapi.item.ItemWrapper;
import me.blueslime.utilitiesapi.text.TextReplacer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Locale;

public class PlayerInteractListener extends PluginListener {
    public PlayerInteractListener(BlueGens plugin) {
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
    public void on(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        GenPlayer genPlayer = getGamePlayer(player);

        Generator generator = plugin.getModule(Generators.class).fetchGenerator(block);

        if (generator == null) {
            return;
        }

        String action = event.getAction().toString().toLowerCase(Locale.ENGLISH).replace("-", " ");

        if (action.contains("left")) {
            event.setCancelled(true);

            if (!generator.isOwner(genPlayer)) {
                Sender.build(player).send(
                    plugin.getMessages(),
                    "messages.not-owner",
                    "&cYou are not the owner of this generator",
                    TextReplacer.builder()
                        .replace("<player_displayname>", player.getDisplayName())
                        .replace("<player_name>", player.getName())
                        .replace("<player_uuid>", player.getUniqueId().toString())
                );
                return;
            }
            if (PlayerUtilities.isInventoryFull(player.getInventory())) {
                Sender.build(player).send(
                    plugin.getMessages(),
                    "messages.inventory-full-to-get-item",
                    "&4&lINVENTORY&f is &cfull&f you can't get your generator item while your inventory is full.",
                    TextReplacer.builder()
                        .replace("<player_displayname>", player.getDisplayName())
                        .replace("<player_name>", player.getName())
                        .replace("<player_uuid>", player.getUniqueId().toString())
                );
                return;
            }

            ItemWrapper item = generator.getLevel().getItem().copy().addStringNBT("bluegens-corrupt-phase," + generator.isCorrupted());

            player.getInventory().addItem(item.getItem());
            plugin.getModule(Generators.class).removeGenerator(generator, player);
            return;
        }

        // (upgrade)
        if (action.contains("right")) {
            if (player.isSneaking()) {
                return;
            }
            if (!generator.isOwner(genPlayer)) {
                Sender.build(player).send(
                    plugin.getMessages(),
                    "messages.not-owner",
                    "&cYou are not the owner of this generator",
                    TextReplacer.builder()
                        .replace("<player_displayname>", player.getDisplayName())
                        .replace("<player_name>", player.getName())
                        .replace("<player_uuid>", player.getUniqueId().toString())
                );
                return;
            }

            if (plugin.getEconomy() == null) {
                Sender.build(player).send(
                    player,
                    plugin.getMessages(),
                    "messages.economy-provider-error",
                    "&cCouldn't found economy provider"
                );
                return;
            }

            if (generator.isCorrupted()) {
                if (plugin.getEconomy().has(player, generator.getLevel().getCorruptionCost())) {
                    plugin.getEconomy().withdrawPlayer(player, generator.getLevel().getCorruptionCost());
                    generator.setCorruption(false);
                    plugin.getModule(Generators.class).removeHologram(player, generator);
                    Sender.build(player).send(
                        player,
                        plugin.getMessages(),
                        "messages.generator-repaired",
                        "&aThis generator has been fixed"
                    );
                    callEvent(new GeneratorRepairEvent(generator, player));
                } else {
                    Sender.build(player).send(
                        player,
                        plugin.getMessages(),
                        "messages.no-player-economy-repair",
                        "&6You don't have enough money to repair this generator. This repair cost: &f<generator_upgrade>&c, and you have &f<balance>",
                        TextReplacer.builder()
                            .replace("<player_displayname>", player.getDisplayName())
                            .replace("<player_name>", player.getName())
                            .replace("<generator_upgrade>", generator.getLevel().getCorruptionCost())
                            .replace("<balance>", String.valueOf(plugin.getEconomy().getBalance(player)))
                            .replace("<generator_upgrade_formatted>", plugin.getEconomy().format(generator.getLevel().getCorruptionCost()))
                            .replace("<balance_formatted>", String.valueOf(plugin.getEconomy().format(plugin.getEconomy().getBalance(player))))
                    );
                }
                return;
            }

            int currentSpawnRate = generator.getLevel().getSpawnRate();
            int nextLevelPrice = generator.getLevel().getNextCost();

            String nextLevelID = generator.getLevel().getNextLevel();

            GeneratorLevel nextLevel = plugin.getModule(Generators.class).fetchLevel(nextLevelID);

            if (nextLevel == null) {
                if (nextLevelID.equalsIgnoreCase("MAX_LEVEL")) {
                    Sender.build(player).send(
                        plugin.getMessages(),
                        "messages.already-in-max-level",
                        "&cYou are already in the max level for this generator!",
                        TextReplacer.builder()
                            .replace("<player_displayname>", player.getDisplayName())
                            .replace("<player_name>", player.getName())
                            .replace("<player_uuid>", player.getUniqueId().toString())
                    );
                } else {
                    Sender.build(player).send(
                        plugin.getMessages(),
                        "messages.can-not-find-level-id",
                        "&cGenerator level for this upgrade doesn't exists, please contact a server admin to get help!",
                        TextReplacer.builder()
                            .replace("<player_displayname>", player.getDisplayName())
                            .replace("<player_name>", player.getName())
                            .replace("<player_uuid>", player.getUniqueId().toString())
                    );
                }
                return;
            }

            if (!plugin.getEconomy().has(player, nextLevelPrice)) {
                Sender.build(player).send(
                    player,
                    plugin.getMessages(),
                    "messages.no-player-economy-upgrade",
                    "&6You don't have enough money to upgrade this generator. This upgrade cost: &f<generator_upgrade>&c, and you have &f<balance>",
                    TextReplacer.builder()
                        .replace("<player_displayname>", player.getDisplayName())
                        .replace("<player_name>", player.getName())
                        .replace("<generator_upgrade>", nextLevelPrice)
                        .replace("<balance>", String.valueOf(plugin.getEconomy().getBalance(player)))
                        .replace("<generator_upgrade_formatted>", plugin.getEconomy().format(nextLevelPrice))
                        .replace("<balance_formatted>", String.valueOf(plugin.getEconomy().format(plugin.getEconomy().getBalance(player))))
                );
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, nextLevelPrice);
            plugin.getModule(Generators.class).upgradeGenerator(generator, player, currentSpawnRate, nextLevel);
            callEvent(new GeneratorUpgradeEvent(generator, player));
            Sender.build(player).send(
                plugin.getMessages(),
                "messages.generator-updated",
                "&aUpdated generator level for this generator",
                TextReplacer.builder()
                    .replace("<player_displayname>", player.getDisplayName())
                    .replace("<player_name>", player.getName())
                    .replace("<player_uuid>", player.getUniqueId().toString())
            );
        }
    }
}
