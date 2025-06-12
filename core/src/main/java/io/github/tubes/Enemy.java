package io.github.tubes;

public class Enemy extends Entity {

    private final int expDrop;
    private final int goldDrop;

    public Enemy(String name, int maxHp, int minDamage, int maxDamage, int accuracy, int level) {
        super(name, maxHp, minDamage, maxDamage, accuracy, level);

        this.expDrop = level * 5;
        this.goldDrop = level * 10;
    }

    // --- Getters untuk Drop ---
    public int getExpDrop() { return expDrop; }
    public int getGoldDrop() { return goldDrop; }
}
