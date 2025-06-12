package io.github.tubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameData {

    private static Preferences prefs;

    // Nama file untuk menyimpan data
    private static final String PREFS_NAME = "HeroesRisingData";
    // Kunci untuk menyimpan data level maksimal
    private static final String KEY_MAX_LEVEL = "maxLevelUnlocked";

    /**
     * Method ini harus dipanggil sekali saat game pertama kali dimulai.
     */
    public static void load() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    /**
     * Mengambil level tertinggi yang sudah terbuka.
     * Jika belum pernah main, defaultnya adalah 1.
     * @return Nomor level tertinggi yang bisa dimainkan.
     */
    public static int getMaxLevelUnlocked() {
        return prefs.getInteger(KEY_MAX_LEVEL, 1);
    }

    /**
     * Dipanggil setelah memenangkan sebuah stage.
     * Akan membuka level berikutnya jika perlu.
     * @param completedLevel Level yang baru saja diselesaikan.
     */
    public static void unlockNextLevel(int completedLevel) {
        int currentMaxLevel = getMaxLevelUnlocked();
        int nextLevel = completedLevel + 1;

        // Jika level berikutnya lebih tinggi dari rekor kita, simpan rekor baru.
        if (nextLevel > currentMaxLevel) {
            prefs.putInteger(KEY_MAX_LEVEL, nextLevel);
            prefs.flush(); // Perintah untuk menulis data ke file
            System.out.println("Level " + nextLevel + " Unlocked!");
        }
    }
}
