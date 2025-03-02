package serhii.bulakh.educationandroidchart.models;

public class Goal {
    private String name;
    private double targetAmount;
    private double currentAmount;

    public Goal(String name, double targetAmount, double currentAmount) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
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

    public void addAmount(double amount) {
        this.currentAmount += amount;
    }

    public double getRemainingAmount() {
        return targetAmount - currentAmount;
    }
}
