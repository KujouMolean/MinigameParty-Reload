package com.molean.minigame;


import com.molean.minigame.events.MinigameFinishEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

import static com.molean.minigame.Utils.getMessage;

public class MinigameManager implements Listener {
    private static final MinigameManager manager = new MinigameManager();
    private static Minigame minigame = null;
    private static GameInstance gameInstance;

    private MinigameManager() {

    }

    public static MinigameManager getInstance() {
        return manager;
    }


    public void start(String game, Player player) {
        if (gameInstance != null) {
            player.sendMessage(getMessage("General.AlreadyHasGame"));
            return;
        }
        ArrayList<Material> materials = new ArrayList<>();
        materials.add(Material.CLOCK);
        if (!InventoryUtils.hasItem(player.getInventory(), Material.BLAZE_POWDER, 16)) {
            player.sendMessage(getMessage("General.NoEnoughMaterial"));
            return;
        }
        try {
            Class<?> aClass = Class.forName("com.molean.minigame.minigame." + game);
            minigame = ((Minigame) aClass.newInstance());
        } catch (ClassNotFoundException e) {
            player.sendMessage(getMessage("General.GameDoesNotExsit"));
            return;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            player.sendMessage(getMessage("General.InternalError"));
            return;
        }
        InventoryUtils.takeItem(player.getInventory(), Material.BLAZE_POWDER, 16);
        gameInstance = new GameInstance(minigame, player);
    }

    public void join(Player player) {
        ArrayList<Material> materials = new ArrayList<>();
        materials.add(Material.CLOCK);
        if (gameInstance == null) {
            player.sendMessage(getMessage("General.JoinNoGame"));
            return;
        }
        gameInstance.join(player);
    }

    public void left(Player player) {
        if (gameInstance == null) {
            player.sendMessage(getMessage("General.LeftNoGame"));
            return;
        }
        gameInstance.left(player);
    }

    private boolean inventoryCheck(PlayerInventory playerInventory, List<Material> excepts) {
        ItemStack[] contents = playerInventory.getContents();
        for (ItemStack content : contents) {
            if (content != null && !excepts.contains(content.getType())) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onMinigameFinish(MinigameFinishEvent event) {
        gameInstance = null;
    }
}
