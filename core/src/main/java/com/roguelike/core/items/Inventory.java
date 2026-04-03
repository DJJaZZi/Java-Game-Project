package com.roguelike.core.items;

/**
 * Inventory - Manages items (Composite Pattern)
 */
public class Inventory {
    private java.util.List<Item> items;
    private int capacity;
    private int currentItemIndex;

    public Inventory(int capacity) {
        this.items = new java.util.ArrayList<>();
        this.capacity = capacity;
        this.currentItemIndex = -1;
    }

    /**
     * Add item to inventory
     */
    public boolean addItem(Item item) {
        if (items.size() < capacity) {
            items.add(item);
            System.out.println("[Inventory] Added: " + item.getName());
            return true;
        }
        System.out.println("[Inventory] Inventory full!");
        return false;
    }

    /**
     * Remove item
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Use current item
     */
    public void useCurrentItem() {
        if (currentItemIndex >= 0 && currentItemIndex < items.size()) {
            Item item = items.get(currentItemIndex);
            item.use();

            if (item.isConsumable()) {
                items.remove(currentItemIndex);
            }
        }
    }

    /**
     * Drop item
     */
    public Item dropItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.remove(index);
        }
        return null;
    }

    /**
     * Get items
     */
    public java.util.List<Item> getItems() {
        return new java.util.ArrayList<>(items);
    }

    /**
     * Get current item
     */
    public Item getCurrentItem() {
        if (currentItemIndex >= 0 && currentItemIndex < items.size()) {
            return items.get(currentItemIndex);
        }
        return null;
    }

    /**
     * Switch to next item
     */
    public void switchToNextItem() {
        if (!items.isEmpty()) {
            currentItemIndex = (currentItemIndex + 1) % items.size();
            System.out.println("[Inventory] Selected: " + getCurrentItem().getName());
        }
    }

    /**
     * Get inventory info string
     */
    public String getInfoString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY (").append(items.size()).append("/").append(capacity).append(") ===\n");

        for (int i = 0; i < items.size(); i++) {
            String marker = i == currentItemIndex ? "> " : "  ";
            sb.append(marker).append(i).append(". ").append(items.get(i).getInfoString()).append("\n");
        }

        return sb.toString();
    }

    // Getters
    public int getSize() {
        return items.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return items.size() >= capacity;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
