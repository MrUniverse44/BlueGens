package me.blueslime.bluegens.modules.sells;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.actions.Actions;
import me.blueslime.utilitiesapi.commands.SimpleCommand;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import me.blueslime.utilitiesapi.text.TextReplacer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SellCommand extends SimpleCommand<BlueGens> {
    public SellCommand(BlueGens plugin) {
        super(plugin, "sell");
    }

    @Override
    public void execute(Sender sender, String command, String[] arguments) {
        if (sender.isPlayer()) {
            Player player = sender.toPlayer();
            if (!sender.hasPermission("bluegens.sell")) {
                sender.send(
                    player,
                    getPlugin().getMessages(),
                    "command.no-permission",
                    "&cYou need permission &7<permission>&c to do this",
                    TextReplacer.builder().replace("<permission>", "bluegens.sell")
                );
                return;
            }

            Economy economy = getPlugin().getEconomy();

            if (economy == null) {
                return;
            }

            SellResult result = getPlugin().getModule(Sells.class).execute(player);

            String formatted = economy.format(result.getValue());

            TextReplacer replacer = TextReplacer.builder()
                .replace("<player_name>", player.getName())
                .replace("<player_displayname>", player.getDisplayName())
                .replace("<total_formatted>", formatted)
                .replace("<total>", String.valueOf(result));

            List<String> actions = new ArrayList<>();

            for (String action : getPlugin().getSell().getStringList("actions")) {
                actions.add(replacer.apply(action));
            }

            if (result.getValue() >= 0) {
                economy.depositPlayer(player, result.getValue());
            }

            getPlugin().getModule(Actions.class).execute(actions, player);

            return;
        }
        sender.send("&cCommand only for players!");
    }
}
