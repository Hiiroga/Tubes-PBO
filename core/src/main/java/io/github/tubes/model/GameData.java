package io.github.tubes.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;

public class GameData {
    private static Preferences prefs;
    private static Json json = new Json();
    private static ArrayList<Player> party;
    private static int partyGold;
    private static Inventory inventory;
    private static ObjectMap<String, Item> itemDatabase;
    private static final String PREFS_NAME = "HeroesRisingData";
    private static final String KEY_MAX_LEVEL = "maxLevelUnlocked";
    private static final String KEY_PARTY_DATA = "partyData";
    private static final String KEY_PARTY_GOLD = "partyGold";
    private static final String KEY_INVENTORY = "inventoryData";

    public static void load() {
        initializeItemDatabase();
        prefs = Gdx.app.getPreferences(PREFS_NAME);

        String partyJson = prefs.getString(KEY_PARTY_DATA, null);
        if (partyJson != null && !partyJson.isEmpty()) {
            try {
                party = json.fromJson(ArrayList.class, Player.class, partyJson);
            } catch (Exception e) { createNewParty(); }
        } else { createNewParty(); }

        partyGold = prefs.getInteger(KEY_PARTY_GOLD, 0);

        String inventoryJson = prefs.getString(KEY_INVENTORY, null);
        if (inventoryJson != null && !inventoryJson.isEmpty()) {
            inventory = json.fromJson(Inventory.class, inventoryJson);
        } else {
            inventory = new Inventory();
        }
    }

    public static void save() {
        if (party != null) {
            prefs.putString(KEY_PARTY_DATA, json.toJson(party));
            prefs.putInteger(KEY_PARTY_GOLD, partyGold);
            prefs.putString(KEY_INVENTORY, json.toJson(inventory));
            prefs.flush();
            System.out.println("Game data saved.");
        }
    }

    private static void initializeItemDatabase() {
        itemDatabase = new ObjectMap<>();
        itemDatabase.put("Potion", new Item("Potion", "Restores 50 HP.", 10, 50));
    }

    public static Item getItem(String name) { return itemDatabase.get(name); }
    public static Inventory getInventory() { return inventory; }
    private static void createNewParty() {
        party = new ArrayList<>();
        party.add(new Player("Knight", 120, 15, 20, 10));
        party.add(new Player("Rogue", 85, 18, 25, 5));
        party.add(new Player("Mage", 80, 20, 30, 3));
    }
    public static ArrayList<Player> getParty() { return party; }
    public static int getGold() { return partyGold; }
    public static void addGold(int amount) { partyGold += amount; }
    public static int getMaxLevelUnlocked() { return prefs.getInteger(KEY_MAX_LEVEL, 1); }
    public static void unlockNextLevel(int completedStage) {
        int currentMaxLevel = getMaxLevelUnlocked();
        int nextLevel = completedStage + 1;
        if (nextLevel > currentMaxLevel) {
            prefs.putInteger(KEY_MAX_LEVEL, nextLevel);
        }
        save();
    }
}
