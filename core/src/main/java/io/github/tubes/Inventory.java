package io.github.tubes;

import com.badlogic.gdx.utils.ObjectMap;

public class Inventory {
    public ObjectMap<String, Integer> items;

    public Inventory() {
        items = new ObjectMap<>();
    }

    public int getQuantity(String itemName) {
        return items.get(itemName, 0);
    }

    public void addItem(String itemName, int quantity) {
        int currentQuantity = getQuantity(itemName);
        items.put(itemName, currentQuantity + quantity);
    }

    public boolean useItem(String itemName) {
        int currentQuantity = getQuantity(itemName);
        if (currentQuantity > 0) {
            items.put(itemName, currentQuantity - 1);
            return true;
        }
        return false;
    }
}
