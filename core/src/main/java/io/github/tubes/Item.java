package io.github.tubes;

public class Item {
    public String name;
    public String description;
    public int cost;
    public int healAmount;

    public Item() {}

    public Item(String name, String description, int cost, int healAmount) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.healAmount = healAmount;
    }
}
