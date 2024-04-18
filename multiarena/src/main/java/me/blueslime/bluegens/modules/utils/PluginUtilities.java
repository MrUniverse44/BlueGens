package me.blueslime.bluegens.modules.utils;

public class PluginUtilities {
    public static boolean isFloat(String floatString) {
        try {
            Float.parseFloat(floatString);
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
