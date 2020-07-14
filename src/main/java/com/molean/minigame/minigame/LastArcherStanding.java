package com.molean.minigame.minigame;

import com.molean.minigame.Minigame;
import com.molean.minigame.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class LastArcherStanding extends Minigame {
    final private Random r = new Random();
    public LastArcherStanding() {
        super("LastArcherStanding");
    }

    @Override
    public void setup(World world, int x, int y, int z) {
        super.setup(world, x, y, z);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.STONE);
            }
        }
    }

    @Override
    public void init(List<Player> players) {
        super.init(players);
        for (Player player : players) {
            player.getInventory().addItem(new ItemStack(Material.ARROW,16));
            player.getInventory().addItem(new ItemStack(Material.BOW));
        }
    }

    @Override
    public boolean canDamage(Player player, EntityDamageEvent.DamageCause damageCause) {
        if(damageCause.equals(EntityDamageEvent.DamageCause.PROJECTILE)){
            setMiniGameMode(player, MiniGameMode.spectator);
            rankList.addFirst(player);
        }

        return super.canDamage(player, damageCause);
    }

    @Override
    public void start() {
        super.start();
        Utils.runTaskAsynchronously(() -> {
            for (int i = 0; inGame; i++) {
                if (getPlayerList().size() == 1) {
                    rankList.addFirst(getPlayerList().get(0));
                    stop();
                }
                if (getPlayerList().size() == 0) {
                    stop();
                }
                Utils.delay(20L);
            }
        });
    }

    @Override
    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(64), y + 3, z + r.nextInt(64));
    }
}
