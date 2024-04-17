package me.blueslime.bluegens.modules.utils.reflect;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class PluginReflect {
    private static PluginReflect REFLECT = null;
    private boolean useMethods = true;
    private boolean handMethod = true;
    private boolean hands;

    @SuppressWarnings("ConstantValue")
    private PluginReflect() {
        try {
            Method method = PlayerInteractEvent.class.getDeclaredMethod("getHand");
            hands = method != null;
        } catch (Exception ignored) {
            hands = false;
        }
    }

    public static ItemStack getItemInHand(Player player) {
        return get().hand(player);
    }

    public static boolean isItemAir(ItemStack item) {
        if (item == null) {
            return true;
        }
        return item.getType() == Material.AIR;
    }

    public static boolean isCancelled(PlayerInteractEvent event) {
        return get().isCancel(event);
    }

    public static boolean doubleHand() {
        return get().checkHands();
    }

    @SuppressWarnings("deprecation")
    private boolean isCancel(PlayerInteractEvent event) {
        if (useMethods) {
            try {
                return event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY;
            } catch (Exception ignored) {
                useMethods = false;
            }
        }
        return event.isCancelled();
    }

    private boolean checkHands() {
        return hands;
    }

    @SuppressWarnings("deprecation")
    private ItemStack hand(Player player) {
        if (handMethod) {
            try {
                return player.getInventory().getItemInMainHand();
            } catch (Exception ignored) {
                handMethod = false;
            }
        }
        return player.getItemInHand();
    }

    private static PluginReflect get() {
        if (REFLECT == null) {
            REFLECT = new PluginReflect();
        }
        return REFLECT;
    }
}
