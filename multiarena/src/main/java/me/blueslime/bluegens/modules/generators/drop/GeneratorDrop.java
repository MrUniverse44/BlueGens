package me.blueslime.bluegens.modules.generators.drop;

import me.blueslime.utilitiesapi.item.ItemWrapper;

public class GeneratorDrop {
    private final ItemWrapper wrapper;
    private final double chance;
    private final int price;

    public GeneratorDrop(double chance, int price, ItemWrapper wrapper) {
        this.wrapper = wrapper;
        this.chance = chance;
        this.price = price;

        if (wrapper != null) {
            this.wrapper.addStringNBT(
                "bluegens-drop-price," + price,
                "bluegens-drop-item,true"
            );
        }
    }

    public ItemWrapper getWrapper() {
        return wrapper;
    }

    public double getChance() {
        return chance;
    }

    public int getPrice() {
        return price;
    }
}
