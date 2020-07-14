package com.molean.minigame.minigame;

import com.molean.minigame.Minigame;
import com.molean.minigame.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class MineField extends Minigame {
    final private Random r = new Random();
    final private Map<Player, Integer> maxscore = new HashMap<>();

    public MineField() {
        super("MineField");
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

    @Override
    public void init(List<Player> players) {
        super.init(players);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.GRASS_BLOCK);
            }
        }
        for (int i = 10; i < 54; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y + 1, z + j);
                if (r.nextBoolean())
                    block.setType(Material.STONE_PRESSURE_PLATE);
                else
                    block.setType(Material.AIR);
            }
        }
        setRestrict(new Location(world, x - 1, y - 1, z - 1), new Location(world, x + 65, y + 65, z + 65));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (inGame && event.getAction().equals(Action.PHYSICAL) && players.get(event.getPlayer()).equals(MiniGameMode.gaming)) {
            world.spawnParticle(Particle.EXPLOSION_NORMAL, event.getClickedBlock().getLocation(), 1000);
            world.playSound(event.getClickedBlock().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 100, 0);
            Utils.runTaskLater(() -> {
                event.getPlayer().teleport(getSafeLanding());
            }, 10L);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        super.onPlayerMove(event);
        Player player = event.getPlayer();
        if (inGame && players.containsKey(player) && players.get(player).equals(MiniGameMode.gaming)) {
            Integer prev = maxscore.get(player);
            if (prev == null)
                prev = 0;
            int now = player.getLocation().getBlockX() - x;
            int modi = Math.min(Math.max(prev, now), 54);
            maxscore.put(player, modi);
            if (maxscore.get(player) >= 54) {
                setMiniGameMode(player, MiniGameMode.spectator);
                rankList.addEnd(player);
            }
        }
    }

    @Override
    public void start() {
        super.start();
        Utils.runTaskAsynchronously(() -> {
            for (int i = 0; inGame && i < 180; i++) {
                Utils.delay(20L);
                Map<String, Integer> map = new HashMap<>();
                players.forEach((player, miniGameMode) -> {
                    Integer integer = maxscore.get(player);
                    map.put(player.getName(), (int) (integer / 54.0 * 100.0));
                });
                setSoreborad(getName(), map);
                if (getPlayerList().size() == 0)
                    break;
            }
            List<Player> playerList = new ArrayList<>(getPlayerList());
            playerList.sort(Comparator.comparingInt(maxscore::get));
            Collections.reverse(playerList);
            for (Player player : playerList) {
                rankList.addEnd(player);
            }
            stop();

        });

    }

    @Override
    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(10), y + 3, z + r.nextInt(64), -90, 0);
    }

    @Override
    public boolean canRebornIfOut(Player player) {
        return true;
    }

}
