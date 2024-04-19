package me.blueslime.bluegens.modules.sells;

public class SellResult {
    private double value;

    public SellResult(double value) {
        this.value = value;
    }

    public SellResult() {
        this(0);
    }

    public void addValue(double value) {
        this.value += value;
    }

    public double getValue() {
        return value;
    }
}
