package me.blueslime.bluegens.modules.generators.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Coordinate {
    private final String world;

    private final int x;
    private final int y;
    private final int z;

    private Coordinate(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Coordinate of(String world, int x, int y, int z) {
        return new Coordinate(world, x, y, z);
    }

    public static Coordinate of(World world, int x, int y, int z) {
        return of(world.getName(), x, y, z);
    }

    public static Coordinate of(Location location) {
        if (location.getWorld() == null) {
            return null;
        }
        return of(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Block getBlock() {
        if (world == null) {
            return null;
        }

        World world = Bukkit.getWorld(this.world);

        if (world == null) {
            return null;
        }

        return world.getBlockAt(
            new Location(
                world,
                x,
                y,
                z
            )
        );
    }

    public Block getUpperBlock() {
        if (world == null) {
            return null;
        }

        World world = Bukkit.getWorld(this.world);

        if (world == null) {
            return null;
        }

        return world.getBlockAt(
            new Location(
                world,
                x,
                y +1,
                z
            )
        );
    }

    public Location getHologramLocation() {
        if (world == null) {
            return null;
        }

        World world = Bukkit.getWorld(this.world);

        if (world == null) {
            return null;
        }

        return new Location(
            world,
            x + 0.5,
            y + 0.5,
            z + 0.5
        );
    }

    public Location getParticleLocation() {
        if (world == null) {
            return null;
        }

        World world = Bukkit.getWorld(this.world);

        if (world == null) {
            return null;
        }

        return new Location(
            world,
            x + 0.5,
            y + 0.8,
            z + 0.5
        );
    }

    public boolean compare(Block block) {
        Location location = block.getLocation();

        if (location.getWorld() == null) {
            return false;
        }

        return (
                location.getBlockX() == (int)getX() &&
                        location.getBlockY() == (int)getY() &&
                        location.getBlockZ() == (int)getZ() &&
                        location.getWorld().getName().equalsIgnoreCase(world)
        );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coordinate other = (Coordinate) obj;
        return world.equals(other.world) && x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        int result = world.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    public String getCode() {
        return world + "-" + x + "-" + y + "-" + z + "-generator";
    }
}
