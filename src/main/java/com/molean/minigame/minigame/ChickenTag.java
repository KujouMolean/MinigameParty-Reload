package com.molean.minigame.minigame;

import com.molean.minigame.Minigame;
import com.molean.minigame.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ChickenTag extends Minigame {

    private final Random r = new Random();
    private final BossBar chickenBar;
    private Chicken chicken;
    private Player holder;

    public ChickenTag() {
        super("ChickenTag");
        chickenBar = Utils.createBar("爆炸鸡在你头上,赶紧交给其他玩家", BarColor.BLUE, BarStyle.SOLID);

    }

    @Override
    public void init(List<Player> players) {
        super.init(players);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.GRASS_BLOCK);
            }
        }
        setRestrict(new Location(world, x - 1, y - 1, z - 1), new Location(world, x + 65, y + 65, z + 65));
    }

    @Override
    public void start() {
        super.start();
        Utils.runTaskAsynchronously(() -> {
            Utils.delay(100L);
            for (int i = 0; isInGame() && i < 40; i++) {
                Utils.runTask(() -> {
                    BossBar bossBar = Utils.createBar("爆炸鸡即将爆炸", BarColor.BLUE, BarStyle.SOLID);
                    players.keySet().forEach(bossBar::addPlayer);
                    Utils.setBarAutoProgress(bossBar, 300, bossBar::removeAll);
                    List<Player> playerList = getPlayerList();
                    holder = playerList.get(r.nextInt(playerList.size()));
                    chicken = (Chicken) world.spawnEntity(holder.getLocation(), EntityType.CHICKEN);
                    chicken.setAI(false);
                    chicken.setGlowing(true);
                    chicken.setCustomName("爆炸鸡");
                    holder.addPassenger(chicken);
                    chickenBar.addPlayer(holder);
                });
                Utils.delay(300L);
                Utils.runTask(() -> {
                    world.createExplosion(holder.getLocation(), 4.0F, false, false);
                    holder = null;
                    chicken.remove();
                    chickenBar.removeAll();
                });
                Utils.delay(20L);
                if (getPlayerList().size() == 0) {
                    break;
                }
            }
            stop();
        });
    }

    @Override
    public void setup(World world, int x, int y, int z) {
        super.setup(world, x, y, z);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.GRASS_BLOCK);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!entity.equals(chicken))
            return;
        event.setDamage(0);
        if (!damager.equals(holder))
            return;
        Collection<Entity> nearbyEntities = world.getNearbyEntities(holder.getLocation(), 2, 2, 2);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof Player) {
                Player temp = (Player) nearbyEntity;
                if (temp.equals(damager)) continue;
                if (getPlayerList().contains(temp)) {
                    holder.removePassenger(chicken);
                    holder = (Player) nearbyEntity;
                    holder.addPassenger(chicken);
                    chickenBar.removeAll();
                    chickenBar.addPlayer(holder);
                    break;
                }
            }

        }
    }

    @Override
    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(64), y + 3, z + r.nextInt(64));
    }

    @Override
    public boolean canDamage(Player player, EntityDamageEvent.DamageCause damageCause) {
        if (damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            return true;
        } else if (damageCause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            return true;
        } else {
            return super.canDamage(player, damageCause);
        }
    }
}
