package com.molean.minigame.minigame;

import com.molean.minigame.Minigame;
import com.molean.minigame.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SheepFreezy extends Minigame {
    final private Random r = new Random();

    public SheepFreezy() {
        super("SheepFreezy");
    }

    @Override
    public void setup(World world, int x, int y, int z) {
        super.setup(world, x, y, z);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.WHITE_WOOL);
            }
        }
    }

    @Override
    public void init(List<Player> players) {
        super.init(players);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.WHITE_WOOL);
            }
        }
        for (Player player : players) {
            ItemStack diamondShovel = new ItemStack(Material.SHEARS);
            player.getInventory().addItem(diamondShovel);
        }
        setRestrict(new Location(world, x - 1, y - 1, z - 1), new Location(world, x + 65, y + 65, z + 65));
    }

    @Override
    public void start() {
        super.start();
        Utils.runTaskAsynchronously(() -> {
            for (int i = 0; inGame && i < 60; i++) {
                Utils.delay(20L);

                Utils.runTask(() -> {
                    List<Player> playerList = new ArrayList<>(players.keySet());
                    playerList.sort(Comparator.comparingInt(this::getScore));
                    Map<String, Integer> map = new HashMap<>();
                    for (Player player : playerList) {
                        map.put(player.getName(), getScore(player));
                    }
                    setSoreborad(getName(), map);
                    Entity entity = world.spawnEntity(getSafeLanding(), EntityType.SHEEP);
                    entity.setCustomName("SheepFreezy");

                });
                if (getPlayerList().size() == 0)
                    break;
            }
            Utils.runTask(() -> {
                world.getNearbyEntities(new Location(world, x + 32, y, z + 32), 64, 64, 64).forEach(entity -> {
                    if (entity instanceof Item || "SheepFreezy".equalsIgnoreCase(entity.getCustomName())) {
                        entity.remove();
                    }
                });
            });

            List<Player> playerList = new ArrayList<>(players.keySet());
            playerList.sort(Comparator.comparingInt(this::getScore));
            for (Player player : playerList) {
                rankList.addEnd(player);
            }
            stop();
        });
    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        if ("SheepFreezy".equalsIgnoreCase(event.getEntity().getCustomName())) {
            event.getEntity().remove();
        }
    }


    @Override
    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(64), y + 3, z + r.nextInt(64));
    }

    private int getScore(Player player) {
        int score = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType().name().contains("WOOL")) {
                score += content.getAmount();
            }
        }
        return score;
    }
}
