package me.blueslime.bluegens.modules.sells;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.PluginModule;
import me.blueslime.bluegens.modules.listeners.api.SellEvent;
import me.blueslime.bluegens.modules.utils.PluginUtilities;
import me.blueslime.utilitiesapi.item.nbt.ItemNBT;
import me.blueslime.utilitiesapi.tools.PluginTools;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Sells extends PluginModule {
    private final List<SellItem> itemList = new ArrayList<>();

    public Sells(BlueGens plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        itemList.addAll(
            SellItem.getItems(plugin.getSell())
        );
        if (plugin.getSell().getBoolean("enabled", true)) {
            new SellCommand(plugin).register(plugin);
        }
    }

    public SellResult execute(Player player) {
        SellResult result = new SellResult();

        List<PossibleItemSell> possibleItemSellList = new ArrayList<>();

        for (ItemStack item : player.getInventory().getStorageContents()) {

            if (PluginUtilities.isAirOrNull(item)) {
                continue;
            }

            String dropNBT = ItemNBT.fromString(item, "bluegens-drop-price");

            if (dropNBT != null && !dropNBT.isEmpty()) {
                plugin.getLogger().info("Possible item shop: " + dropNBT);
                if (PluginTools.isNumber(dropNBT) || PluginUtilities.isDouble(dropNBT)) {
                    plugin.getLogger().info("Added item with NBT");
                    double price = PluginTools.isNumber(dropNBT) ? Integer.parseInt(dropNBT) : Double.parseDouble(dropNBT);
                    int total = (int)price * item.getAmount();
                    PossibleItemSell possibleItem = new PossibleItemSell(item, total);
                    possibleItemSellList.add(possibleItem);
                    continue;
                }
            }

            for (SellItem sellItem : itemList) {
                PossibleItemSell possibleItem = sellItem.execute(item);
                if (possibleItem != null) {
                    if (possibleItem.isDisabled()) {
                        continue;
                    }
                    possibleItemSellList.add(possibleItem);
                }
            }
        }

        SellEvent event = new SellEvent(possibleItemSellList, player);

        callEvent(event);

        for (PossibleItemSell possibleItemSell : event.getSellList()) {
            if (possibleItemSell.isDisabled()) {
                continue;
            }

            ItemStack item = possibleItemSell.getItem().clone();

            player.getInventory().removeItem(
                item
            );

            result.addValue(possibleItemSell.getWinningPrice());
            result.addAmount(possibleItemSell.getAmount());
        }

        return result;
    }

    @Override
    public void shutdown() {
        itemList.clear();
    }

    @Override
    public void reload() {
        itemList.clear();
        itemList.addAll(
            SellItem.getItems(plugin.getSell())
        );
    }
}
