package io.github.tubes.model;

public abstract class Entity {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int minDamage;
    protected int maxDamage;
    protected int defense;
    public boolean dead = false;


    public Entity(String name, int maxHp, int minDamage, int maxDamage, int defense) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.defense = defense;
    }

    public void takeDamage(int damageAmount) {
        this.hp -= damageAmount;
        if (this.hp <= 0) {
            this.hp = 0;
            this.dead = true;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; if (this.hp > this.maxHp) { this.hp = this.maxHp; } }
    public int getMaxHp() { return maxHp; }
    public int getMinDamage() { return minDamage; }
    public int getMaxDamage() { return maxDamage; }
    public int getDefense() { return defense; }
}
