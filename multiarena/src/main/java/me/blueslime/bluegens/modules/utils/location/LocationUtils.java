package me.blueslime.bluegens.modules.utils.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {

    public static Location fromString(String text) {
        if(text.equalsIgnoreCase("NOTSET") || text.equalsIgnoreCase("NOT_SET") || text.isEmpty()) {
            return null;
        }

        String[] loc = text.split(",", 6);

        World w = Bukkit.getWorld(loc[0]);

        if (w != null) {
            double x = Double.parseDouble(loc[1]);
            double y = Double.parseDouble(loc[2]);
            double z = Double.parseDouble(loc[3]);
            float yaw = Float.parseFloat(loc[4]);
            float pitch = Float.parseFloat(loc[5]);

            return new Location(
                w,
                x,
                y,
                z,
                yaw,
                pitch
            );
        }

        return null;
    }

    public static String fromLocation(Location location) {
        if (location.getWorld() == null) {
            return "NOT_SET";
        }

        return location.getWorld().getName() + "," +
            location.getX() + "," +
            location.getY() + "," +
            location.getZ() + "," +
            location.getYaw() + "," +
            location.getPitch();
    }
}
