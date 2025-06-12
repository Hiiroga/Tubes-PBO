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

    /**
     * Menambahkan EXP dan memeriksa apakah pemain naik level.
     * @param amount Jumlah EXP yang didapat.
     */
    public void addExp(int amount) {
        this.exp += amount;
        if (this.exp >= this.maxExp) {
            levelUp();
        }
    }

    /**
     * Logika untuk menaikkan level pemain.
     */
    private void levelUp() {
        // Simpan sisa EXP
        int remainingExp = this.exp - this.maxExp;

        this.level++;
        this.exp = 0; // Reset EXP untuk level baru
        this.maxExp = calculateMaxExpForLevel(this.level);

        // Tingkatkan status
        this.maxHp += MathUtils.random(5, 10); // Tambah max HP 5 sampai 10
        this.hp = this.maxHp; // Sembuhkan HP penuh saat level up
        this.minDamage += MathUtils.random(1, 3); // Tambah min damage 1 sampai 3
        this.maxDamage += MathUtils.random(2, 4); // Tambah max damage 2 sampai 4

        System.out.println("LEVEL UP! Anda mencapai level " + this.level);

        // Jika sisa EXP masih cukup untuk level up lagi (rekursif)
        if (remainingExp > 0) {
            addExp(remainingExp);
        }
    }

    /**
     * Menghitung kebutuhan EXP untuk level berikutnya.
     * @param currentLevel Level saat ini.
     * @return Jumlah EXP yang dibutuhkan.
     */
    private int calculateMaxExpForLevel(int currentLevel) {
        // Formula sederhana: (level^2) * 10
        return (int) (Math.pow(currentLevel, 2) * 10);
    }

    // --- Getters and Setters untuk Player ---
    public int getExp() { return exp; }
    public int getMaxExp() { return maxExp; }
    public int getGold() { return gold; }
    public void addGold(int amount) { this.gold += amount; }

}
