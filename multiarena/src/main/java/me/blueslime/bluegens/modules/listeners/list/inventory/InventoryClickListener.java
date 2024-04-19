package me.blueslime.bluegens.modules.listeners.list.inventory;

import me.blueslime.bluegens.BlueGens;
import me.blueslime.bluegens.modules.listeners.api.BlueArmorPart;
import me.blueslime.bluegens.modules.listeners.list.PluginListener;
import me.blueslime.bluegens.modules.utils.PluginUtilities;
import me.blueslime.bluegens.modules.utils.generator.GeneratorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class InventoryClickListener extends PluginListener {
    public InventoryClickListener(BlueGens plugin) {
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

    @EventHandler
    public void on(InventoryClickEvent event) {
        boolean shift = false;
        boolean numberKey = false;
        if (event.isCancelled()) {
            return;
        }

        if (event.getAction() == InventoryAction.NOTHING) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT)) {
            shift = true;
        }
        if (event.getClick().equals(ClickType.NUMBER_KEY)) {
            numberKey = true;
        }

        if (
            event.getSlotType() != InventoryType.SlotType.ARMOR &&
            event.getSlotType() != InventoryType.SlotType.QUICKBAR &&
            event.getSlotType() != InventoryType.SlotType.CONTAINER
        ) {
            return;
        }

        if (event.getClickedInventory() != null) {
            String inventoryType = event.getClickedInventory().getType().toString().toLowerCase(Locale.ENGLISH);
            if (inventoryType.contains("barrel") || inventoryType.contains("chest") || inventoryType.contains("shulker") || inventoryType.contains("creative")) {
                return;
            }
        }

        if (
            !event.getInventory().getType().equals(InventoryType.CRAFTING) &&
            !event.getInventory().getType().equals(InventoryType.PLAYER)
        ) {
            return;
        }

        int armorPart = BlueArmorPart.ofItem(shift ? event.getCurrentItem() : event.getCursor());

        if (shift){
            if (armorPart != -1){
                ItemStack itemStack = event.getCurrentItem();

                if (GeneratorUtils.isDrop(itemStack) || GeneratorUtils.isDrop(event.getCursor())) {
                    event.setCancelled(true);
                }
            }
        } else {
            ItemStack newArmorPiece = event.getCursor();
            ItemStack oldArmorPiece = event.getCurrentItem();

            if (numberKey && event.getClickedInventory() != null) {
                if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                    ItemStack hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());
                    if (!PluginUtilities.isAirOrNull(hotbarItem)) {
                        armorPart = BlueArmorPart.ofItem(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = event.getClickedInventory().getItem(event.getSlot());
                    } else {
                        armorPart = BlueArmorPart.ofItem(
                            !PluginUtilities.isAirOrNull(event.getCurrentItem()) ?
                                event.getCurrentItem() :
                                event.getCursor()
                        );
                    }
                }
            } else {
                if (PluginUtilities.isAirOrNull(event.getCursor()) && !PluginUtilities.isAirOrNull(event.getCurrentItem())) {
                    armorPart = BlueArmorPart.ofItem(event.getCurrentItem());
                }
            }
            if (armorPart != -1 && event.getRawSlot() == armorPart) {
                if (GeneratorUtils.isDrop(oldArmorPiece) || GeneratorUtils.isDrop(newArmorPiece)) {
                    event.setCancelled(true);
                }
            }
        }

    }
}
