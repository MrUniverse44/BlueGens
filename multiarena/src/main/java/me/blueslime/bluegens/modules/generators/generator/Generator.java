package me.blueslime.bluegens.modules.generators.generator;

import me.blueslime.bluegens.modules.generators.level.GeneratorLevel;
import me.blueslime.bluegens.modules.generators.location.Coordinate;
import me.blueslime.bluegens.modules.storage.player.GenPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Generator {
    private final Coordinate coordinate;
    private GeneratorLevel level;

    private boolean corruption;
    private final String owner;

    private Generator(String owner, GeneratorLevel level, String world, int x, int y, int z, boolean corruption) {
        this.coordinate = Coordinate.of(world, x, y, z);
        this.corruption = corruption;
        this.level = level;
        this.owner = owner;
    }

    public boolean isOwner(Player player) {
        return owner.equalsIgnoreCase(player.getUniqueId().toString().replace("-", ""));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOwner(GenPlayer player) {
        return owner.equalsIgnoreCase(player.getId());
    }

    public Block getBlock() {
        return coordinate.getBlock();
    }

    public Block getUpperBlock() {
        return coordinate.getUpperBlock();
    }

    public boolean compare(Block block) {
        return coordinate.compare(block);
    }

    public double getX() {
        return coordinate.getX();
    }

    public double getY() {
        return coordinate.getY();
    }

    public double getZ() {
        return coordinate.getZ();
    }

    public static Generator builder(String owner, GeneratorLevel level, String world, int x, int y, int z) {
        return builder(owner, level, world, x, y, z, false);
    }

    public static Generator builder(String owner, GeneratorLevel level, String world, int x, int y, int z, boolean corrupt) {
        return new Generator(owner, level, world, x, y, z, corrupt);
    }

    public static Generator builder(String owner, GeneratorLevel level, Block block) {
        return builder(owner, level, block, false);
    }

    public static Generator builder(Player owner, GeneratorLevel level, String world, int x, int y, int z) {
        return builder(owner, level, world, x, y, z, false);
    }

    public static Generator builder(Player owner, GeneratorLevel level, String world, int x, int y, int z, boolean corrupt) {
        return new Generator(owner.getUniqueId().toString().replace("-", ""), level, world, x, y, z, corrupt);
    }

    public static Generator builder(Player owner, GeneratorLevel level, Block block) {
        return builder(owner.getUniqueId().toString().replace("-", ""), level, block, false);
    }

    public static Generator builder(GenPlayer owner, GeneratorLevel level, String world, int x, int y, int z) {
        return builder(owner, level, world, x, y, z, false);
    }

    public static Generator builder(GenPlayer owner, GeneratorLevel level, String world, int x, int y, int z, boolean corrupt) {
        return new Generator(owner.getId(), level, world, x, y, z, corrupt);
    }

    public static Generator builder(GenPlayer owner, GeneratorLevel level, Block block) {
        return builder(owner.getId(), level, block, false);
    }

    public static Generator builder(String owner, GeneratorLevel level, Block block, boolean corrupt) {
        Location location = block.getLocation();

        if (location.getWorld() == null) {
            return null;
        }

        return new Generator(
            owner,
            level,
            location.getWorld().getName(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            corrupt
        );
    }

    public GeneratorLevel getLevel() {
        return level;
    }

    public void setLevel(GeneratorLevel level) {
        this.level = level;
    }

    public boolean isCorrupted() {
        return corruption;
    }

    public void setCorruption(boolean corruption) {
        this.corruption = corruption;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getCode() {
        return coordinate.getCode();
    }

    public String getOwnerId() {
        return owner;
    }
}
