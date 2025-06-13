package io.github.tubes.model;

public class Player extends Entity {

    public Player(String name, int maxHp, int minDamage, int maxDamage, int defense) {
        super(name, maxHp, minDamage, maxDamage, defense);
    }

    @Override
    public int getMinDamage() {
        return super.minDamage ;
    }

    @Override
    public int getMaxDamage() {
        return super.maxDamage;
    }

    @Override
    public int getDefense() {
        return super.defense ;
    }

}
