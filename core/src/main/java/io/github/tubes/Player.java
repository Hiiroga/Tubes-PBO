package io.github.tubes;

public class Player extends Entity {
    // Variabel untuk buff sementara
    private int bonusDamage = 0;
    private int bonusDefense = 0;

    public Player() {
        super();
    }

    public Player(String name, int maxHp, int minDamage, int maxDamage, int defense) {
        super(name, maxHp, minDamage, maxDamage, defense);
    }

    // Override getter untuk menambahkan bonus
    @Override
    public int getMinDamage() {
        return super.minDamage + bonusDamage;
    }

    @Override
    public int getMaxDamage() {
        return super.maxDamage + bonusDamage;
    }

    @Override
    public int getDefense() {
        return super.defense + bonusDefense;
    }

    // Method untuk menerapkan buff
    public void applyBuff(int extraDamage, int extraDefense) {
        this.bonusDamage = extraDamage;
        this.bonusDefense = extraDefense;
    }

    // Method untuk menghapus buff setelah pertarungan
    public void clearBuffs() {
        this.bonusDamage = 0;
        this.bonusDefense = 0;
    }
}
