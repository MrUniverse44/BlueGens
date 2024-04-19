package me.blueslime.bluegens.modules.sells;

public class SellResult {
    private int itemAmount;
    private double value;

    public SellResult(int items, double value) {
        this.itemAmount = items;
        this.value = value;
    }

    public SellResult() {
        this(0, 0);
    }

    public void addValue(double value) {
        this.value += value;
    }

    public void addAmount(int itemAmount) {
        this.itemAmount += itemAmount;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public double getValue() {
        return value;
    }
}
