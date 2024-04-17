package me.blueslime.bluegens.modules.commands;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.generators.Generators;
import me.blueslime.bluegens.modules.generators.level.GeneratorLevel;
import me.blueslime.bluegens.modules.utils.list.ReturnableArrayList;
import me.blueslime.utilitiesapi.commands.SimpleCommand;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import me.blueslime.utilitiesapi.item.ItemWrapper;
import me.blueslime.utilitiesapi.text.TextReplacer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class Command extends SimpleCommand<BlueGens> {
    public Command(BlueGens plugin, String command) {
        super(plugin, command);
    }

    @Override
    public void execute(Sender sender, String command, String[] arguments) {
        if (arguments.length == 0) {
            if (sender.isPlayer()) {
                sender.send(
                    sender.toPlayer(),
                    getPlugin().getMessages(),
                    "command.no-arguments",
                    "&a/bluegens buy &7to buy a new generator"
                );
                return;
            }
            sender.send(
                getPlugin().getMessages(),
                "command.no-arguments",
                "&a/bluegens buy &7to buy a new generator"
            );
            return;
        }
        if (arguments[0].equalsIgnoreCase("buy")) {
            Player targetPlayer;
            if (arguments.length >= 2) {
                if ((sender.isPlayer() && sender.hasPermission("bluegens.buy.other")) || !sender.isPlayer()) {
                    String playerName = arguments[1];
                    targetPlayer = getPlugin().getServer().getPlayer(playerName);
                    if (targetPlayer == null) {
                        if (sender.isPlayer()) {
                            sender.send(
                                sender.toPlayer(),
                                getPlugin().getMessages(),
                                "command.no-player-found",
                                "&cThis player is not online."
                            );
                            return;
                        }
                        sender.send(
                            getPlugin().getMessages(),
                            "command.no-player-found",
                            "&cThis player is not online."
                        );
                        return;
                    }
                } else {
                    if (sender.isPlayer()) {
                        targetPlayer = sender.toPlayer();
                    } else {
                        return;
                    }
                }
            } else {
                targetPlayer = sender.isPlayer() ? sender.toPlayer() : null;
            }
            if (targetPlayer == null) {
                if (sender.isPlayer()) {
                    sender.send(
                            sender.toPlayer(),
                            getPlugin().getMessages(),
                            "command.no-player-found",
                            "&cThis player is not online."
                    );
                    return;
                }
                sender.send(
                        getPlugin().getMessages(),
                        "command.no-player-found",
                        "&cThis player is not online."
                );
                return;
            }

            GeneratorLevel level = getPlugin().getModule(Generators.class).fetchLevel(
                getPlugin().getSettings().getString("settings.first-generator-level", "")
            );

            if (level == null) {
                sender.send(
                    getPlugin().getMessages(),
                    "messages.can-not-find-level-id",
                    "&cGenerator level for this upgrade doesn't exists, please contact a server admin to get help!"
                );
                return;
            }

            double price = getPlugin().getSettings().getDouble("settings.first-generator-level-price", 7500);

            if (getPlugin().getEconomy().has(targetPlayer, price)) {
                getPlugin().getEconomy().withdrawPlayer(targetPlayer, price);

                ItemWrapper original = level.getItem();
                ItemWrapper wrapper = original.copy();

                String displayName = level.getDisplayName();

                Economy economy = getPlugin().getEconomy();

                String uCost = economy != null ? economy.format(level.getNextCost()) : String.valueOf(level.getNextCost());
                String rCost = economy != null ? economy.format(level.getCorruptionCost()) : String.valueOf(level.getCorruptionCost());

                TextReplacer replacer = TextReplacer.builder()
                    .replace(
                        "<generator_displayname>",
                        displayName
                    ).replace(
                        "<generator_display_name>",
                        displayName
                    ).replace(
                        "<display_name>",
                        displayName
                    ).replace(
                        "<displayname>",
                        displayName
                    ).replace(
                        "<generator_time>",
                        String.valueOf(level.getSpawnRate())
                    ).replace(
                        "<time>",
                        String.valueOf(level.getSpawnRate())
                    ).replace(
                        "<generator_upgrade>",
                        uCost
                    ).replace(
                        "<upgrade>",
                        uCost
                    ).replace(
                        "<generator_repair>",
                        rCost
                    ).replace(
                        "<repair>",
                        rCost
                    );

                wrapper.setName(
                    replacer.apply(original.getName())
                );

                wrapper.setLore(
                    new ReturnableArrayList<>(original.getLore()).replace(
                        replacer::apply
                    )
                );

                targetPlayer.getInventory().addItem(wrapper.getItem());
            }
        }
    }
}
