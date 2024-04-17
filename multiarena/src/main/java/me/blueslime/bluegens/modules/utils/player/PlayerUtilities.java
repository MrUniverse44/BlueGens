package me.blueslime.bluegens.modules.utils.player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerUtilities {
    public static boolean isInventoryFull(PlayerInventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || itemStack.getAmount() == 0) {
                return false;
            }
        }
        return true;
    }
}
