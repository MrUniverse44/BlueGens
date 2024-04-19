package me.blueslime.bluegens.modules.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PluginUtilities {
    public static boolean isFloat(String floatString) {
        try {
            Float.parseFloat(floatString);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean isAirOrNull(ItemStack item){
        return item == null || item.getType().equals(Material.AIR);
    }

    public static boolean isDouble(String doubleValue) {
        try {
            Double.parseDouble(doubleValue);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static class FloatUtil {
        public static float converter(double number) {
            return (float) (number / 20.D);
        }

        public static int meters(double number) {
            return (int) number;
        }
    }
}
