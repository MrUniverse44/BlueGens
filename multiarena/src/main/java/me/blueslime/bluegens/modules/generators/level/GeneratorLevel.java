package me.blueslime.bluegens.modules.generators.level;

import me.blueslime.bluegens.modules.generators.drop.GeneratorDrop;
import me.blueslime.utilitiesapi.item.ItemWrapper;
import me.blueslime.utilitiesapi.text.TextUtilities;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GeneratorLevel {
    private boolean corruptionPhase;
    private double corruptionChance;
    private int corruptionCost;

    private String nextId;
    private int nextCost;

    private int spawnRate;
    private final String id;

    private ItemWrapper item;

    private String displayName;

    private final List<GeneratorDrop> dropList = new ArrayList<>();

    public GeneratorLevel(ConfigurationSection configuration, String id) {
        this.id = id;

        loadItems(configuration);
    }

    private void loadItems(ConfigurationSection configuration) {
        this.displayName = configuration.getString("display-name", id.replace("_", " ").replace("-", " "));
        this.corruptionPhase = configuration.getBoolean("corrupt.enabled", true);
        this.corruptionChance = configuration.getDouble("corrupt.chance", 0.35);
        this.corruptionCost = configuration.getInt("corrupt.repair-cost", 4500);

        this.nextId = configuration.getString("upgrade.next", "MAX_LEVEL");
        this.nextCost = configuration.getInt("upgrade.cost", 5000);

        this.spawnRate = configuration.getInt("spawn-rate", 20);
        this.item = ItemWrapper.fromData(configuration, "item");

        this.item.addStringNBT(
            "bluegens-generator," + id
        );

        ConfigurationSection section = configuration.getConfigurationSection("drops");

        if (section != null) {
            List<String> keys = new ArrayList<>(section.getKeys(false));

            for (String key : keys) {
                dropList.add(
                    new GeneratorDrop(
                        section.getDouble(key + ".chance", 100),
                        section.getInt(key + ".price", 400),
                        ItemWrapper.fromData(section.getConfigurationSection(key))
                    )
                );
            }
        }
    }

    public String getDisplayName() {
        return TextUtilities.colorize(displayName != null ? displayName : id);
    }

    public int getSpawnRate() {
        return spawnRate;
    }

    public String getId() {
        return id;
    }

    public String getNextLevel() {
        return nextId;
    }

    public List<GeneratorDrop> getDroppedItems() {
        return dropList;
    }

    public boolean isCorruptionPhase() {
        return corruptionPhase;
    }

    public double getCorruptionChance() {
        return corruptionChance;
    }

    public int getCorruptionCost() {
        return corruptionCost;
    }

    public ItemWrapper getItem() {
        return item;
    }

    public ItemStack getItemStack() {
        return item.getItem();
    }

    public int getNextCost() {
        return nextCost;
    }
}
