package me.blueslime.bluegens.modules.sells;

import org.bukkit.inventory.ItemStack;

public class PossibleItemSell {
    private ItemStack item;
    private double winningPrice;

    public PossibleItemSell(ItemStack item, double winningPrice) {
        this.item = item;
        this.winningPrice = winningPrice;
    }

    public void disable() {
        this.item = null;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setWinningPrice(double winningPrice) {
        this.winningPrice = winningPrice;
    }

    public boolean isDisabled() {
        return item == null;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getWinningPrice() {
        return winningPrice;
    }

    public int getAmount() {
        if (item == null) {
            return 0;
        }
        return item.getAmount();
    }
}
