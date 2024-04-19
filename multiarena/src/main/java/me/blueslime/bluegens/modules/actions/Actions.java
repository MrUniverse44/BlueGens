package me.blueslime.bluegens.modules.actions;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.PluginModule;
import me.blueslime.bluegens.modules.actions.action.Action;
import me.blueslime.bluegens.modules.actions.type.ActionBarAction;
import me.blueslime.bluegens.modules.actions.type.ConsoleAction;
import me.blueslime.bluegens.modules.actions.type.MessageAction;
import me.blueslime.bluegens.modules.actions.type.TitlesAction;
import me.blueslime.bluegens.modules.utils.list.ReturnableArrayList;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Actions extends PluginModule {
    private final List<Action> externalActions = new ReturnableArrayList<>();
    private final List<Action> action = new ReturnableArrayList<>();

    public Actions(BlueGens plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        registerInternalAction(
            new MessageAction(),
            new ActionBarAction(),
            new TitlesAction(),
            new ConsoleAction()
        );
    }

    @Override
    public void shutdown() {
        action.clear();
    }

    @Override
    public void reload() {
        shutdown();
        initialize();
    }

    /**
     * Register actions to the plugin
     * @param actions to register
     */
    private void registerInternalAction(Action... actions) {
        action.addAll(Arrays.asList(actions));
    }

    /**
     * Register actions to the plugin
     * @param actions to register
     */
    public void registerAction(Action... actions) {
        externalActions.addAll(Arrays.asList(actions));
    }

    public List<Action> getActions() {
        return action;
    }

    public List<Action> getExternalActions() {
        return externalActions;
    }

    public void execute(List<String> actions) {
        execute(actions, null);
    }

    public void execute(List<String> actions, Player player) {
        List<Action> entireList = new ReturnableArrayList<Action>();

        entireList.addAll(externalActions);
        entireList.addAll(action);

        for (String param : actions) {
            fetch(entireList, player, param);
        }
    }

    private void fetch(List<Action> list, Player player, String param) {
        if (player == null) {
            return;
        }
        for (Action action : list) {
            if (action.isAction(param)) {
                action.execute(plugin, param, player);
                return;
            }
        }
        plugin.getLogger().info("'" + param + "' don't have an action, please see actions with /<command> actions");
    }
}
