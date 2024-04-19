package me.blueslime.bluegens.modules.sells;

import me.blueslime.bluegens.modules.utils.PluginUtilities;
import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SellItem {
    private final Material material;
    private final double price;

    private SellItem(ConfigurationSection configuration, String key) {
        this.material = PluginConsumer.ofUnchecked(
            () -> Material.valueOf(key.replace("-", "_").toUpperCase(Locale.ENGLISH)),
            e -> {},
            null
        );

        this.price = configuration.getDouble("worth." + key);
    }

    public Material getMaterial() {
        return material;
    }

    public double getPrice() {
        return price;
    }

    public static List<SellItem> getItems(ConfigurationSection configuration) {
        ConfigurationSection section = configuration.getConfigurationSection("worth");

        if (section == null) {
            return new ArrayList<>();
        }

        List<SellItem> items = new ArrayList<>();

        for (String key : section.getKeys(false)) {
            SellItem item = new SellItem(configuration, key);

            if (item.getMaterial() == null) {
                continue;
            }

            items.add(item);
        }

        return items;
    }

    public PossibleItemSell execute(ItemStack itemStack) {
        if (PluginUtilities.isAirOrNull(itemStack)) {
            return null;
        }

        if (material == itemStack.getType()) {
            double total = getPrice() * itemStack.getAmount();

            return new PossibleItemSell(itemStack, total);
        }
        return null;
    }
}
