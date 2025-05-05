package com.example.coinly;

/**
 * Model class representing a savings goal "pocket"
 */
public class Pocket {
    private String name;
    private double targetAmount;
    private double currentAmount;
    private int iconResourceId;
    private boolean isLocked;

    public Pocket(String name, double targetAmount, double currentAmount, int iconResourceId, boolean isLocked) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.iconResourceId = iconResourceId;
        this.isLocked = isLocked;
    }

    public String getName() {
        return name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public int getProgressPercentage() {
        return (int) (currentAmount / targetAmount * 100);
    }

    public String getFormattedTarget() {
        return "Php " + String.format("%,.2f", targetAmount);
    }
}