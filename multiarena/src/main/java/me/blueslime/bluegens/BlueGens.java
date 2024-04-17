package me.blueslime.bluegens;

import me.blueslime.bluegens.modules.metrics.Metrics;
import me.blueslime.bluegens.modules.plugin.Plugin;
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

        new Metrics(this, 21631);
    }

    @Override
    public void registerModules() {

    }

    @Override
    public void reload() {

    }

    public Economy getEconomy() {
        return economy;
    }
}
