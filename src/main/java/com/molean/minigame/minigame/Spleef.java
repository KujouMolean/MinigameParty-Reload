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


public class Spleef extends Minigame {

    private final Random r = new Random();

    public Spleef() {
        super("Spleef");
    }

    @Override
    public void setup(World world, int x, int y, int z) {
        super.setup(world, x, y, z);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.SNOW_BLOCK);
            }
        }
    }

    @Override
    public void init(List<Player> players) {
        super.init(players);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.SNOW_BLOCK);
            }
        }

        for (Player player : players) {
            ItemStack diamondShovel = new ItemStack(Material.DIAMOND_SHOVEL);
            player.getInventory().addItem(diamondShovel);
        }

        setRestrict(new Location(world, x - 1, y - 1, z - 1), new Location(world, x + 65, y + 65, z + 65));
    }

    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(64), y + 3, z + r.nextInt(64));
    }

    @Override
    public void start() {
        super.start();
        Utils.runTaskAsynchronously(() -> {
            for (int i = 0; inGame && i < 300; i++) {
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
    public boolean canBuild(Player player, Location location) {
        return Utils.inBox(new Location(world, x, y, z), new Location(world, x + 64, y, z + 64), location);
    }

    @Override
    public boolean canDamage(Player player, EntityDamageEvent.DamageCause damageCause) {
        if (damageCause.equals(EntityDamageEvent.DamageCause.PROJECTILE))
            return true;
        else return super.canDamage(player, damageCause);
    }
}
