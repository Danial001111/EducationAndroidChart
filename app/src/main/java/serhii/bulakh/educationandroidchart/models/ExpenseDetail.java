package serhii.bulakh.educationandroidchart.models;

public class ExpenseDetail {

    private String category;
    private float amount;
    private String comment;

    public ExpenseDetail(String category, float amount, String comment) {
        this.category = category;
        this.amount = amount;
        this.comment = comment;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
