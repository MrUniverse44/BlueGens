package me.blueslime.bluegens.modules.generators.hologram;

import me.blueslime.bluegens.modules.generators.generator.Generator;
import me.blueslime.utilitiesapi.text.TextUtilities;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class GeneratorHologram {

    private static final double HOLOGRAM_DISTANCE = 0.3D;

    private final List<ArmorStand> armorStandList = new ArrayList<>();
    private final List<String> lineList = new ArrayList<>();
    private final String id;

    public GeneratorHologram(Generator generator, List<String> lines) {
        this.lineList.addAll(lines);
        this.id = generator.getCode();
        spawn(generator);
    }

    public void spawn(Generator generator) {
        int amount = 0;
        int size = lineList.size();

        Location location = generator.getCoordinate().getHologramLocation();

        for (String line : lineList) {
            Location lineLocation = location.clone();

            if (amount == 0) {
                double best = (HOLOGRAM_DISTANCE + 0.1) * size;
                lineLocation.setY(best);
            } else {
                double max = (HOLOGRAM_DISTANCE + 0.2) * size;
                double remover = HOLOGRAM_DISTANCE * amount;
                lineLocation.setY(max - remover);
            }

            ArmorStand armorStand = generate(line, lineLocation);

            armorStandList.add(armorStand);
            amount++;
        }
    }

    public String getId() {
        return id;
    }

    public void remove() {
        List<ArmorStand> armorStands = new ArrayList<>(armorStandList);

        for (ArmorStand armorStand : armorStands) {
            armorStand.remove();
        }

        lineList.clear();
        armorStandList.clear();
        armorStands.clear();
    }

    private ArmorStand generate(String line, Location location) {

        if (location.getWorld() == null) {
            return null;
        }

        Entity entity = location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);;
        ArmorStand armorStand = (ArmorStand)entity;

        armorStand.setCustomNameVisible(true);
        armorStand.setSmall(true);
        armorStand.setCustomName(
            TextUtilities.colorize(line)
        );
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setVisible(false);
        armorStand.setMarker(true);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);

        return armorStand;
    }

    public Location getLocation() {
        if (armorStandList.isEmpty()) {
            return null;
        }
        return armorStandList.get(0).getLocation();
    }

    public List<String> getLines() {
        return lineList;
    }

    public List<ArmorStand> getArmorStands() {
        return armorStandList;
    }
}
