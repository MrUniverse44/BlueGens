package me.blueslime.bluegens;

import me.blueslime.bluegens.modules.PluginModule;
import me.blueslime.bluegens.modules.actions.Actions;
import me.blueslime.bluegens.modules.commands.Command;
import me.blueslime.bluegens.modules.generators.Generators;
import me.blueslime.bluegens.modules.listeners.Listeners;
import me.blueslime.bluegens.modules.metrics.Metrics;
import me.blueslime.bluegens.modules.plugin.Plugin;
import me.blueslime.bluegens.modules.sells.Sells;
import me.blueslime.bluegens.modules.tasks.Tasks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class BlueGens extends Plugin {
    private Economy economy = null;

    @Override
    public void onEnable() {
        initialize(this);

        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);

        if (provider != null) {
            this.economy = provider.getProvider();
        } else {
            getLogger().severe("Couldn't load plugin, missing dependency: Any Economy plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new Command(this, "bluegens").register(this);
        new Metrics(this, 21631);
    }

    @Override
    public void onDisable() {
        shutdown();
    }

    @Override
    public void registerModules() {
        registerModule(
            new Generators(this),
            new Listeners(this),
            new Actions(this),
            new Sells(this),
            new Tasks(this)
        );
    }

    @Override
    public void reload() {
        build();

        for (PluginModule module : getModules().values()) {
            module.reload();
        }
    }

    public Economy getEconomy() {
        return economy;
    }
}
