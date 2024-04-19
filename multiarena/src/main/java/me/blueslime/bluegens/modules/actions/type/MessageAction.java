package me.blueslime.bluegens.modules.actions.type;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.actions.action.Action;
import me.blueslime.messagehandler.types.messages.MessageHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageAction extends Action {
    public MessageAction() {
        super("[message]", "<message>", "message:");
    }



    /**
     * Execute action
     *
     * @param plugin    of the event
     * @param parameter text
     * @param players   players
     */
    @Override
    public void execute(BlueGens plugin, String parameter, List<Player> players) {
        if (players == null || players.isEmpty()) {
            return;
        }

        boolean placeholders = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        parameter = replace(parameter).replace("\\n", "\n");

        MessageHandler MESSAGES = MessageHandler.getInstance();

        for (Player player : players) {

            String message = parameter;

            if (placeholders) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }

            message = message.replace("\\n", "\n");

            MESSAGES.send(player, message);
        }
    }
}
