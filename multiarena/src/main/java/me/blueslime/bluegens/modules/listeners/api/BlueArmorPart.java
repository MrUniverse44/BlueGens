package me.blueslime.bluegens.modules.listeners.api;

import me.blueslime.bluegens.modules.utils.PluginUtilities;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * @author Arnah
 * @since Jul 30, 2015
 */
public class BlueArmorPart{
    public final static int HELMET = 5;
    public final static int CHESTPLATE = 6;
    public final static int LEGGINGS = 7;
    public final static int BOOTS = 8;
    public final static int UNKNOWN = -1;

    /**
     * Find a ArmorPart from a specified ItemStack.
     *
     * @param itemStack The ItemStack to parse the type of.
     * @return Integer Result, or -1 if not found.
     */
    public static int ofItem(final ItemStack itemStack){
        if (PluginUtilities.isAirOrNull(itemStack)) {
            return UNKNOWN;
        }

        String type = itemStack.getType().toString();

        type = type.toLowerCase(Locale.ENGLISH);

        if (type.contains("helmet") || type.contains("skull") || type.contains("head")) {
            return HELMET;
        }

        if (type.contains("chestplate") || type.contains("elytra")) {
            return CHESTPLATE;
        }

        if (type.contains("leggings")) {
            return LEGGINGS;
        }

        if (type.contains("boots")) {
            return BOOTS;
        }

        return UNKNOWN;
    }
}
