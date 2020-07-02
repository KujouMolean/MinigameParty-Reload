package com.molean.minigame;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static boolean hasItem(Inventory inventory, Material material, int amount) {
        return inventory.contains(material, amount);
    }

    public static boolean takeItem(Inventory inventory, Material material, int amount) {
        if(!hasItem(inventory,material,amount))
            return false;
        ItemStack[] contents = inventory.getContents();
        for (ItemStack content : contents) {
            if(content!=null&&content.getType().equals(material)){
                if(content.getAmount()>=amount){
                    content.setAmount(content.getAmount()-amount);
                    break;
                } else{
                    amount-=content.getAmount();
                    content.setAmount(0);
                }
            }
        }
        return true;
    }
}
