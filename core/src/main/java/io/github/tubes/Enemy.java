package io.github.tubes;

public class Enemy extends Entity {
    private final int goldDrop;

    public Enemy(String name, int maxHp, int minDamage, int maxDamage, int defense, int stageLevel) {
        super(name, maxHp, minDamage, maxDamage, defense);
        this.goldDrop = stageLevel * 30;
    }

    public int getGoldDrop() { return goldDrop; }
}
