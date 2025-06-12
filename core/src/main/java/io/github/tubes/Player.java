package io.github.tubes;

import com.badlogic.gdx.math.MathUtils;

public class Player extends Entity {

    private int exp;
    private int maxExp;
    private int gold;

    public Player(String name, int maxHp, int minDamage, int maxDamage, int accuracy, int level) {
        super(name, maxHp, minDamage, maxDamage, accuracy, level);
        this.exp = 0;
        this.maxExp = calculateMaxExpForLevel(level);
        this.gold = 0;
    }


    public void addExp(int amount) {
        this.exp += amount;
        if (this.exp >= this.maxExp) {
            levelUp();
        }
    }


    private void levelUp() {
        int remainingExp = this.exp - this.maxExp;

        this.level++;
        this.exp = 0; // Reset EXP untuk level baru
        this.maxExp = calculateMaxExpForLevel(this.level);


        this.maxHp += MathUtils.random(5, 10); // Tambah max HP 5 sampai 10
        this.hp = this.maxHp; // Sembuhkan HP penuh saat level up
        this.minDamage += MathUtils.random(1, 3); // Tambah min damage 1 sampai 3
        this.maxDamage += MathUtils.random(2, 4); // Tambah max damage 2 sampai 4

        System.out.println("LEVEL UP! Anda mencapai level " + this.level);

        if (remainingExp > 0) {
            addExp(remainingExp);
        }
    }

    private int calculateMaxExpForLevel(int currentLevel) {
        return (int) (Math.pow(currentLevel, 2) * 10);
    }

    public int getExp() { return exp; }
    public int getMaxExp() { return maxExp; }
    public int getGold() { return gold; }
    public void addGold(int amount) { this.gold += amount; }

}
